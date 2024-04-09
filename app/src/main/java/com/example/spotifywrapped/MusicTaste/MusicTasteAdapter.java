package com.example.spotifywrapped.MusicTaste;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifywrapped.R;

public class MusicTasteAdapter extends RecyclerView.Adapter<MusicTasteAdapter.ViewHolder> {
    private String[] genres;
    private int[] genreStats;

    public MusicTasteAdapter(String[] genres, int[] genreStats) {
        this.genres = genres;
        this.genreStats = genreStats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String genre = genres[position];
        int stats = genreStats[position];
        holder.textView.setText(genre + ": " + stats);
    }

    @Override
    public int getItemCount() {
        return genres.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.music_list_item);
        }
    }
}
