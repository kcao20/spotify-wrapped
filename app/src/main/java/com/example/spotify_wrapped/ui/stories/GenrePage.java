package com.example.spotify_wrapped.ui.stories;

import android.content.SharedPreferences;
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

import com.example.spotify_wrapped.databinding.StoryType3Binding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenrePage extends Fragment {

    private StoryType3Binding binding;
    private SharedPreferences sharedPreferences;
    private JSONArray artists;
    private JSONObject data;

    private TextView textView1;
    private ImageView imageView1;
    private TextView textView2;
    private ImageView imageView2;
    private TextView textView3;
    private ImageView imageView3;
    private TextView textView4;
    private ImageView imageView4;
    private TextView textView5;
    private ImageView imageView5;

    public GenrePage(JSONObject data) {
        try {
            artists = data.getJSONArray("items");
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
        binding = StoryType3Binding.inflate(inflater, container, false);

        textView1 = binding.textView6;

        Map<String, Integer> genres = new HashMap<>();
        for (int i = 0; i < artists.length(); i++) {
            try {
                JSONArray genreArray = artists.getJSONObject(i).getJSONArray("genres");
                for (int j = 0; j < genreArray.length(); j++) {
                    String genre = genreArray.getString(j);
                    Integer val = genres.get(genre);
                    val = val == null ? 1 : val + 1;
                    genres.put(genre, val);
                }
            } catch (Exception e) {
                Log.d("JSON", e.toString());
            }
        }

        List<Map.Entry<String, Integer>> sortedList = genres.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toList());
        populateGenres(sortedList);

        return binding.getRoot();
    }

    private void populateGenres(List<Map.Entry<String, Integer>> genres) {
        StringBuilder text = new StringBuilder();
        int end = Math.min(5, genres.size());
        for (int i = 0; i < end; i++) {
            text.append(genres.get(genres.size() - 1 - i).getKey() + "\n");
        }
        textView1.setText(text);
    }
}
