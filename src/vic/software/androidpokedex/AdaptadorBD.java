package vic.software.androidpokedex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdaptadorBD {
	private static final String BASEDATOS = "S2DAMBD";
	private static final String TABLA = "pokemon";
	private static final int VERSION = 1;
	private static final String SQLCREAR1 = "create table pokemon (id integer primary key autoincrement, "
			+ "nombre text not null, tipo text not null, est_evo integer not null);";
	private static final String SQLCREAR2= "CREATE TABLE detalles (id_poke INTEGER PRIMARY KEY, descripcion TEXT, ratio NUMERIC, altura NUMERIC, peso NUMERIC);";

	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase bd;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, BASEDATOS, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(SQLCREAR1);
				db.execSQL(SQLCREAR2);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS POKEMON");
			db.execSQL("DROP TABLE IF EXISTS DETALLES");
			onCreate(db);
		}
	}

	public AdaptadorBD(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	// ---abrir la base de datos---
	public void abrirW() throws SQLException {
		bd = DBHelper.getWritableDatabase();
	}
	public void abrirR() throws SQLException {
		bd = DBHelper.getReadableDatabase();
	}

	// ---cerrar la base de datos---
	public void cerrar() {
		DBHelper.close();
	}

	public Cursor buscarDetallesPoke(long id) throws SQLException {
		Cursor mCursor = bd.rawQuery("select id, nombre, altura, peso, ratio, descripcion from pokemon, detalles where id=id_poke and id="+id,null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	public Cursor buscarPokemon(String nombre) throws SQLException {
		Cursor mCursor = bd.rawQuery("select * from "+TABLA+" where nombre like '"+nombre+"%'",null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	public long insertarPokemon() {
		ContentValues valores = new ContentValues();
		valores.put("id",0);
		valores.put("nombre", "kk");
		valores.put("tipo", "kk");
		valores.put("est_evo",0);
		return bd.insert(TABLA, null, valores);
	}
	

}