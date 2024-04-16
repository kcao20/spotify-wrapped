package com.example.spotify_wrapped;

import androidx.annotation.NonNull;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class TrackRepo {
    private OkHttpClient client = new OkHttpClient();

    public interface Callback<T> {
        void onSuccess(T result);

        void onError(Exception e);
    }

    public void fetchTopTracks(
            String accessToken, String timeRange, Callback<List<Track>> callback) {
        String url = "https://api.spotify.com/v1/me/top/tracks?time_range=" + timeRange;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Pass the error to the callback
                callback.onError(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)
                    throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError(new IOException("Unexpected code " + response));
                    return;
                }

                try {
                    String responseData = response.body().string();
                    List<Track> tracks = TrackParser.parseTracks(responseData);
                    callback.onSuccess(tracks);
                } catch (JSONException e) {
                    callback.onError(e);
                }
            }
        });
    }
}
