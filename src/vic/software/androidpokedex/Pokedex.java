package vic.software.androidpokedex;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Pokedex extends AppCompatActivity {
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final Handler handler = new Handler(Looper.getMainLooper());
	private Future<?> query;
	private Runnable debounce;
	private int generation;
	private boolean destroyed;
	private EditText search;
	private RecyclerView list;
	private PokemonAdapter adapter;
	private LinearLayoutManager layoutManager;
	private View loading;
	private View state;
	private View retry;
	private TextView message;
	private TextView count;
	private Parcelable pendingScroll;

	@Override public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.AppTheme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		UiInsets.apply(this, findViewById(R.id.root));
		ViewCompat.setAccessibilityHeading(findViewById(R.id.catalogue_title), true);
		search = findViewById(R.id.search);
		list = findViewById(R.id.pokemon_list);
		loading = findViewById(R.id.loading);
		state = findViewById(R.id.state);
		retry = findViewById(R.id.retry);
		message = findViewById(R.id.state_message);
		count = findViewById(R.id.result_count);
		layoutManager = new LinearLayoutManager(this);
		list.setLayoutManager(layoutManager);
		adapter = new PokemonAdapter(id -> startActivity(
				new Intent(this, Detalles.class).putExtra("id", id)));
		list.setAdapter(adapter);
		// Restore before the watcher, so rotation causes just one query.
		if (savedInstanceState != null) {
			search.setText(savedInstanceState.getString("filter", ""));
			pendingScroll = savedInstanceState.getParcelable("scroll");
		}
		search.addTextChangedListener(new TextWatcher() {
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {
				pendingScroll = null;
				requestSearch(true);
			}
			@Override public void afterTextChanged(Editable s) { }
		});
		search.setOnEditorActionListener((v, action, event) -> {
			if (action != EditorInfo.IME_ACTION_SEARCH) return false;
			requestSearch(false);
			InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			if (keyboard != null) keyboard.hideSoftInputFromWindow(search.getWindowToken(), 0);
			search.clearFocus();
			return true;
		});
		findViewById(R.id.clear_search).setOnClickListener(v -> search.setText(""));
		retry.setOnClickListener(v -> requestSearch(false));
		requestSearch(false);
	}

	private void requestSearch(boolean delay) {
		final int request = ++generation;
		if (debounce != null) handler.removeCallbacks(debounce);
		if (query != null) query.cancel(true);
		final String filter = search.getText().toString();
		final Context context = getApplicationContext();
		loading.setVisibility(View.VISIBLE);
		state.setVisibility(View.GONE);
		list.setVisibility(View.INVISIBLE);
		count.setText(R.string.loading_list);
		debounce = () -> query = executor.submit(() -> {
			try {
				List<Pokemon> result;
				try (AdaptadorBD db = new AdaptadorBD(context)) {
					result = new ArrayList<>(db.buscarPokemon(filter));
				}
				handler.post(() -> {
					if (destroyed || request != generation) return;
					loading.setVisibility(View.GONE);
					adapter.submit(result);
					count.setText(getResources().getQuantityString(R.plurals.result_count, result.size(), result.size()));
					if (result.isEmpty()) {
						message.setText(R.string.no_results);
						retry.setVisibility(View.GONE);
						state.setVisibility(View.VISIBLE);
					} else {
						list.setVisibility(View.VISIBLE);
						if (pendingScroll != null) {
							layoutManager.onRestoreInstanceState(pendingScroll);
							pendingScroll = null;
						} else {
							list.scrollToPosition(0);
						}
					}
				});
			} catch (Exception exception) {
				handler.post(() -> {
					if (destroyed || request != generation) return;
					loading.setVisibility(View.GONE);
					message.setText(R.string.list_error);
					count.setText(R.string.unavailable);
					retry.setVisibility(View.VISIBLE);
					state.setVisibility(View.VISIBLE);
				});
			}
		});
		handler.postDelayed(debounce, delay ? 250 : 0);
	}

	@Override protected void onSaveInstanceState(Bundle outState) {
		outState.putString("filter", search.getText().toString());
		outState.putParcelable("scroll", pendingScroll != null
				? pendingScroll : list.getVisibility() == View.VISIBLE
				? layoutManager.onSaveInstanceState() : null);
		super.onSaveInstanceState(outState);
	}

	@Override protected void onDestroy() {
		destroyed = true;
		generation++;
		handler.removeCallbacksAndMessages(null);
		if (query != null) query.cancel(true);
		executor.shutdownNow();
		super.onDestroy();
	}
}
