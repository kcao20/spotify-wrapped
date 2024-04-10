package com.example.spotify_wrapped.ui.dashboard;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        final TextView textView1 = binding.textView1;
        final ImageView imageView1 = binding.imageView1;
        final TextView textView2 = binding.textView2;
        final ImageView imageView2 = binding.imageView2;
        final TextView textView3 = binding.textView3;
        final ImageView imageView3 = binding.imageView3;
        final TextView textView4 = binding.textView4;
        final ImageView imageView4 = binding.imageView4;
        final TextView textView5 = binding.textView5;
        final ImageView imageView5 = binding.imageView5;

        artistsBtn.setOnClickListener(v -> {
            Map<String, String> queries = new HashMap<>();
            queries.put("limit", "5");
            queries.put("time_range", "medium_term");
            api.getTopItems("artists", queries).observe(getViewLifecycleOwner(), data -> {
                try {
                    ArrayList<String> artists = new ArrayList<>();
                    JSONArray items = data.getJSONArray("items");
                    textView1.setText(items.getJSONObject(0).getString("name"));
                    Picasso.get()
                            .load(items.getJSONObject(0)
                                    .getJSONArray("images")
                                    .getJSONObject(0)
                                    .getString("url"))
                            .into(imageView1);
                    textView2.setText(items.getJSONObject(1).getString("name"));
                    Picasso.get()
                            .load(items.getJSONObject(1)
                                    .getJSONArray("images")
                                    .getJSONObject(0)
                                    .getString("url"))
                            .into(imageView2);
                    textView3.setText(items.getJSONObject(2).getString("name"));
                    Picasso.get()
                            .load(items.getJSONObject(2)
                                    .getJSONArray("images")
                                    .getJSONObject(0)
                                    .getString("url"))
                            .into(imageView3);
                    textView4.setText(items.getJSONObject(3).getString("name"));
                    Picasso.get()
                            .load(items.getJSONObject(3)
                                    .getJSONArray("images")
                                    .getJSONObject(0)
                                    .getString("url"))
                            .into(imageView4);
                    textView5.setText(items.getJSONObject(4).getString("name"));
                    Picasso.get()
                            .load(items.getJSONObject(4)
                                    .getJSONArray("images")
                                    .getJSONObject(0)
                                    .getString("url"))
                            .into(imageView5);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        tracksBtn.setOnClickListener(v -> {
            Map<String, String> queries = new HashMap<>();
            queries.put("limit", "5");
            queries.put("time_range", "medium_term");
            api.getTopItems("tracks", queries).observe(getViewLifecycleOwner(), data -> {
                try {
                    JSONArray items = data.getJSONArray("items");
                    textView1.setText(items.getJSONObject(0).getString("name"));
                    Picasso.get()
                            .load(items.getJSONObject(0)
                                    .getJSONObject("album")
                                    .getJSONArray("images")
                                    .getJSONObject(0)
                                    .getString("url"))
                            .into(imageView1);
                    textView2.setText(items.getJSONObject(1).getString("name"));
                    Picasso.get()
                            .load(items.getJSONObject(1)
                                    .getJSONObject("album")
                                    .getJSONArray("images")
                                    .getJSONObject(0)
                                    .getString("url"))
                            .into(imageView2);
                    textView3.setText(items.getJSONObject(2).getString("name"));
                    Picasso.get()
                            .load(items.getJSONObject(2)
                                    .getJSONObject("album")
                                    .getJSONArray("images")
                                    .getJSONObject(0)
                                    .getString("url"))
                            .into(imageView3);
                    textView4.setText(items.getJSONObject(3).getString("name"));
                    Picasso.get()
                            .load(items.getJSONObject(3)
                                    .getJSONObject("album")
                                    .getJSONArray("images")
                                    .getJSONObject(0)
                                    .getString("url"))
                            .into(imageView4);
                    textView5.setText(items.getJSONObject(4).getString("name"));
                    Picasso.get()
                            .load(items.getJSONObject(4)
                                    .getJSONObject("album")
                                    .getJSONArray("images")
                                    .getJSONObject(0)
                                    .getString("url"))
                            .into(imageView5);
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
