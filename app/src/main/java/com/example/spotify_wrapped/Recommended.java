package com.example.spotify_wrapped;

public class Recommended {
private class Song {
private String name;
private String artist;

public Song(String name, String artist) {
    //creates new instance of song class
    this.name = name;
    this.artist = artist;
}

public String getSong() {
    //returns song name
    return name;
}
public String getArtist() {
    //returns artist name
    return artist;
}
}
private class TrackImage {

}
}
