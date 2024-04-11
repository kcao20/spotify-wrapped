package com.example.spotify_wrapped;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.spotify_wrapped.databinding.ActivityStoryBinding;
import com.example.spotify_wrapped.ui.stories.StoryAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        ViewPager2 viewPager = binding.pager;
        StoryAdapter storyAdapter = new StoryAdapter(this, time_span);
        viewPager.setAdapter(storyAdapter);
    }
}
