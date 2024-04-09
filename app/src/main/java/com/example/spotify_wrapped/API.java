package com.example.spotify_wrapped;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class API extends ViewModel {

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;

    private String mAccessToken;

    private MutableLiveData<JSONObject> data = new MutableLiveData<>();

    public API(@NonNull String accessToken) {
        mAccessToken = accessToken;
    }

    private void request(String url) {
        if (mAccessToken == null) {
            return;
        }

        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    data.postValue(jsonObject);
                } catch (JSONException | IOException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Log.d("JSON", response.toString());
                }
            }
        });
    }

    public void getUserProfile() {
        request("https://api.spotify.com/v1/me");
    }

    public void getTopItems(String type) {
        request(String.format("https://api.spotify.com/v1/me/top/%s", type));
    }

    public void logout() {
        mAccessToken = null;
    }

    public LiveData<JSONObject> getData() {
        return data;
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
}
