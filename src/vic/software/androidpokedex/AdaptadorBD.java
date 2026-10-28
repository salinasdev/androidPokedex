package vic.software.androidpokedex;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.system.ErrnoException;
import android.system.Os;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/** Read-only bundled catalogue. Call from a worker thread and close after use. */
public final class AdaptadorBD implements AutoCloseable {
	// A separate versioned catalogue leaves the legacy S2DAMBD database untouched.
	static final String DATABASE_NAME = "pokedex-catalog-v1.db";
	private static final Object IMPORT_LOCK = new Object();
	private final Context context;
	private SQLiteDatabase database;

	public AdaptadorBD(Context context) {
		this.context = context.getApplicationContext();
	}

	private SQLiteDatabase open() {
		if (database != null && database.isOpen()) return database;
		synchronized (IMPORT_LOCK) {
			File destination = context.getDatabasePath(DATABASE_NAME);
			if (!destination.exists()) importCatalogue(destination);
			database = SQLiteDatabase.openDatabase(destination.getPath(), null,
					SQLiteDatabase.OPEN_READONLY);
			try {
				validate(database);
			} catch (RuntimeException exception) {
				database.close();
				database = null;
				throw exception;
			}
		}
		return database;
	}

	private void importCatalogue(File destination) {
		File directory = destination.getParentFile();
		if (directory == null || (!directory.isDirectory() && !directory.mkdirs())) {
			throw new SQLiteException("Cannot create catalogue directory");
		}
		File temporary = null;
		try {
			temporary = File.createTempFile("pokedex-", ".tmp", directory);
			try (InputStream input = context.getAssets().open("DBPOK");
				 FileOutputStream output = new FileOutputStream(temporary)) {
				byte[] buffer = new byte[8192];
				int length;
				while ((length = input.read(buffer)) != -1) {
					output.write(buffer, 0, length);
				}
				output.getFD().sync();
			}
			try (SQLiteDatabase candidate = SQLiteDatabase.openDatabase(temporary.getPath(),
					null, SQLiteDatabase.OPEN_READONLY)) {
				validate(candidate);
			}
			// Rename within the same directory: readers never see a partially copied DB.
			Os.rename(temporary.getPath(), destination.getPath());
		} catch (IOException | ErrnoException exception) {
			throw new SQLiteException("Cannot import Pokemon catalogue", exception);
		} finally {
			if (temporary != null && temporary.exists() && !temporary.delete()) {
				temporary.deleteOnExit();
			}
		}
	}

	private static void validate(SQLiteDatabase db) {
		// The original asset has user_version=0; it is not a Room schema.
		if (db.getVersion() != 0) throw new SQLiteException("Unexpected catalogue version");
		try (Cursor check = db.rawQuery("PRAGMA quick_check", null)) {
			if (!check.moveToFirst() || !"ok".equals(check.getString(0))) {
				throw new SQLiteException("Invalid catalogue integrity");
			}
		}
		try (Cursor rows = db.rawQuery("SELECT COUNT(*), MIN(id), MAX(id) "
				+ "FROM pokemon JOIN detalles ON pokemon.id = detalles.id_poke "
				+ "WHERE nombre IS NOT NULL AND descripcion IS NOT NULL "
				+ "AND altura IS NOT NULL AND peso IS NOT NULL AND ratio IS NOT NULL", null)) {
			if (!rows.moveToFirst() || rows.getInt(0) != 151
					|| rows.getInt(1) != 1 || rows.getInt(2) != 151) {
				throw new SQLiteException("Incomplete Pokemon catalogue");
			}
		}
	}

	public List<Pokemon> buscarPokemon(String nombre) {
		List<Pokemon> result = new ArrayList<>();
		try (Cursor cursor = open().rawQuery(
				"SELECT id, nombre FROM pokemon WHERE nombre LIKE ? ESCAPE '\\' ORDER BY id",
				new String[]{SearchFilter.prefix(nombre)})) {
			while (cursor.moveToNext()) {
				Pokemon pokemon = new Pokemon();
				pokemon.setId(cursor.getLong(0));
				pokemon.setNombre(cursor.getString(1));
				result.add(pokemon);
			}
		}
		return result;
	}

	public Pokemon buscarDetallesPoke(long id) {
		if (id < 1 || id > 151) return null;
		try (Cursor cursor = open().rawQuery(
				"SELECT id, nombre, altura, peso, ratio, descripcion FROM pokemon "
						+ "JOIN detalles ON pokemon.id = detalles.id_poke WHERE id = ?",
				new String[]{Long.toString(id)})) {
			if (!cursor.moveToFirst()) return null;
			Pokemon pokemon = new Pokemon();
			pokemon.setId(cursor.getLong(0));
			pokemon.setNombre(cursor.getString(1));
			pokemon.setAltura(cursor.getDouble(2));
			pokemon.setPeso(cursor.getDouble(3));
			pokemon.setRatio(cursor.getInt(4));
			pokemon.setDescri(cursor.getString(5));
			return pokemon;
		}
	}

	public void cerrar() {
		close();
	}

	@Override public void close() {
		if (database != null) {
			database.close();
			database = null;
		}
	}
}