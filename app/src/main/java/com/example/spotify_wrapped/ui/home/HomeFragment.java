package com.example.spotify_wrapped.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.spotify_wrapped.API;
import com.example.spotify_wrapped.R;
import com.example.spotify_wrapped.StoryActivity;
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
        if (!API.isInstance()) {
            API.setAccessToken(sharedPreferences.getString("access_token", null));
        }

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String[] time_span = {"long_term", "medium_term", "short_term"};
        Spinner time_span_spinner = binding.spinner;
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, time_span);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time_span_spinner.setAdapter(adapter);

        ImageButton imageBtn = binding.imageBtn;
        TextView weclomeTextView = binding.welcomeTextView;

        API.getUserProfile().observe(getViewLifecycleOwner(), data -> {
            try {
                weclomeTextView.setText(
                        String.format("Welcome, %s", data.getString("display_name")));
                String imageUrl = data.getJSONArray("images").getJSONObject(1).getString("url");
                Picasso.get().load(imageUrl).into(imageBtn);
            } catch (JSONException e) {
                Log.d("error", e.toString());
            }
        });

        imageBtn.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.profileFragment);
        });

        Button start = binding.wrapped;
        start.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), StoryActivity.class);
            intent.putExtra("time_span", time_span_spinner.getSelectedItem().toString());
            intent.putExtra("wrapped_id", "new");
            startActivity(intent);
        });

        return root;
    }
}
