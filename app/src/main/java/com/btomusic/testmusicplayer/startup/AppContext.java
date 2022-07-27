package com.btomusic.testmusicplayer.startup;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import com.fxn.stash.Stash;

public class AppContext extends Application {

    public static String MAIN_ACTION = "com.btomusic.tsetmusicplayer.action";
    public static String PLAY_ACTION = "PLAY";
    public static String STOP_ACTION = "com.btomusic.tsetmusicplayer.action.stop";
    public static final String CHANNEL_ID_1 = "CHANNEL_1";
    public static final String CHANNEL_ID_2 = "CHANNEL_2";
    public static String STARTFOREGROUND_ACTION = "com.btomusic.tsetmusicplayer.action.startforeground";
    public static String STOPFOREGROUND_ACTION = "com.btomusic.tsetmusicplayer.action.stopforeground";

    @Override
    public void onCreate() {
        super.onCreate();
        Stash.init(this);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel1 = new NotificationChannel(CHANNEL_ID_1,"Channel(1)",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel1.setDescription("Channel 1 Description");
            NotificationChannel notificationChannel2 = new NotificationChannel(CHANNEL_ID_2,"Channel(2)",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel2.setDescription("Channel 2 Description");
            NotificationManager manager = (NotificationManager) getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel1);
            manager.createNotificationChannel(notificationChannel2);

        }
    }
}
