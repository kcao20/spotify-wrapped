package com.example.spotify_wrapped.ui.notifications;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.spotify_wrapped.API;
import com.example.spotify_wrapped.R;
import com.example.spotify_wrapped.databinding.FragmentNotificationsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private SharedPreferences sharedPreferences;
    private CheckBox checkBoxMonth;
    private CheckBox checkBoxSixMonths;
    private CheckBox checkBoxYear;
    private MediaPlayer mediaPlayer;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = requireContext()
                .getSharedPreferences(
                        requireContext().getString(R.string.shared_pref_key), MODE_PRIVATE);
        if (!API.isInstance()) {
            API.setAccessToken(sharedPreferences.getString("access_token", null));
        }
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button artistsBtn = binding.artistBtn;
        final Button tracksBtn = binding.tracksBtn;
        final TextView artistsTextView = binding.textView;
        checkBoxMonth = binding.checkBox;
        checkBoxSixMonths = binding.checkBox2;
        checkBoxYear = binding.checkBox3;

        artistsBtn.setOnClickListener(v -> {
            String timeRange = "";

            if (checkBoxMonth.isChecked()) timeRange = "short_term";
            else if (checkBoxSixMonths.isChecked()) timeRange = "medium_term";
            else if (checkBoxYear.isChecked()) timeRange = "long_term";
            else timeRange = "short_term";
            Log.v("TimeRangeLog", "Time Range: " + timeRange);
            Map<String, String> queries = new HashMap<>();
            queries.put("time_range", timeRange);
            API.getTopItems("artists", queries).observe(getViewLifecycleOwner(), data -> {
                try {
                    ArrayList<String> artists = new ArrayList<>();
                    JSONArray items = data.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        artists.add(item.getString("name"));

                    }
                    artistsTextView.setText(String.join("\n", artists));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        });


        tracksBtn.setOnClickListener(v -> {
            String timeRange = "";

            if (checkBoxMonth.isChecked()) timeRange = "short_term";
            else if (checkBoxSixMonths.isChecked()) timeRange = "medium_term";
            else if (checkBoxYear.isChecked()) timeRange = "long_term";
            else timeRange = "short_term";
            Log.v("TimeRangeLog", "Time Range: " + timeRange);
            Map<String, String> queries = new HashMap<>();
            queries.put("time_range", timeRange);
            API.getTopItems("tracks", queries).observe(getViewLifecycleOwner(), data -> {
                try {
                    String previewUrl = null;
                    ArrayList<String> artists = new ArrayList<>();
                    JSONArray items = data.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        artists.add(item.getString("name"));
                        if (i==0) {
                            previewUrl = item.optString("preview_url");
                        }
                    }
                    artistsTextView.setText(String.join("\n", artists));
                    if (previewUrl != null && !previewUrl.isEmpty()) {
                        playPreview(previewUrl);
                    } else {
                        Log.d("DashboardFragment", "First track preview URL is null or empty");
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        return root;
    }

    private void playPreview(String url) {
        // Ensure only one instance of MediaPlayer is running at a time
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync(); // Asynchronously prepare the media player
            mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start()); // Start playback once prepared
        } catch (IOException e) {
            Log.e("MediaPlayer", "Error setting data source", e);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
