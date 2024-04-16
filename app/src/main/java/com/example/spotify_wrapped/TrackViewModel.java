package com.example.spotify_wrapped;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class TrackViewModel extends ViewModel {

    private MutableLiveData<List<Track>> tracks = new MutableLiveData<>();
    private TrackRepo trackRepo = new TrackRepo();

    public LiveData<List<Track>> getTracks() {
        return tracks;
    }

    public void fetchTopTracks(String accessToken, String timeRange) {
        trackRepo.fetchTopTracks(accessToken, timeRange, new TrackRepo.Callback<List<Track>>() {
            @Override
            public void onSuccess(List<Track> result) {
                tracks.postValue(result);
            }

            @Override
            public void onError(Exception e) {
                // Handle error, potentially updating the UI to show an error message
            }
        });
    }
}
