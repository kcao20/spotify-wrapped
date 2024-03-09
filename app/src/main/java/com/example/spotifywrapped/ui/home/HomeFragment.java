package com.example.spotifywrapped.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifywrapped.Auth;
import com.example.spotifywrapped.R;
import com.example.spotifywrapped.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment implements Auth.AuthCallback {
    private Auth auth;
    private FragmentHomeBinding binding;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        auth.onActivityResult(data);
                    }
                });

        auth = new Auth(requireContext(), this);

        TextView profileTextView = root.findViewById(R.id.response_text_view);

        // Initialize the buttons
        Button profileBtn = root.findViewById(R.id.profile_btn);
        Button authenticate = root.findViewById(R.id.btn_authenticate);
        Button logout = root.findViewById(R.id.logout);

        authenticate.setOnClickListener(v -> {
            auth.getCode(launcher);
        });

        profileBtn.setOnClickListener(v -> {
            auth.onGetUserProfileClicked(profileTextView);
        });

        logout.setOnClickListener(v -> {
            auth.logout();
        });

        return root;
    }

    @Override
    public void onTokenReceived(String accessToken) {
        // Handle token received
        Toast.makeText(requireContext(), "Token: " + accessToken, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onError(String error) {
        // Handle error
        Toast.makeText(requireContext(), "Token request failed: " + error, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onCodeReceived(String code) {
        // Handle token received
        Toast.makeText(requireContext(), "Code :" + code, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
