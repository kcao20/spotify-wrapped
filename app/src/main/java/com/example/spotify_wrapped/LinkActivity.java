package com.example.spotify_wrapped;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spotify_wrapped.databinding.ActivityLinkBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.time.Duration;

public class LinkActivity extends AppCompatActivity {

    private ActivityLinkBinding binding;
    private FirebaseAuth firebaseAuth;

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private String CLIENT_ID;
    private String CLIENT_SECRET;
    private String REDIRECT_URI;

    private static final int REQUEST_CODE = 1337;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Dotenv dotenv = Dotenv.configure().directory("/assets").filename("env").load();
        CLIENT_ID = dotenv.get("CLIENT_ID");
        CLIENT_SECRET = dotenv.get("CLIENT_SECRET");
        REDIRECT_URI = dotenv.get("REDIRECT_URI");

        sharedPreferences =
                this.getSharedPreferences(getString(R.string.shared_pref_key), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        String uid = firebaseAuth.getCurrentUser().getUid();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        if (sharedPreferences.contains("access_token")
                || sharedPreferences.contains("refresh_token")) {
            if (System.currentTimeMillis() > sharedPreferences.getLong("expires_at", 0)) {
                refreshAccessToken();
            } else {
                startActivity(new Intent(LinkActivity.this, MainActivity.class));
            }
        } else {
            usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String accessToken = dataSnapshot
                            .child("user_data")
                            .child("access_token")
                            .getValue(String.class);
                    Log.d("DB", "Access Token: " + accessToken);
                    Long expiresAt = dataSnapshot
                            .child("user_data")
                            .child("expires_at")
                            .getValue(Long.class);
                    Log.d("DB", "Expires At: " + expiresAt);
                    String refreshToken = dataSnapshot
                            .child("user_data")
                            .child("refresh_token")
                            .getValue(String.class);
                    Log.d("DB", "Refresh Token: " + refreshToken);
                    if (accessToken != null && expiresAt != null && refreshToken != null) {
                        editor.putString("access_token", accessToken);
                        editor.putString("refresh_token", refreshToken);
                        long currentTimestamp = System.currentTimeMillis();
                        editor.putLong(
                                "expires_at",
                                currentTimestamp + Duration.ofSeconds(expiresAt).toMillis());
                        editor.apply();
                        startActivity(new Intent(LinkActivity.this, MainActivity.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                    Log.e("TAG", "Failed to read value.", error.toException());
                }
            });
        }

        binding = ActivityLinkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                CLIENT_ID, AuthorizationResponse.Type.CODE, REDIRECT_URI);

        builder.setScopes(new String[] {"user-read-private", "user-read-email", "user-top-read"});
        AuthorizationRequest request = builder.build();

        Button login = binding.buttonLogin;

        login.setOnClickListener(v -> {
            AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                    // Response was successful and contains auth token
                case CODE:
                    editor.putString("code", response.getCode());
                    editor.apply();

                    exchangeAuthorizationCodeForAccessToken();
                    break;

                    // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    Log.d("APP", response.getError());
                    break;

                    // Most likely auth flow was cancelled
                default:
                    Log.d("APP", "fuckery");
            }
        }
    }

    public void exchangeAuthorizationCodeForAccessToken() {
        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", sharedPreferences.getString("code", ""))
                .add("redirect_uri", REDIRECT_URI)
                .build();

        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
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
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)
                    throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    editor.putString("access_token", jsonObject.getString("access_token"));
                    editor.putString("refresh_token", jsonObject.getString("refresh_token"));
                    long currentTimestamp = System.currentTimeMillis();
                    editor.putLong(
                            "expires_at",
                            currentTimestamp
                                    + Duration.ofSeconds(jsonObject.getInt("expires_in"))
                                            .toMillis());
                    editor.apply();
                    DatabaseReference usersRef =
                            FirebaseDatabase.getInstance().getReference("users");
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    String uid = user.getUid();
                    usersRef.child(uid)
                            .child("user_data")
                            .child("access_token")
                            .setValue(jsonObject.getString("access_token"));
                    usersRef.child(uid)
                            .child("user_data")
                            .child("refresh_token")
                            .setValue(jsonObject.getString("refresh_token"));
                    usersRef.child(uid)
                            .child("user_data")
                            .child("expires_at")
                            .setValue(currentTimestamp
                                    + Duration.ofSeconds(jsonObject.getInt("expires_in"))
                                            .toMillis());

                    startActivity(new Intent(LinkActivity.this, MainActivity.class));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void refreshAccessToken() {
        String refreshToken = sharedPreferences.getString("refresh_token", null);

        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .build();

        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
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
                Log.d("HTTP", "Failed to refresh access token: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    editor.putString("access_token", jsonObject.getString("access_token"));
                    editor.apply();

                    startActivity(new Intent(LinkActivity.this, MainActivity.class));
                } catch (JSONException | IOException e) {
                    Log.d("HTTP", "Failed to parse token response: " + e.getMessage());
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
