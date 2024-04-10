package com.example.spotify_wrapped;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class API extends ViewModel {

    private final Executor executor = Executors.newCachedThreadPool();
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;
    private String mAccessToken;

    public API(@NonNull String accessToken) {
        mAccessToken = accessToken;
    }

    private void request(String url, Map<String, String> params, MutableLiveData<JSONObject> data) {
        if (mAccessToken == null) {
            return;
        }

        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }

        final Request request = new Request.Builder()
                .url(httpBuilder.build())
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        mCall = mOkHttpClient.newCall(request);

        executor.execute(() -> {
            try {
                Response response = mCall.execute();
                final JSONObject jsonObject = new JSONObject(response.body().string());
                data.postValue(jsonObject);
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public LiveData<JSONObject> makeRequest(String url, Map<String, String> params) {
        MutableLiveData<JSONObject> liveData = new MutableLiveData<>();
        request(url, params, liveData);
        return liveData;
    }

    public LiveData<JSONObject> getUserProfile() {
        return makeRequest("https://api.spotify.com/v1/me", null);
    }

    public LiveData<JSONObject> getTopItems(String type, Map<String, String> params) {
        String url = String.format("https://api.spotify.com/v1/me/top/%s", type);
        return makeRequest(url, params);
    }

    public void logout() {
        mAccessToken = null;
    }
}
