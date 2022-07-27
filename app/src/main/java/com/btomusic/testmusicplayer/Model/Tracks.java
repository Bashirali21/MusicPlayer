package com.btomusic.testmusicplayer.Model;

import android.net.Uri;

public class Tracks {

    private Uri name;
    private String parts;
    private long time;
    private boolean isPlaying;

    public Tracks(){}

    public Tracks(Uri name,String parts, long time,boolean isPlaying) {
        this.name = name;
        this.parts = parts;
        this.time = time;
        this.isPlaying = isPlaying;
    }

    public Uri getName() {
        return name;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public void setName(Uri name) {
        this.name = name;
    }

    public String getParts() {
        return parts;
    }

    public void setParts(String parts) {
        this.parts = parts;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
