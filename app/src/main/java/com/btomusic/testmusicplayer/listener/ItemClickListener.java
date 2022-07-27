package com.btomusic.testmusicplayer.listener;

import android.view.View;

/**
 * Created by Amal on 25/12/2018.
 */

public interface ItemClickListener {

    void onItemClick(int position, View view);
    void onPlayPauseClick(int position, View view);
}
