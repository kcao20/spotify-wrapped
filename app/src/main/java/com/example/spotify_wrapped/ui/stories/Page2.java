package com.example.spotify_wrapped.ui.stories;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spotify_wrapped.API;
import com.example.spotify_wrapped.R;
import com.example.spotify_wrapped.databinding.StoryPage1Binding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Page2 extends Fragment {

    private StoryPage1Binding binding;
    private SharedPreferences sharedPreferences;
    private JSONObject data;

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

    public Page2(JSONObject data) {
        this.data = data;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireContext()
                .getSharedPreferences(
                        requireContext().getString(R.string.shared_pref_key), MODE_PRIVATE);
        if (!API.isInstance()) {
            API.setAccessToken(sharedPreferences.getString("access_token", null));
        }
    }

    @Nullable @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = StoryPage1Binding.inflate(inflater, container, false);

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

        try {
            populateTracks(data.getJSONArray("items"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return binding.getRoot();
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
}
