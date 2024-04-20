package com.example.spotify_wrapped;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class AuthActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        Button buttonSignIn = findViewById(R.id.buttonSignIn);
        user = firebaseAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(AuthActivity.this, LinkActivity.class));
            finish();
        }

        signInLauncher = registerForActivityResult(new StartActivityForResult(), result -> {
            Log.d("AUTHENTICATION", String.valueOf(result.getResultCode()));
            if (result.getResultCode() == RESULT_OK) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String userEmail = user.getEmail();
                    String uid = user.getUid();
                    usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                usersRef.child(uid)
                                        .child("user_data")
                                        .child("email")
                                        .setValue(userEmail);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("DB", error.toString());
                        }
                    });
                    startActivity(new Intent(AuthActivity.this, LinkActivity.class));
                    finish();
                }
            } else {
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        buttonSignIn.setOnClickListener(v -> startSignInActivity());
    }

    private void startSignInActivity() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().setRequireName(false).build());

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build();

        signInLauncher.launch(signInIntent);
    }
}
