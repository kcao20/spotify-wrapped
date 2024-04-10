package com.example.spotifywrapped.MusicTaste;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spotifywrapped.R;

public class MusicTasteFragment extends Fragment {

    private RecyclerView recyclerView;
    private String[] genres = {"Pop", "Country", "Jazz"};
    private int[] genreStats = {5, 3, 1};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_taste, container, false);

        recyclerView = recyclerView.findViewById(R.id.musicTasteList);
        MusicTasteAdapter adapter = new MusicTasteAdapter(genres, genreStats);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }
}