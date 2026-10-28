package vic.software.androidpokedex;

import static org.junit.Assert.*;
import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CatalogueTest {
	private Context context;

	@Before public void cleanCatalogue() {
		context = InstrumentationRegistry.getInstrumentation().getTargetContext();
		context.deleteDatabase(AdaptadorBD.DATABASE_NAME);
	}

	@Test public void importsAndReopensAll151Pokemon() {
		try (AdaptadorBD db = new AdaptadorBD(context)) {
			List<Pokemon> pokemon = db.buscarPokemon("");
			assertEquals(151, pokemon.size());
			for (int i = 0; i < pokemon.size(); i++) {
				assertEquals(i + 1, pokemon.get(i).getId());
				Pokemon detail = db.buscarDetallesPoke(i + 1);
				assertNotNull(detail);
				assertFalse(detail.getDescri().isEmpty());
				assertTrue(detail.getAltura() > 0);
				assertTrue(detail.getPeso() > 0);
			}
		}
		try (AdaptadorBD reopened = new AdaptadorBD(context)) {
			assertEquals(151, reopened.buscarPokemon("").size());
		}
	}

	@Test public void searchIsLiteralAndCaseInsensitive() {
		try (AdaptadorBD db = new AdaptadorBD(context)) {
			assertEquals(25, db.buscarPokemon("  pika  ").get(0).getId());
			for (String input : new String[]{"'", "' OR 1=1 --", "%", "_", "\\", "no-existe"}) {
				assertTrue(input, db.buscarPokemon(input).isEmpty());
			}
			assertNull(db.buscarDetallesPoke(0));
			assertNull(db.buscarDetallesPoke(152));
		}
	}

	@Test public void everyPokemonHasSpriteAndSound() {
		for (int id = 1; id <= 151; id++) {
			assertEquals(context.getResources().getIdentifier("p" + id, "drawable", context.getPackageName()),
					PokemonResources.sprite(id));
			assertEquals(context.getResources().getIdentifier("s" + id, "raw", context.getPackageName()),
					PokemonResources.sound(id));
		}
		assertEquals(0, PokemonResources.sprite(-1));
		assertEquals(0, PokemonResources.sprite(Long.MAX_VALUE));
		assertEquals(0, PokemonResources.sound(152));
	}
}