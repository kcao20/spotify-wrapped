package com.example.spotify_wrapped;

public class Track {
    private String name;
    private String artist;
    private String album;
    private String[] genres;

    public Track(String name, String artist, String album, String[] genres) {
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.genres = genres;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }
}
