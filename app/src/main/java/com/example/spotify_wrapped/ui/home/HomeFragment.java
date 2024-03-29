package com.example.spotify_wrapped.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotify_wrapped.API;
import com.example.spotify_wrapped.LoginActivity;
import com.example.spotify_wrapped.R;
import com.example.spotify_wrapped.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SharedPreferences sharedPreferences;
    private API api;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        sharedPreferences = requireContext()
                .getSharedPreferences(
                        requireContext().getString(R.string.shared_pref_key), MODE_PRIVATE);
        api = new API(sharedPreferences.getString("access_token", null));

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView profileTextView = root.findViewById(R.id.response_text_view);

        Button profileBtn = root.findViewById(R.id.profile_btn);
        Button logout = root.findViewById(R.id.logout);

        profileBtn.setOnClickListener(v -> {
            api.onGetUserProfileClicked(profileTextView);
        });

        logout.setOnClickListener(v -> {
            if (homeViewModel.logout()) {
                Toast.makeText(getContext(), "Logged out Successfully", Toast.LENGTH_SHORT)
                        .show();
                startActivity(new Intent(requireActivity(), LoginActivity.class));
            }
        });

        return root;
    }
}
