package com.example.spotify_wrapped.ui.stories;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spotify_wrapped.databinding.WelcomePageBinding;

import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WelcomePage extends Fragment {

    private WelcomePageBinding binding;
    private String name;
    private String time_span;
    private LocalDate date;

    public WelcomePage(JSONObject data, String time_span, LocalDate date) {
        try {
            name = data.getString("display_name");
        } catch (Exception e) {
            Log.d("JSON", e.toString());
        }
        this.time_span = time_span;
        this.date = date;
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
        binding = WelcomePageBinding.inflate(inflater, container, false);

        TextView greeting = binding.greetingTextView;
        greeting.setText(String.format("Hello %s", name));

        TextView dateView = binding.dateTextView;
        if (date != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            dateView.setText(String.format("Generated on %s", date.format(formatter)));
        }

        TextView scope = binding.textView;
        switch (time_span) {
            case "long_term" -> scope.setText(
                    "It's Wrapped Time.\nSee you listening habits for the past year!");
            case "medium_term" -> scope.setText(
                    "It's Wrapped Time.\nSee you listening habits for the past 6 months!");
            case "short_term" -> scope.setText(
                    "It's Wrapped Time.\nSee you listening habits for the past month!");
        }

        return binding.getRoot();
    }
}
