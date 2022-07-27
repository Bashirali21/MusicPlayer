package com.btomusic.testmusicplayer.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import com.btomusic.testmusicplayer.MainScreen;
import com.btomusic.testmusicplayer.listener.ActionPlaying;

import java.io.IOException;

public class MusicService extends Service {

    private AudioBinder audioBinder = new AudioBinder();
    private ActionPlaying actionPlaying;

    private static final String ACTION_PAUSE = "PAUSE";
    private static final String ACTION_PLAY = "PLAY";
MediaPlayer mediaPlayer=new MediaPlayer();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return audioBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String actionName = intent.getStringExtra("action");
        String url = intent.getStringExtra("song");
        if (actionName != null){
            switch (actionName) {
                case ACTION_PLAY:
                    if (actionPlaying != null){
                        actionPlaying.playClicked();
                    }
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void setCallback(ActionPlaying actionPlaying){
        this.actionPlaying= actionPlaying;
    }

    public class AudioBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
