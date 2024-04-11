package com.example.spotify_wrapped.ui.dashboard;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotify_wrapped.API;
import com.example.spotify_wrapped.R;
import com.example.spotify_wrapped.databinding.FragmentDashboardBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private SharedPreferences sharedPreferences;
    private API api;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        sharedPreferences = requireContext()
                .getSharedPreferences(
                        requireContext().getString(R.string.shared_pref_key), MODE_PRIVATE);
        api = new API(sharedPreferences.getString("access_token", null));

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button artistsBtn = binding.artistBtn;
        final Button tracksBtn = binding.tracksBtn;
        final TextView artistsTextView = binding.textView;

        artistsBtn.setOnClickListener(v -> {
            api.getTopItems("artists");
            api.getData().observe(getViewLifecycleOwner(), data -> {
                try {
                    ArrayList<String> artists = new ArrayList<>();
                    JSONArray items = data.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        artists.add(item.getString("name"));
                    }
                    artistsTextView.setText(String.join("\n", artists));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        tracksBtn.setOnClickListener(v -> {
            api.getTopItems("tracks");
            api.getData().observe(getViewLifecycleOwner(), data -> {
                try {
                    ArrayList<String> artists = new ArrayList<>();
                    JSONArray items = data.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        artists.add(item.getString("name"));
                    }
                    artistsTextView.setText(String.join("\n", artists));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
