package com.example.spotify_wrapped.ui.stories;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.spotify_wrapped.StoryActivity;

import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Map;

public class StoryAdapter extends FragmentStateAdapter {

    private String time_span;
    private Map<String, JSONObject> data;
    private LocalDate date;

    public StoryAdapter(
            @NonNull StoryActivity fragmentActivity,
            Map<String, JSONObject> data,
            String time_span,
            LocalDate date) {
        super(fragmentActivity);
        this.data = data;
        this.time_span = time_span;
        this.date = date;
    }

    @NonNull @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 0 -> new WelcomePage(data.get("profile"), time_span, date);
            case 1 -> new Page1(data.get("artists"));
            case 2 -> new Page2(data.get("tracks"));
            case 3 -> new GenrePage(data.get("artists"));
            case 4 -> new SummaryPage(data);
            default -> null;
        };
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
