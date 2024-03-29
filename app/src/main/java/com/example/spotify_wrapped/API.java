package com.example.spotify_wrapped;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class API {

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;

    private String mAccessToken;

    public API(@NonNull String accessToken) {
        mAccessToken = accessToken;
    }

    public void onGetUserProfileClicked(TextView profileTextView) {
        if (mAccessToken == null) {
            return;
        }

        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
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
                    profileTextView.setText(jsonObject.toString(2));
                } catch (JSONException | IOException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                }
            }
        });
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
}
