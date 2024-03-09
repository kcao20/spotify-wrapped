package com.example.spotifywrapped;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import io.github.cdimascio.dotenv.Dotenv;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Auth {
    public final String CLIENT_ID;
    public final String CLIENT_SECRET;
    public final String REDIRECT_URI;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private String mRefreshToken;
    private Call mCall;
    private final Context context;
    private final AuthCallback callback;
    private final Handler handler;

    public Auth(Context context, AuthCallback callback) {
        this.context = context;
        this.callback = callback;
        handler = new Handler(Looper.getMainLooper());
        Dotenv dotenv = Dotenv.configure().directory("/assets").filename("env").load();

        CLIENT_ID = dotenv.get("CLIENT_ID");
        CLIENT_SECRET = dotenv.get("CLIENT_SECRET");
        REDIRECT_URI = dotenv.get("REDIRECT_URI");
    }

    public void getToken(ActivityResultLauncher<Intent> launcher) {
        final AuthorizationRequest request =
                getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        Intent intent = AuthorizationClient.createLoginActivityIntent((Activity) context, request);
        launcher.launch(intent);
    }

    public void getCode(ActivityResultLauncher<Intent> launcher) {
        final AuthorizationRequest request =
                getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        Intent intent = AuthorizationClient.createLoginActivityIntent((Activity) context, request);
        launcher.launch(intent);
    }

    public void onActivityResult(Intent data) {
        final AuthorizationResponse response =
                AuthorizationClient.getResponse(Activity.RESULT_OK, data);

        if (response != null && response.getType() == AuthorizationResponse.Type.CODE) {
            // Token request succeeded, handle the access token
            String mAccessCode = response.getCode();
            callback.onCodeReceived(mAccessCode);
            exchangeAuthorizationCodeForAccessToken(mAccessCode);
        } else {
            // Token request failed or response is null, handle the error
            String error = response != null ? response.getError() : "Unknown error";
            callback.onError(error);
        }
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(
                        CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(true)
                .setScopes(new String[] {"user-read-email"}) // Add desired scopes
                .setCampaign("your-campaign-token")
                .build();
    }

    public void exchangeAuthorizationCodeForAccessToken(String code) {
        // Build the request to exchange authorization code for access token
        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", REDIRECT_URI)
                .build();
        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;

        // Encode the concatenated string using Base64 encoding
        String encoded = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        Request request = new Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .addHeader("Authorization", "Basic " + encoded)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build();

        // Execute the request asynchronously
        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure to exchange authorization code for access token
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(
                                "HTTP",
                                "Failed to exchange authorization code for access token: "
                                        + e.getMessage());
                        callback.onError("Failed to exchange authorization code for access token");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle successful response to exchange authorization code for access token
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject =
                                    new JSONObject(response.body().string());
                            mAccessToken = jsonObject.getString("access_token");
                            mRefreshToken = jsonObject.getString(
                                    "refresh_token"); // Refresh token may be included in the
                            // response
                            callback.onTokenReceived(mAccessToken);
                        } catch (JSONException | IOException e) {
                            Log.d("HTTP", "Failed to parse token response: " + e.getMessage());
                            callback.onError("Failed to parse token response");
                        }
                    }
                });
            }
        });
    }

    public void refreshAccessToken(String refreshToken) {
        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .build();
        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;

        // Encode the concatenated string using Base64 encoding
        String encoded = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        Request request = new Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Basic " + encoded)
                .post(formBody)
                .build();
        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("HTTP", "Failed to refresh access token: " + e.getMessage());
                        callback.onError("Failed to refresh access token");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle successful response to exchange authorization code for access token
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject =
                                    new JSONObject(response.body().string());
                            mAccessToken = jsonObject.getString("access_token");
                            callback.onTokenReceived(mAccessToken);
                        } catch (JSONException | IOException e) {
                            Log.d("HTTP", "Failed to parse token response: " + e.getMessage());
                            callback.onError("Failed to parse token response");
                        }
                    }
                });
            }
        });
    }

    // just a test to see if the token works, the textview and response can be changed
    public void onGetUserProfileClicked(TextView profileTextView) {
        if (mAccessToken == null) {
            Toast.makeText(context, "You need to get an access token first!", Toast.LENGTH_SHORT)
                    .show();
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
                Toast.makeText(
                                context,
                                "Failed to fetch data, watch Logcat for more details",
                                Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Check if the response was successful
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final JSONObject jsonObject =
                                    new JSONObject(response.body().string());

                            profileTextView.setText(jsonObject.toString(
                                    3)); // can change this to whatever needs the profile
                        } catch (JSONException | IOException e) {
                            Log.d("JSON", "Failed to parse data: " + e);
                            Toast.makeText(
                                            context,
                                            "Failed to parse data, watch Logcat for more details",
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
            }
        });
    }

    public void logout() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mAccessToken != null) {
                    mAccessToken = null;
                    Toast.makeText(context, "logged out successfully", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    public interface AuthCallback {
        void onTokenReceived(String accessToken);

        void onError(String error);

        void onCodeReceived(String code);
    }
}
