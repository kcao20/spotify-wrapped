package com.example.spotify_wrapped.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.spotify_wrapped.API;
import com.example.spotify_wrapped.R;
import com.example.spotify_wrapped.databinding.FragmentHomeBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

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

        ImageButton imageBtn = binding.imageBtn;
        TextView weclomeTextView = binding.welcomeTextView;
        TextView profileTextView = binding.responseTextView;

        Button profileBtn = binding.profileBtn;

        api.getUserProfile().observe(getViewLifecycleOwner(), data -> {
            try {
                weclomeTextView.setText(
                        String.format("Welcome, %s", data.getString("display_name")));
                String imageUrl = data.getJSONArray("images").getJSONObject(1).getString("url");
                Picasso.get().load(imageUrl).into(imageBtn);
            } catch (JSONException e) {
                Log.d("error", e.toString());
            }
        });

        profileBtn.setOnClickListener(v -> {
            api.getUserProfile().observe(getViewLifecycleOwner(), data -> {
                try {
                    profileTextView.setText(data.toString(2));
                } catch (JSONException e) {
                    Log.d("error", e.toString());
                }
            });
        });

        imageBtn.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.profileFragment);
        });

        return root;
    }
}
