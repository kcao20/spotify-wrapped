package com.example.spotify_wrapped.ui.dashboard;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotify_wrapped.API;
import com.example.spotify_wrapped.R;
import com.example.spotify_wrapped.databinding.FragmentDashboardBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private SharedPreferences sharedPreferences;
    private API api;

    private Button artistsBtn;
    private Button tracksBtn;
    private TextView textView1;
    private ImageView imageView1;
    private TextView textView2;
    private ImageView imageView2;
    private TextView textView3;
    private ImageView imageView3;
    private TextView textView4;
    private ImageView imageView4;
    private TextView textView5;
    private ImageView imageView5;

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

        String[] time_span = {"long_term", "medium_term", "short_term"};
        Spinner time_span_spinner = binding.spinner;
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, time_span);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time_span_spinner.setAdapter(adapter);

        artistsBtn = binding.artistBtn;
        tracksBtn = binding.tracksBtn;
        textView1 = binding.textView1;
        imageView1 = binding.imageView1;
        textView2 = binding.textView2;
        imageView2 = binding.imageView2;
        textView3 = binding.textView3;
        imageView3 = binding.imageView3;
        textView4 = binding.textView4;
        imageView4 = binding.imageView4;
        textView5 = binding.textView5;
        imageView5 = binding.imageView5;

        artistsBtn.setOnClickListener(v -> {
            onArtistBtnClick(time_span_spinner.getSelectedItem().toString());
        });

        tracksBtn.setOnClickListener(v -> {
            onTrackBtnClick(time_span_spinner.getSelectedItem().toString());
        });

        return root;
    }

    private void onArtistBtnClick(String time_span) {
        Map<String, String> queries = new HashMap<>();
        queries.put("limit", "5");
        queries.put("time_range", time_span);
        api.getTopItems("artists", queries).observe(getViewLifecycleOwner(), data -> {
            try {
                populateArtists(data.getJSONArray("items"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void onTrackBtnClick(String time_span) {
        Map<String, String> queries = new HashMap<>();
        queries.put("limit", "5");
        queries.put("time_range", time_span);
        api.getTopItems("tracks", queries).observe(getViewLifecycleOwner(), data -> {
            try {
                populateTracks(data.getJSONArray("items"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void populateArtists(JSONArray items) throws JSONException {
        switch (items.length()) {
            case 5:
                textView5.setText(items.getJSONObject(4).getString("name"));
                Picasso.get()
                        .load(items.getJSONObject(4)
                                .getJSONArray("images")
                                .getJSONObject(0)
                                .getString("url"))
                        .into(imageView5);
            case 4:
                textView4.setText(items.getJSONObject(3).getString("name"));
                Picasso.get()
                        .load(items.getJSONObject(3)
                                .getJSONArray("images")
                                .getJSONObject(0)
                                .getString("url"))
                        .into(imageView4);
            case 3:
                textView3.setText(items.getJSONObject(2).getString("name"));
                Picasso.get()
                        .load(items.getJSONObject(2)
                                .getJSONArray("images")
                                .getJSONObject(0)
                                .getString("url"))
                        .into(imageView3);
            case 2:
                textView2.setText(items.getJSONObject(1).getString("name"));
                Picasso.get()
                        .load(items.getJSONObject(1)
                                .getJSONArray("images")
                                .getJSONObject(0)
                                .getString("url"))
                        .into(imageView2);
            case 1:
                textView1.setText(items.getJSONObject(0).getString("name"));
                Picasso.get()
                        .load(items.getJSONObject(0)
                                .getJSONArray("images")
                                .getJSONObject(0)
                                .getString("url"))
                        .into(imageView1);
        }
    }

    private void populateTracks(JSONArray items) throws JSONException {
        switch (items.length()) {
            case 5:
                textView5.setText(items.getJSONObject(4).getString("name"));
                Picasso.get()
                        .load(items.getJSONObject(4)
                                .getJSONObject("album")
                                .getJSONArray("images")
                                .getJSONObject(0)
                                .getString("url"))
                        .into(imageView5);
            case 4:
                textView4.setText(items.getJSONObject(3).getString("name"));
                Picasso.get()
                        .load(items.getJSONObject(3)
                                .getJSONObject("album")
                                .getJSONArray("images")
                                .getJSONObject(0)
                                .getString("url"))
                        .into(imageView4);
            case 3:
                textView3.setText(items.getJSONObject(2).getString("name"));
                Picasso.get()
                        .load(items.getJSONObject(2)
                                .getJSONObject("album")
                                .getJSONArray("images")
                                .getJSONObject(0)
                                .getString("url"))
                        .into(imageView3);
            case 2:
                textView2.setText(items.getJSONObject(1).getString("name"));
                Picasso.get()
                        .load(items.getJSONObject(1)
                                .getJSONObject("album")
                                .getJSONArray("images")
                                .getJSONObject(0)
                                .getString("url"))
                        .into(imageView2);
            case 1:
                textView1.setText(items.getJSONObject(0).getString("name"));
                Picasso.get()
                        .load(items.getJSONObject(0)
                                .getJSONObject("album")
                                .getJSONArray("images")
                                .getJSONObject(0)
                                .getString("url"))
                        .into(imageView1);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
