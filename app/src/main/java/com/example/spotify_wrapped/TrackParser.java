package com.example.spotify_wrapped;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrackParser {

    public static List<Track> parseTracks(String json) throws JSONException {
        List<Track> tracks = new ArrayList<>();
        JSONObject root = new JSONObject(json);
        JSONArray items = root.getJSONObject("items").getJSONArray("tracks");

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String name = item.getString("name");

            JSONArray artistsArray = item.getJSONArray("artists");
            StringBuilder artistsBuilder = new StringBuilder();
            for (int j = 0; j < artistsArray.length(); j++) {
                artistsBuilder.append(artistsArray.getJSONObject(j).getString("name"));
                if (j < artistsArray.length() - 1) {
                    artistsBuilder.append(", ");
                }
            }
            String artistNames = artistsBuilder.toString();

            String album = item.getJSONObject("album").getString("name");

            JSONArray genresArray = item.getJSONObject("album").getJSONArray("genres");
            String[] genres = new String[genresArray.length()];
            for (int k = 0; k < genresArray.length(); k++) {
                genres[k] = genresArray.getString(k);
            }

            Track track = new Track(name, artistNames, album, genres);
            tracks.add(track);
        }

        return tracks;
    }
}
