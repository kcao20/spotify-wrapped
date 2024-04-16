package com.example.spotify_wrapped.ui.stories;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spotify_wrapped.databinding.StoryType1Binding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Page1 extends Fragment {

    private StoryType1Binding binding;
    private JSONArray tracks;

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

    public Page1(JSONObject data) {
        try {
            tracks = data.getJSONArray("items");
        } catch (Exception e) {
            Log.d("JSON", e.toString());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = StoryType1Binding.inflate(inflater, container, false);

        TextView titleTextView = binding.titleTextView;
        titleTextView.setText("Your top artists");

        textView1 = binding.item1;
        imageView1 = binding.imageView1;
        textView2 = binding.item2;
        imageView2 = binding.imageView2;
        textView3 = binding.item3;
        imageView3 = binding.imageView3;
        textView4 = binding.item4;
        imageView4 = binding.imageView4;
        textView5 = binding.item5;
        imageView5 = binding.imageView5;

        try {
            populateArtists(tracks);
        } catch (Exception e) {
            Log.d("JSON", e.toString());
        }

        return binding.getRoot();
    }

    private void populateArtists(JSONArray items) throws JSONException {
        if (items.length() >= 5) {
            textView5.setText(items.getJSONObject(4).getString("name"));
            Picasso.get()
                    .load(items.getJSONObject(4)
                            .getJSONArray("images")
                            .getJSONObject(0)
                            .getString("url"))
                    .into(imageView5);
        }
        if (items.length() >= 4) {
            textView4.setText(items.getJSONObject(3).getString("name"));
            Picasso.get()
                    .load(items.getJSONObject(3)
                            .getJSONArray("images")
                            .getJSONObject(0)
                            .getString("url"))
                    .into(imageView4);
        }
        if (items.length() >= 3) {
            textView3.setText(items.getJSONObject(2).getString("name"));
            Picasso.get()
                    .load(items.getJSONObject(2)
                            .getJSONArray("images")
                            .getJSONObject(0)
                            .getString("url"))
                    .into(imageView3);
        }
        if (items.length() >= 2) {
            textView2.setText(items.getJSONObject(1).getString("name"));
            Picasso.get()
                    .load(items.getJSONObject(1)
                            .getJSONArray("images")
                            .getJSONObject(0)
                            .getString("url"))
                    .into(imageView2);
        }
        if (items.length() >= 1) {
            textView1.setText(items.getJSONObject(0).getString("name"));
            Picasso.get()
                    .load(items.getJSONObject(0)
                            .getJSONArray("images")
                            .getJSONObject(0)
                            .getString("url"))
                    .into(imageView1);
        }
    }
}
