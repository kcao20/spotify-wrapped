package com.example.spotify_wrapped;

import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class StoryActivity extends AppCompatActivity {

    private ActivityStoryBinding binding;
    private FirebaseAuth firebaseAuth;
    private String wrappedId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        String time_span;
        if (intent != null) {
            time_span = intent.getStringExtra("time_span");
            wrappedId = intent.getStringExtra("wrapped_id");
        } else {
            time_span = "long_term";
            wrappedId = "new";
        }

        MutableLiveData<Map<String, JSONObject>> spotifyData = new MutableLiveData<>();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

        if (wrappedId.equals("new")) {
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
        } else {
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
        }

        ViewPager2 viewPager = binding.pager;

        spotifyData.observe(this, data -> {
            if (data.size() == 3) {
                LocalDate date = null;
                if (wrappedId.equals("new")) {
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
                } else {
                    date = LocalDate.parse(wrappedId, formatter);
                }
                StoryAdapter storyAdapter = new StoryAdapter(this, data, time_span, date);
                viewPager.setAdapter(storyAdapter);
            }
        });
    }
}
