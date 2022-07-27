package com.btomusic.testmusicplayer;

public class Song {

    private String path;
    private String title;
    private String duration;

    public Song(String path, String title) {
        this.path = path;
        this.title = title;
    }

    public Song() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
