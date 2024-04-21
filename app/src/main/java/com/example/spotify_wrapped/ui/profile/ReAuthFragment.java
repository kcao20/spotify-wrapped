package com.example.spotify_wrapped.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.spotify_wrapped.AuthActivity;
import com.example.spotify_wrapped.R;
import com.example.spotify_wrapped.ui.home.HomeViewModel;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReAuthFragment extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button saveButton;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reauth, container, false);
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        saveButton = view.findViewById(R.id.save);
        Bundle args = getArguments();
        String op = args.getString("op");

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
                            if (op.equals("update")) {
                                Navigation.findNavController(v).navigate(R.id.ReAuthToUpdateLogin);
                            }
                            if (op.equals("delete")) {
                                String uid = user.getUid();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                        .getReference("users")
                                        .child(uid);
                                databaseReference.removeValue();
                                user.delete().addOnCompleteListener(deleted -> {
                                    if (deleted.isSuccessful()) {
                                        homeViewModel.logout();
                                        Toast.makeText(
                                                        requireContext(),
                                                        "account deleted",
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                        startActivity(
                                                new Intent(requireActivity(), AuthActivity.class));
                                        getActivity().finish();
                                    } else {
                                        Toast.makeText(
                                                        requireContext(),
                                                        "deleting failed",
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                            }

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
