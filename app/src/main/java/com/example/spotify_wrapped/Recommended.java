package com.example.spotify_wrapped;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

public class Recommended {
private class Song {
private String name;
private String artist;
private String imgURL;

public Song(String name, String artist, String imgURL) {
    //creates new instance of song class
    this.name = name;
    this.artist = artist;
    this.imgURL = imgURL;
}

public String getSong() {
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
            .load(imageURL)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_notifications_black_24dp)
            .into(imageView);
}

}
}
