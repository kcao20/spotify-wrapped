package com.example.spotify_wrapped.ui.stories;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.spotify_wrapped.StoryActivity;

public class StoryAdapter extends FragmentStateAdapter {

    private String time_span;

    public StoryAdapter(@NonNull StoryActivity fragmentActivity, String time_span) {
        super(fragmentActivity);
        this.time_span = time_span;
    }

    @NonNull @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 0 -> new Page1(time_span);
            case 1 -> new Page2(time_span);
            default -> null;
        };
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
