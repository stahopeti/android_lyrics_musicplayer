package com.example.peterbencestahorszki.viewpager_proba;

/**
 * Created by peterbencestahorszki on 2016. 03. 14..
 */
public class MusicFile {

    private String artist;
    private String title;
    private String path;
    private String LYRICS;

    public MusicFile() {
    }

    public MusicFile(String artist, String title, String path, String LYRICS) {
        this.artist = artist;
        this.title = title;
        this.path = path;
        this.LYRICS = LYRICS;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLYRICS() {
        return LYRICS;
    }

    public void setLYRICS(String LYRICS) {
        this.LYRICS = LYRICS;
    }
}
