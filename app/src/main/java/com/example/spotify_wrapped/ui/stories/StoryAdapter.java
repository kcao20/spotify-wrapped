package com.example.spotify_wrapped.ui.stories;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.spotify_wrapped.StoryActivity;

import org.json.JSONObject;

import java.util.Map;

public class StoryAdapter extends FragmentStateAdapter {

    private String time_span;
    private Map<String, JSONObject> data;

    public StoryAdapter(
            @NonNull StoryActivity fragmentActivity,
            Map<String, JSONObject> data,
            String time_span) {
        super(fragmentActivity);
        this.data = data;
        this.time_span = time_span;
    }

    @NonNull @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 0 -> new WelcomePage(data.get("profile"), time_span);
            case 1 -> new Page1(data.get("artists"));
            case 2 -> new Page2(data.get("tracks"));
            case 3 -> new Page3(data);
            default -> null;
        };
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
