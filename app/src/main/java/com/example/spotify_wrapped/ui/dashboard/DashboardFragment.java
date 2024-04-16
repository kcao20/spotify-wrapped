package com.example.spotify_wrapped.ui.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify_wrapped.StoryActivity;
import com.example.spotify_wrapped.databinding.FragmentDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();

        usersRef.child(uid)
                .child("past_summaries")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<PastSummary> pastSummaries = new ArrayList<>();
                        for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                            String date = dateSnapshot.getKey();
                            for (DataSnapshot termSnapshot : dateSnapshot.getChildren()) {
                                String term = termSnapshot.getKey();
                                PastSummary pastSummary = new PastSummary(date, term);
                                pastSummaries.add(pastSummary);
                                Log.d("past bullshit", date + ":" + term);
                            }
                        }

                        PastSummariesAdapter adapter = new PastSummariesAdapter(pastSummaries);
                        adapter.setOnItemClickListener(position -> {
                            PastSummary pastSummary = pastSummaries.get(position);
                            String date = pastSummary.getDate();
                            String timeSpan = pastSummary.getTimeSpan();
                            Intent intent = new Intent(getActivity(), StoryActivity.class);
                            intent.putExtra("time_span", timeSpan);
                            intent.putExtra("wrapped_id", date);
                            startActivity(intent);
                        });
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(
                                "Firebase",
                                "Error fetching past summaries",
                                databaseError.toException());
                    }
                });

        return root;
    }

    private MediaPlayer mediaPlayer;

    private void playPreview(String url) {
        // Ensure only one instance of MediaPlayer is running at a time
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync(); // Asynchronously prepare the media player
            mediaPlayer.setOnPreparedListener(
                    mp -> mediaPlayer.start()); // Start playback once prepared
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
