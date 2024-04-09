package com.example.spotify_wrapped.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.spotify_wrapped.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class EditLoginFragment extends Fragment {

    private EditText password1EditText;
    private EditText passwordEditText;
    private Button saveButton;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_login, container, false);

        // Initialize views
        password1EditText = view.findViewById(R.id.pass1);
        passwordEditText = view.findViewById(R.id.password);
        saveButton = view.findViewById(R.id.save);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        saveButton.setOnClickListener(v -> {
            if (user != null) {
                String password1 = password1EditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (password1.equals(password)) {
                    user.updatePassword(password).addOnCompleteListener(passwordTask -> {
                        if (passwordTask.isSuccessful()) {
                            Log.d("PasswordUpdate", "Password updated successfully.");
                            Navigation.findNavController(v).navigate(R.id.updateLoginToHome);
                        } else {
                            Log.e(
                                    "PasswordUpdate",
                                    "Failed to update password.",
                                    passwordTask.getException());
                            if (passwordTask.getException()
                                    instanceof FirebaseAuthWeakPasswordException) {
                                Toast.makeText(
                                                requireContext(),
                                                "password is too weak!",
                                                Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                Toast.makeText(
                                                requireContext(),
                                                "password update failed",
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(requireContext(), "passwords do not match", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        return view;
    }
}
