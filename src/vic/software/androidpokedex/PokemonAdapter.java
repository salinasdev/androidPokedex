package vic.software.androidpokedex;

import android.view.LayoutInflater;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/** Small, bounded catalogue; only visible rows are inflated. */
final class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.Holder> {
    interface OnPokemonClick { void open(long id); }
    private List<Pokemon> items = new ArrayList<>();
    private final OnPokemonClick listener;

    PokemonAdapter(OnPokemonClick listener) {
        this.listener = listener;
        setHasStableIds(true);
        setStateRestorationPolicy(StateRestorationPolicy.PREVENT_WHEN_EMPTY);
    }

    void submit(List<Pokemon> result) {
        final List<Pokemon> previous = items;
        final List<Pokemon> next = new ArrayList<>(result);
        // At most 151 rows; synchronous diff keeps scroll restoration ordered.
        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() { return previous.size(); }
            @Override public int getNewListSize() { return next.size(); }
            @Override public boolean areItemsTheSame(int oldPosition, int newPosition) {
                return previous.get(oldPosition).getId() == next.get(newPosition).getId();
            }
            @Override public boolean areContentsTheSame(int oldPosition, int newPosition) {
                return TextUtils.equals(previous.get(oldPosition).getNombre(),
                        next.get(newPosition).getNombre());
            }
        });
        items = next;
        diff.dispatchUpdatesTo(this);
    }

    @Override public long getItemId(int position) { return items.get(position).getId(); }
    @Override public int getItemCount() { return items.size(); }
    @Override public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.pokemon_row, parent, false));
    }

    @Override public void onBindViewHolder(Holder holder, int position) {
        Pokemon pokemon = items.get(position);
        holder.name.setText(pokemon.getNombre());
        holder.number.setText(holder.itemView.getContext().getString(R.string.pokemon_number, pokemon.getId()));
        int image = PokemonResources.sprite(pokemon.getId());
        holder.sprite.setImageResource(image != 0 ? image : R.drawable.ic_pokeball);
        holder.itemView.setContentDescription(holder.itemView.getContext().getString(
                R.string.open_pokemon, pokemon.getId(), pokemon.getNombre()));
        holder.itemView.setOnClickListener(v -> listener.open(pokemon.getId()));
    }

    static final class Holder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView number;
        final ImageView sprite;
        Holder(View view) {
            super(view);
            name = view.findViewById(R.id.row_name);
            number = view.findViewById(R.id.row_number);
            sprite = view.findViewById(R.id.row_sprite);
        }
    }
}