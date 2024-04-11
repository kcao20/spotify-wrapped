package com.example.spotify_wrapped;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager2.widget.ViewPager2;

import com.example.spotify_wrapped.databinding.ActivityStoryBinding;
import com.example.spotify_wrapped.ui.stories.StoryAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StoryActivity extends AppCompatActivity {

    private ActivityStoryBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String time_span = "long_term";
        if (intent != null) {
            time_span = intent.getStringExtra("time_span");
        }

        FloatingActionButton close = binding.floatingActionButton;
        close.setOnClickListener(v -> {
            startActivity(new Intent(StoryActivity.this, MainActivity.class));
        });

        MutableLiveData<Map<String, JSONObject>> spotifyData = new MutableLiveData<>();

        Map<String, String> queries = new HashMap<>();
        queries.put("limit", "5");
        queries.put("time_range", time_span);
        API.getTopItems("artists", queries).observe(this, data -> {
            Map<String, JSONObject> map = spotifyData.getValue();
            if (map == null) {
                map = new HashMap<>();
            }
            map.put("artist", data);
            spotifyData.setValue(map);
        });

        queries = new HashMap<>();
        queries.put("limit", "5");
        queries.put("time_range", time_span);
        API.getTopItems("tracks", queries).observe(this, data -> {
            Map<String, JSONObject> map = spotifyData.getValue();
            if (map == null) {
                map = new HashMap<>();
            }
            map.put("track", data);
            spotifyData.setValue(map);
        });

        ViewPager2 viewPager = binding.pager;
        spotifyData.observe(this, data -> {
            if (data.size() == 2) {
                StoryAdapter storyAdapter = new StoryAdapter(this, data);
                viewPager.setAdapter(storyAdapter);
            }
        });
    }
}
