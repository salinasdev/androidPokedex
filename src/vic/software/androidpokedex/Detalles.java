package vic.software.androidpokedex;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Detalles extends AppCompatActivity {
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final Handler handler = new Handler(Looper.getMainLooper());
	private Future<?> query;
	private int generation;
	private boolean destroyed;
	private boolean started;
	private boolean autoPlayPending;
	private long id;
	private int soundResource;
	private MediaPlayer player;
	private Button sound;
	private ScrollView content;
	private Integer pendingScrollY;
	private View loading;
	private View state;

	@Override public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.AppTheme);
		super.onCreate(savedInstanceState);
		Object extra = getIntent().getExtras() == null ? null : getIntent().getExtras().get("id");
		if (!(extra instanceof Long) || (Long) extra < 1 || (Long) extra > 151) {
			invalidPokemon();
			return;
		}
		id = (Long) extra;
		autoPlayPending = savedInstanceState == null
				|| savedInstanceState.getBoolean("auto_play_pending", false);
		if (savedInstanceState != null) {
			pendingScrollY = savedInstanceState.getInt("detail_scroll_y", 0);
		}
		setContentView(R.layout.detalles);
		UiInsets.apply(this, findViewById(R.id.root));
		ViewCompat.setAccessibilityHeading(findViewById(R.id.detail_title), true);
		ViewCompat.setAccessibilityHeading(findViewById(R.id.pokemon_name), true);
		ViewCompat.setAccessibilityHeading(findViewById(R.id.physical_data_title), true);
		ViewCompat.setAccessibilityHeading(findViewById(R.id.about_pokemon_title), true);
		content = findViewById(R.id.detail_scroll);
		loading = findViewById(R.id.loading);
		state = findViewById(R.id.state);
		sound = findViewById(R.id.play_sound);
		sound.setOnClickListener(v -> playSound());
		findViewById(R.id.back).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
		findViewById(R.id.retry).setOnClickListener(v -> loadDetails());
		loadDetails();
	}

	private void invalidPokemon() {
		Toast.makeText(this, R.string.invalid_pokemon, Toast.LENGTH_LONG).show();
		finish();
	}

	private void loadDetails() {
		final int request = ++generation;
		if (query != null) query.cancel(true);
		loading.setVisibility(View.VISIBLE);
		content.setVisibility(View.GONE);
		state.setVisibility(View.GONE);
		final Context context = getApplicationContext();
		final long pokemonId = id;
		query = executor.submit(() -> {
			try {
				Pokemon pokemon;
				try (AdaptadorBD db = new AdaptadorBD(context)) {
					pokemon = db.buscarDetallesPoke(pokemonId);
				}
				handler.post(() -> {
					if (destroyed || request != generation) return;
					loading.setVisibility(View.GONE);
					if (pokemon == null) {
						invalidPokemon();
						return;
					}
					bind(pokemon);
				});
			} catch (Exception exception) {
				handler.post(() -> {
					if (destroyed || request != generation) return;
					loading.setVisibility(View.GONE);
					state.setVisibility(View.VISIBLE);
				});
			}
		});
	}

	private void bind(Pokemon pokemon) {
		String name = pokemon.getNombre();
		((TextView) findViewById(R.id.pokemon_number)).setText(getString(R.string.pokemon_number, id));
		((TextView) findViewById(R.id.pokemon_name)).setText(name);
		((TextView) findViewById(R.id.height)).setText(getString(R.string.height_value, pokemon.getAltura()));
		((TextView) findViewById(R.id.weight)).setText(getString(R.string.weight_value, pokemon.getPeso()));
		((TextView) findViewById(R.id.capture)).setText(getString(R.string.capture_value, pokemon.getRatio()));
		((TextView) findViewById(R.id.description)).setText(pokemon.getDescri());
		ImageView sprite = findViewById(R.id.sprite);
		int image = PokemonResources.sprite(id);
		sprite.setImageResource(image != 0 ? image : R.drawable.ic_pokeball);
		sprite.setContentDescription(getString(R.string.sprite_description, name));
		soundResource = PokemonResources.sound(id);
		sound.setEnabled(soundResource != 0);
		sound.setText(soundResource != 0 ? R.string.play_sound : R.string.sound_unavailable);
		sound.setContentDescription(getString(R.string.sound_description, name));
		content.setVisibility(View.VISIBLE);
		if (pendingScrollY != null) {
			// Restore only after the asynchronously loaded content has been laid out.
			content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				@Override public boolean onPreDraw() {
					content.getViewTreeObserver().removeOnPreDrawListener(this);
					if (!destroyed && pendingScrollY != null && content.getVisibility() == View.VISIBLE) {
						content.scrollTo(0, pendingScrollY);
						pendingScrollY = null;
					}
					return true;
				}
			});
		}
		playPendingSound();
	}

	private void playPendingSound() {
		if (autoPlayPending && started && content != null && content.getVisibility() == View.VISIBLE) {
			autoPlayPending = false;
			playSound();
		}
	}

	private void playSound() {
		if (!started || soundResource == 0 || player != null) return;
		sound.setEnabled(false);
		sound.setText(R.string.playing_sound);
		try {
			MediaPlayer next = new MediaPlayer();
			player = next;
			next.setAudioAttributes(new AudioAttributes.Builder()
					.setUsage(AudioAttributes.USAGE_MEDIA)
					.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build());
			next.setOnCompletionListener(mp -> {
				if (player == mp) releaseSound();
			});
			next.setOnErrorListener((mp, what, extra) -> {
				if (player == mp) soundFailed();
				return true;
			});
			next.setOnPreparedListener(mp -> {
				if (started && player == mp) {
					try { mp.start(); } catch (IllegalStateException exception) { soundFailed(); }
				} else if (player == mp) releaseSound();
			});
			try (AssetFileDescriptor asset = getResources().openRawResourceFd(soundResource)) {
				if (asset == null) throw new IllegalStateException("Audio resource unavailable");
				next.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
			}
			next.prepareAsync();
		} catch (Exception exception) {
			soundFailed();
		}
	}

	private void soundFailed() {
		releaseSound();
		Toast.makeText(this, R.string.sound_error, Toast.LENGTH_SHORT).show();
	}

	private void releaseSound() {
		MediaPlayer previous = player;
		player = null;
		if (previous != null) {
			previous.setOnPreparedListener(null);
			previous.setOnCompletionListener(null);
			previous.setOnErrorListener(null);
			previous.release();
		}
		if (sound != null) {
			sound.setEnabled(soundResource != 0);
			sound.setText(soundResource != 0 ? R.string.play_sound : R.string.sound_unavailable);
		}
	}

	@Override protected void onStart() {
		super.onStart();
		started = true;
		playPendingSound();
	}

	@Override protected void onSaveInstanceState(Bundle outState) {
		if (content != null) {
			outState.putInt("detail_scroll_y", pendingScrollY != null ? pendingScrollY : content.getScrollY());
			outState.putBoolean("auto_play_pending", autoPlayPending);
		}
		super.onSaveInstanceState(outState);
	}

	@Override protected void onStop() {
		started = false;
		autoPlayPending = false;
		releaseSound();
		super.onStop();
	}

	@Override protected void onDestroy() {
		destroyed = true;
		generation++;
		if (query != null) query.cancel(true);
		executor.shutdownNow();
		handler.removeCallbacksAndMessages(null);
		releaseSound();
		super.onDestroy();
	}
}
