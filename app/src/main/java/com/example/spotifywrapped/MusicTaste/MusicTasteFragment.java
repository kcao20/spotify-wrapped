package com.example.spotifywrapped.MusicTaste;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spotify_wrapped.API;
import com.example.spotifywrapped.R;
import com.example.spotifywrapped.databinding.FragmentDashboardBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MusicTasteFragment extends Fragment {

    private RecyclerView recyclerView;
    private String[] genres;
    private int[] genreStats = {5, 3, 1};
    private API api;
    private SharedPreferences sharedPreferences;
    Map<String, Integer> genreCountMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_taste, container, false);

        sharedPreferences = requireContext().getSharedPreferences(requireContext().getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
        api = new API(Objects.requireNonNull(sharedPreferences.getString("access_token", null)));

        // connect genres[] to database
        api.getAvailableGenres();
        api.getData().observe(getViewLifecycleOwner(), data -> {
            try {
                JSONArray genresData = data.getJSONArray("genres");
                genres = new String[genresData.length()];
                for (int i = 0; i < genres.length; i++) {
                    genres[i] = genresData.getString(i);
                    genreCountMap.put(genresData.getString(i), 0);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        // connect genreStats[] to database
        api.getTopItems("artists");
        api.getData().observe(getViewLifecycleOwner(), data -> {
            try {
                // get array of artists
                JSONArray artists = data.getJSONArray("items");
                for (int i = 0; i < 20; i++) {
                    // get artist
                    JSONObject artist = artists.getJSONObject(i);
                    JSONArray artistGenres = artist.getJSONArray("genres");
                    for (int j = 0; j < artistGenres.length(); j++) {
                        try {
                            int currentNumSongs = genreCountMap.get(artistGenres.getString(i));
                            genreCountMap.put(artistGenres.getString(i), currentNumSongs + 1);
                        } catch (NullPointerException e) {
                            genreCountMap.put(artistGenres.getString(i), 1);
                        }
                    }
                }

                genreStats = new int[genres.length];
                for (int i = 0; i < genreStats.length; i++) {
                    genreStats[i] = genreCountMap.get(genres[i]);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        recyclerView = recyclerView.findViewById(R.id.musicTasteList);
        MusicTasteAdapter adapter = new MusicTasteAdapter(genres, genreStats);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }
}