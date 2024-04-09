package com.example.spotify_wrapped;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ReAuthFragment extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button saveButton;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reauth, container, false);

        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        saveButton = view.findViewById(R.id.save);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        saveButton.setOnClickListener(v -> {
            if (user != null) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Navigation.findNavController(v).navigate(R.id.ReAuthToUpdateLogin);
                        } else {
                            Toast.makeText(
                                            requireContext(),
                                            "Authentication failed",
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                } else {
                    Toast.makeText(requireContext(), "Fields may not be empty", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        return view;
    }
}
