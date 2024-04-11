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

import com.example.spotify_wrapped.databinding.StoryPage3Binding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public class Page3 extends Fragment {

    private StoryPage3Binding binding;
    private JSONArray artists;
    private JSONArray tracks;

    public Page3(Map<String, JSONObject> data) {
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
        binding = StoryPage3Binding.inflate(inflater, container, false);

        ImageView artistImageView = binding.artistImageView;
        TextView artistsTextView = binding.artistsTextView;
        TextView tracksTextView = binding.tracksTextView;

        try {
            Picasso.get()
                    .load(artists.getJSONObject(0)
                            .getJSONArray("images")
                            .getJSONObject(0)
                            .getString("url"))
                    .into(artistImageView);
            StringBuilder topArtists = new StringBuilder();
            for (int i = 0; i < artists.length(); i++) {
                String artist =
                        String.format("%d %s\n", i + 1, artists.getJSONObject(i).getString("name"));
                topArtists.append(artist);
            }
            artistsTextView.setText(topArtists.toString());

            StringBuilder topTracks = new StringBuilder();
            for (int i = 0; i < tracks.length(); i++) {
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
