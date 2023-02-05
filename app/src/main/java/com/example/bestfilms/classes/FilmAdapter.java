package com.example.bestfilms.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bestfilms.R;

import java.util.ArrayList;
import java.util.List;


public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.ViewHolder> implements Filterable {

    private final LayoutInflater inflater;
    private ArrayList<CardFilm> fullFilms;
    private ArrayList<CardFilm> filterFilms;

    private ItemClickListener mInterfaceListener;


    public FilmAdapter(Context context, ArrayList<CardFilm> cardFilms) {
        this.inflater = LayoutInflater.from(context);
        this.fullFilms = new ArrayList<>(cardFilms);
        this.filterFilms = cardFilms;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CardFilm> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullFilms);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (CardFilm item : fullFilms) {
                    if (item.getNameRu().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filterFilms.clear();
            filterFilms.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setClickListeners(ItemClickListener itemClickListener) {
        mInterfaceListener = itemClickListener;
    }

    public CardFilm longFilmPressEvent(int position){
        CardFilm cardFilm = filterFilms.get(position);
        cardFilm.setFavourites(!cardFilm.isFavourites());
        filterFilms.set(position, cardFilm);
        notifyItemChanged(position);
        return cardFilm;
    }

    public CardFilm getItemByIndex(int idx){
        if(idx < 0 || idx >= filterFilms.size()) return null;
        return filterFilms.get(idx);
    }

    public void refreshListFilms(ArrayList<CardFilm> films){
        fullFilms = new ArrayList<>(films);
        filterFilms = films;
        notifyDataSetChanged();
    }

    public void clearListFilms(){
        fullFilms.clear();
        filterFilms.clear();
        notifyDataSetChanged();
    }

    public void removeFilms(int position){
        CardFilm film = filterFilms.get(position);
        filterFilms.remove(position);
        fullFilms.remove(film);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public FilmAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmAdapter.ViewHolder holder, int position) {
        CardFilm cardFilm = filterFilms.get(position);
        holder.poster.setImageBitmap(cardFilm.getPoster());

        String name = cardFilm.getNameRu();
        if(name.length() > 13){
            name = name.substring(0, 13).trim() + "...";
        }
        holder.nameRu.setText(name);
        holder.info.setText(cardFilm.getYear());
        holder.fav.setImageResource(R.drawable.ic_fav);
        holder.fav.setVisibility(cardFilm.isFavourites() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return filterFilms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;
        TextView nameRu;
        TextView info;
        ImageView fav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            poster = itemView.findViewById(R.id.poster);
            nameRu = itemView.findViewById(R.id.nameRu);
            info = itemView.findViewById(R.id.info);
            fav = itemView.findViewById(R.id.fav);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mInterfaceListener == null) return false;
                    mInterfaceListener.onItemLongClick(view, getAdapterPosition());
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mInterfaceListener == null) return;
                    mInterfaceListener.onItemClick(view, getAdapterPosition());
                }
            });

        }
    }
}
