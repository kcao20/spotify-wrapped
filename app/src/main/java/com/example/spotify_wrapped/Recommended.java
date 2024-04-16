package com.example.spotify_wrapped;
import android.view.View;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


public class Recommended {
public class Song {//nested class containing parts of a song
private String name;
private String artist;
private String imgURL;

public Song(String name, String artist, String imgURL) {
    //creates new instance of song class
    this.name = name;
    this.artist = artist;
    this.imgURL = imgURL;
}

public String getName() {
    //returns song name
    return name;
}
public String getArtist() {
    //returns artist name
    return artist;
}
public String getImgURL() {
    return imgURL;
}
public void loadIMG(ImageView imageView) {
    Picasso.get()
            .load(imgURL)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_notifications_black_24dp)
            .into(imageView);
}

}

//create instance to access API
    private Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("http://api.spotify.com")
    //.baseURL("http://api.spotify.com")
    .addConverterFactory(GsonConverterFactory.create())
        .build();

public void getRecommendations(final RecListener recListener) {
    //create instance to access API interface
    SpotifyService spotifyService = retrofit.create(SpotifyService.class);
    //call API to get recommendations
    Call<RecommendationResponse> call = spotifyService.getRecommendations();
    call.enqueue(new retrofit2.Callback<RecommendationResponse>() {
        @Override
        public void onResponse(Call<RecommendationResponse> call, Response<RecommendationResponse> response) {
            if (response.isSuccessful()) {
                RecommendationResponse recommendationResponse = response.body();
                List<Song> songs = processRecommendations(recommendationResponse);
                //success, go to listener
                recListener.onRecFetched(songs);

            } else {
                //need to display error here
                recListener.onRecFail();

            }
        }

        @Override
        public void onFailure(Call<RecommendationResponse> call, Throwable t) {
            recListener.onRecFail();//notify failure
        }
    });

}
public class RecommendationResponse{ //song used to classify response as an object
    private List<Song> songs;
    public List<Song> getSongs() {
        return songs;
    }
    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
public interface SpotifyService{ //interface containing methods to get recommendations
    @GET("recommendations")
    Call<RecommendationResponse> getRecommendations();
}
public interface RecListener{
    view onRecFetched(List<Song> recommendations);
    view onRecFail();
}
public List<Song> processRecommendations(RecommendationResponse r) { //handles recommended songs
    List<Song> sonRecs = new ArrayList<>(); //arraylist to store them in
    if (r != null && r.getSongs() != null) { //makes sure it's not null
        for (Song rec : r.getSongs()) {
            String name = rec.getName();
            String artist = rec.getArtist();
            String imgURL = rec.getImgURL();
            Song track = new Song(name, artist, imgURL); //creates a new song object with each song's characteristics

            sonRecs.add(track); //adds it to the list of recommendations
        }
    }
    return sonRecs; //returns list of recommendations
}

}
