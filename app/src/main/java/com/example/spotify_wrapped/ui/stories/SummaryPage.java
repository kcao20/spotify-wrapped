package com.example.spotify_wrapped.ui.stories;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spotify_wrapped.databinding.StoryType2Binding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public class SummaryPage extends Fragment {

    private StoryType2Binding binding;
    private JSONArray artists;
    private JSONArray tracks;

    public SummaryPage(Map<String, JSONObject> data) {
        try {
            artists = data.get("artists").getJSONArray("items");
            tracks = data.get("tracks").getJSONArray("items");
        } catch (Exception e) {
            Log.d("JSON", e.toString());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = StoryType2Binding.inflate(inflater, container, false);

        ImageView artistImageView = binding.artistImageView;
        TextView artistsTextView = binding.artistsTextView;
        TextView tracksTextView = binding.tracksTextView;

        Log.d("summary", "loaded");

        try {
            Picasso.get()
                    .load(artists.getJSONObject(0)
                            .getJSONArray("images")
                            .getJSONObject(0)
                            .getString("url"))
                    .into(artistImageView);
            StringBuilder topArtists = new StringBuilder();
            int end = Math.min(5, artists.length());
            for (int i = 0; i < end; i++) {
                String artist =
                        String.format("%d %s\n", i + 1, artists.getJSONObject(i).getString("name"));
                topArtists.append(artist);
            }
            artistsTextView.setText(topArtists.toString());

            StringBuilder topTracks = new StringBuilder();
            end = Math.min(5, tracks.length());
            for (int i = 0; i < end; i++) {
                String track =
                        String.format("%d %s\n", i + 1, tracks.getJSONObject(i).getString("name"));
                topTracks.append(track);
            }
            tracksTextView.setText(topTracks.toString());
        } catch (Exception e) {
            Log.d("JSON", e.toString());
        }

        return binding.getRoot();
    }
}
