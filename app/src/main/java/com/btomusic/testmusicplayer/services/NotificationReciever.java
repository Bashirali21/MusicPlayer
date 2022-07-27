package com.btomusic.testmusicplayer.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.work.OneTimeWorkRequest;

public class NotificationReciever extends BroadcastReceiver {

    private static final String ACTION_PAUSE = "PAUSE";
    private static final String ACTION_PLAY = "PLAY";
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context,MusicService.class);
        final String action = intent.getAction();
        String url = intent.getStringExtra("url");
        switch (action) {
            case ACTION_PLAY:
                intent1.putExtra("action",action);
                intent1.putExtra("song",url);
               context.startService(intent1);

                break;
        }
    }
}
