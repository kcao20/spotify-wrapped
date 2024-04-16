package com.example.spotify_wrapped;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager2.widget.ViewPager2;

import com.example.spotify_wrapped.databinding.ActivityStoryBinding;
import com.example.spotify_wrapped.ui.stories.StoryAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StoryActivity extends AppCompatActivity {

    private ActivityStoryBinding binding;

    private DatabaseReference usersRef;
    private DateTimeFormatter formatter;
    private FirebaseAuth firebaseAuth;
    private LocalDate date;
    private MediaPlayer mediaPlayer;
    private MutableLiveData<Map<String, JSONObject>> spotifyData;
    private String uid;
    private String wrappedId;
    private String time_span;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        if (intent != null) {
            time_span = intent.getStringExtra("time_span");
            wrappedId = intent.getStringExtra("wrapped_id");
        } else {
            time_span = "long_term";
            wrappedId = "new";
        }

        spotifyData = new MutableLiveData<>();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        checkForPreviousData();

        if (wrappedId.equals("new")) {
            grabNewData();
        } else {
            retrieveOldData();
        }

        viewPager = binding.pager;

        spotifyData.observe(this, data -> {
            if (data.size() == 3) {
                if (wrappedId.equals("new")) {
                    uploadData(data);
                }
                ArrayList<String> previewLinks = grabPreviewList(data.get("tracks"));
                playPreview(previewLinks);
                startViewAdapter(data);
            }
        });
    }

    private void checkForPreviousData() {
        usersRef.child(uid)
                .child("past_summaries")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        LocalDate currentDate = LocalDate.now();
                        for (DataSnapshot summarySnapshot : dataSnapshot.getChildren()) {
                            String time = summarySnapshot.getKey();
                            LocalDate localDate = LocalDate.parse(time, formatter);
                            if (localDate.equals(currentDate)
                                    && dataSnapshot.child(time).child(time_span).exists()) {
                                wrappedId = (dataSnapshot
                                        .child(summarySnapshot.getKey())
                                        .getKey());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(
                                "Firebase",
                                "Error fetching past summaries",
                                databaseError.toException());
                    }
                });
    }

    private void grabNewData() {
        API.getUserProfile().observe(this, data -> {
            Map<String, JSONObject> map = spotifyData.getValue();
            if (map == null) {
                map = new HashMap<>();
            }
            map.put("profile", data);
            spotifyData.setValue(map);
        });

        Map<String, String> queries = new HashMap<>();
        queries.put("limit", "50");
        queries.put("time_range", time_span);
        API.getTopItems("artists", queries).observe(this, data -> {
            Map<String, JSONObject> map = spotifyData.getValue();
            if (map == null) {
                map = new HashMap<>();
            }
            map.put("artists", data);
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
            map.put("tracks", data);
            spotifyData.setValue(map);
        });
    }

    private void retrieveOldData() {
        usersRef.child(uid)
                .child("past_summaries")
                .child(wrappedId)
                .child(time_span)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String artistJsonString =
                                dataSnapshot.child("artists").getValue(String.class);
                        String trackJsonString =
                                dataSnapshot.child("tracks").getValue(String.class);
                        String profileJsonString =
                                dataSnapshot.child("profile").getValue(String.class);
                        try {
                            JSONObject artist = new JSONObject(artistJsonString);
                            JSONObject track = new JSONObject(trackJsonString);
                            JSONObject profile = new JSONObject(profileJsonString);
                            String timeSpan = dataSnapshot
                                    .child("time_span")
                                    .getValue(String.class); // idk if you want to display
                            // timespan
                            Map<String, JSONObject> map = spotifyData.getValue();
                            if (map == null) {
                                map = new HashMap<>();
                            }
                            map.put("artists", artist);
                            map.put("tracks", track);
                            map.put("profile", profile);
                            spotifyData.setValue(map);
                        } catch (JSONException e) {
                            Log.d("json", e.toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                        Log.e("TAG", "Failed to read value.", error.toException());
                    }
                });
        date = LocalDate.parse(wrappedId, formatter);
    }

    private void startViewAdapter(Map<String, JSONObject> data) {
        StoryAdapter storyAdapter = new StoryAdapter(this, data, time_span, date);
        viewPager.setAdapter(storyAdapter);
    }

    private void uploadData(Map<String, JSONObject> data) {
        LocalDate currentDate = LocalDate.now();
        for (String key : data.keySet()) {
            Object value = data.get(key);
            usersRef.child(uid)
                    .child("past_summaries")
                    .child(currentDate.toString())
                    .child(time_span)
                    .child(key)
                    .setValue(value.toString());
        }
    }

    private ArrayList<String> grabPreviewList(JSONObject data) {
        ArrayList<String> links = new ArrayList<>();
        try {
            JSONArray tracks = data.getJSONArray("items");
            for (int i = 0; i < tracks.length(); i++) {
                links.add(tracks.getJSONObject(i).getString("preview_url"));
            }
        } catch (JSONException e) {
            Log.d("JSON", e.toString());
        }
        return links;
    }

    private void playPreview(ArrayList<String> urls) {
        // Ensure only one instance of MediaPlayer is running at a time
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());
        try {
            mediaPlayer.setDataSource(urls.get(0));
            mediaPlayer.prepareAsync(); // Asynchronously prepare the media player

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                int currentIndex = 0;

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start(); // Start playback once prepared

                    mp.setOnCompletionListener(mediaPlayer -> {
                        // Move to the next URL in the list
                        currentIndex++;
                        if (currentIndex < urls.size()) {
                            try {
                                mediaPlayer.reset();
                                mediaPlayer.setDataSource(urls.get(currentIndex));
                                mediaPlayer.prepareAsync();
                            } catch (IOException e) {
                                Log.e("MediaPlayer", "Error setting data source", e);
                            }
                        }
                    });
                }
            });
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
}
