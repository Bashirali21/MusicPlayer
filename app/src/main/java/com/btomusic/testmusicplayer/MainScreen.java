package com.btomusic.testmusicplayer;

import static android.app.PendingIntent.FLAG_MUTABLE;

import static com.btomusic.testmusicplayer.startup.AppContext.CHANNEL_ID_2;
import static com.btomusic.testmusicplayer.startup.AppContext.PLAY_ACTION;

import android.animation.Animator;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import com.btomusic.testmusicplayer.Login.Login;
import com.btomusic.testmusicplayer.Model.Tracks;
import com.btomusic.testmusicplayer.adapters.SongTracksList;
import com.btomusic.testmusicplayer.databinding.ActivityMainBinding;
import com.btomusic.testmusicplayer.listener.ActionPlaying;
import com.btomusic.testmusicplayer.listener.ItemClickListener;
import com.btomusic.testmusicplayer.services.MusicService;
import com.btomusic.testmusicplayer.services.NotificationReciever;
import com.btomusic.testmusicplayer.utils.Constants;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.fxn.stash.Stash;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainScreen extends AppCompatActivity implements ServiceConnection, ActionPlaying {
    boolean isLimitlessClicked = false;
    boolean isCrushitClicked = false;
    boolean pordCast = false;
    private SeekBar seekBar;
    private ImageView playBtn, nextBtn, lastBtn;
    private Switch autoplay;
    private TextView songTxt, startTime, endTime, uname;
    private Button logoutBtn;
    private RelativeLayout limitlessBtn, crushitBtn;
    GoogleApiClient mGoogleSignInClient;
    private Handler handler = new Handler();
    private Runnable runnable;
    public android.media.MediaPlayer mediaPlayer = MediaPlayer.getInstance();
    private ArrayList<Song> songList = new ArrayList<>();
    private boolean autoplaySong;
    private AppCompatButton backBtn;
    private RecyclerView recyclerView;
    private int pos = 0;
    private List<Tracks> tracksList = new ArrayList<>();
    private LinearLayout song_layout, list_layout;
    private FirebaseAuth mAuth;
    private MusicService musicService;
    private FirebaseUser user;
    Dialog dialog;
    private boolean isAutoPlay = false;
    private Uri mediaPath = null;
    private ActivityMainBinding b;
    LinearLayout detailed_podcast, book_layout;
    View selected_podcast, book_summary;
    TextView textView1, textView2;
    SongTracksList adapter;
    ImageView imageView1, imageView2;
    private MediaSessionCompat sessionCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        playBtn = (ImageView) findViewById(R.id.pause_play);
        nextBtn = (ImageView) findViewById(R.id.next);
        lastBtn = (ImageView) findViewById(R.id.previous);
        autoplay = (Switch) findViewById(R.id.autoplay);
        songTxt = (TextView) findViewById(R.id.song_name);
        startTime = (TextView) findViewById(R.id.current_time);
        endTime = (TextView) findViewById(R.id.total_time);
        limitlessBtn = findViewById(R.id.limitless);
        crushitBtn = findViewById(R.id.crushit);
        uname = (TextView) findViewById(R.id.username);
        logoutBtn = (Button) findViewById(R.id.logout);
        list_layout = (LinearLayout) findViewById(R.id.list_layout);
        song_layout = (LinearLayout) findViewById(R.id.songsBtn);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        backBtn = findViewById(R.id.back);
        book_layout = findViewById(R.id.book_layout);
        selected_podcast = findViewById(R.id.select_detailed);
        detailed_podcast = findViewById(R.id.detailed_podcast);
        book_summary = findViewById(R.id.book);
        textView1 = findViewById(R.id.textView1);
        imageView1 = findViewById(R.id.imageView1);
        textView2 = findViewById(R.id.textView2);
        imageView2 = findViewById(R.id.imageView2);
        sessionCompat = new MediaSessionCompat(this, "PlayerAudio");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                song_layout.setVisibility(View.VISIBLE);
                list_layout.setVisibility(View.GONE);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (mAuth != null) {
            uname.setText(user.getDisplayName());
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                //  .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainScreen.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        dialog = new Dialog(MainScreen.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_premium);
        dialog.setCancelable(true);

        dialog.findViewById(R.id.okBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainScreen.this, "Payment method is coming soon...", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                if (isLimitlessClicked) {
                    b.lockLayoutLimitless.setVisibility(View.GONE);
                    mediaPath = Uri.parse("android.resource://" + getPackageName() + "/" +
                            R.raw.limitless);
                    getSongDetails(mediaPath, "Limitless");
                }
                if (isCrushitClicked) {
                    b.lockLayoutCrushIt.setVisibility(View.GONE);
                    mediaPath = Uri.parse("android.resource://" + getPackageName() + "/" +
                            R.raw.crush_it);
                    getSongDetails(mediaPath, "Crush it");
                }
            }
        });

        addItems();
        limitlessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Stash.getBoolean(Constants.IS_LIMITLESS_LOCKED, true)) {
                    isLimitlessClicked = true;
                    showPremiumDialog();
                    Stash.put(Constants.IS_LIMITLESS_LOCKED, false);
                    return;
                }
                //songTxt.setText("limitless");
                mediaPath = Uri.parse("android.resource://" + getPackageName() + "/" +
                        R.raw.limitless);
                pos = 0;
                getSongDetails(mediaPath, "Limitless");
            }
        });
        crushitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Stash.getBoolean(Constants.IS_CRUSH_IT_LOCKED, true)) {
                    isCrushitClicked = true;
                    showPremiumDialog();
                    Stash.put(Constants.IS_CRUSH_IT_LOCKED, false);
                    return;
                }
                pos = 1;

                mediaPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.crush_it);
                getSongDetails(mediaPath, "Crush it");

            }
        });
        autoplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //  autoplaySong = true;
                    Stash.put(Constants.IS_AUTOPLAY, true);
                    isAutoPlay = true;
                } else {
                    Stash.put(Constants.IS_AUTOPLAY, false);
                }
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleSignInClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
                mGoogleSignInClient.disconnect();
                startActivity(new Intent(MainScreen.this, Login.class));
                finish();
            }
        });
        autoplaySong = Stash.getBoolean(Constants.IS_AUTOPLAY);
        autoplay.setChecked(autoplaySong);
        if (autoplaySong) {
            MediaPlayer.currentIndex = 0;
            mediaPath = Uri.parse(songList.get(MediaPlayer.currentIndex).getPath());
            getSongDetails(mediaPath,
                    songList.get(MediaPlayer.currentIndex).getTitle());
        }
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && mediaPath != null) {
                    playClicked();
                    if (pordCast) {


                        makeAllFalse(getCurrentPart());

                    }
                } else if (isAutoPlay) {
                    MediaPlayer.currentIndex = 0;
                    mediaPath = Uri.parse(songList.get(MediaPlayer.currentIndex).getPath());
                    getSongDetails(mediaPath,
                            songList.get(MediaPlayer.currentIndex).getTitle());
                }
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer != null && mediaPath != null) {
                    playNextSong();
                }
            }
        });
        lastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && mediaPath != null) {
                    playPreviousSong();
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    if (pordCast) {

                        makeAllFalse(getCurrentPart());

                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (Stash.getBoolean(Constants.IS_LIMITLESS_LOCKED, true)) {
            // LIMITLESS BUTTON IS LOCKED
            b.lockLayoutLimitless.setVisibility(View.VISIBLE);
        }
        if (Stash.getBoolean(Constants.IS_CRUSH_IT_LOCKED, true)) {
            // CRUSH IT BUTTON IS LOCKED
            b.lockLayoutCrushIt.setVisibility(View.VISIBLE);
        }


        b.menuBtn.setOnClickListener(v -> {
            YoYo.with(Techniques.SlideInLeft)
                    .duration(300)
                    .onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            b.navLayout.setVisibility(View.VISIBLE);
                        }
                    })
                    .playOn(b.navLayout);
        });

        b.hideNavBtn.setOnClickListener(v -> {
            YoYo.with(Techniques.SlideOutLeft)
                    .duration(300)
                    .onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            b.navLayout.setVisibility(View.GONE);
                        }
                    })
                    .playOn(b.navLayout);
        });

        b.hideNavLayout.setOnClickListener(v -> {
            YoYo.with(Techniques.SlideOutLeft)
                    .duration(300)
                    .onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            b.navLayout.setVisibility(View.GONE);
                        }
                    })
                    .playOn(b.navLayout);
        });

        LinearLayoutManager manager = new LinearLayoutManager(MainScreen.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        initializeMenus();
    }

    private void initializeMenus() {
        book_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pordCast = false;
                list_layout.setVisibility(View.GONE);
                song_layout.setVisibility(View.VISIBLE);
                book_summary.setVisibility(View.VISIBLE);
                selected_podcast.setVisibility(View.GONE);
                imageView1.setImageResource(R.drawable.ic_baseline_skip_next_24);
                imageView2.setImageResource(R.drawable.ic_baseline_skip_next_white);
                textView1.setTextColor(Color.RED);
                textView2.setTextColor(Color.WHITE);
            }
        });
        detailed_podcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tracksList.clear();
                pordCast = true;
                list_layout.setVisibility(View.VISIBLE);
                song_layout.setVisibility(View.GONE);
                book_summary.setVisibility(View.GONE);
                selected_podcast.setVisibility(View.VISIBLE);
                textView1.setTextColor(Color.WHITE);
                textView2.setTextColor(Color.RED);
                imageView1.setImageResource(R.drawable.ic_baseline_skip_next_white);
                imageView2.setImageResource(R.drawable.ic_baseline_skip_next_24);
                if (mediaPlayer != null && mediaPath != null) {

                    mediaPath = Uri.parse(songList.get(pos).getPath());
                    getSongDetails(mediaPath,
                            songList.get(pos).getTitle());
                }
            }
        });
    }

    private void showPremiumDialog() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);

    }

    private void addItems() {
        songList.add(new Song("android.resource://" + getPackageName() + "/" + R.raw.limitless,
                "Limitless"));
        songList.add(new Song("android.resource://" + getPackageName() + "/" +
                R.raw.crush_it, "Crush it"));
    }

    private void getSongDetails(Uri mediaPath, String title) {
        songTxt.setText(title);

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(MainScreen.this, mediaPath);
        long duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

        loadTracks(mediaPath, duration);
        String durationStr = String.valueOf(duration);

        endTime.setText(convertToMMSS(durationStr));
        if (autoplaySong) {
            autoPlayMusic(mediaPath);
        } else {
            playMusic(mediaPath, duration, 0);
        }

    }

    private void loadTracks(Uri mediaPath, long duration) {
        tracksList.clear();
        tracksList.add(new Tracks(mediaPath, "Parts - " + 1, duration / 4, false));
        tracksList.add(new Tracks(mediaPath, "Parts - " + 2, duration / 2, false));
        tracksList.add(new Tracks(mediaPath, "Parts - " + 3, duration - 100000 / 2, false));
        adapter = new SongTracksList(MainScreen.this, tracksList);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                loadTracks(mediaPath, duration);
                Tracks model = tracksList.get(position);
                int p = (int) tracksList.get(position).getTime();
                mediaPlayer.seekTo(p);
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        makeAllFalse(position);
                    } else {
                        model.setPlaying(false);
                    }
                }
            }

            @Override
            public void onPlayPauseClick(int position, View view) {
                Log.d("current", getCurrentPart() + "");
                Log.d("Cliked", position + "");
                if (pordCast) {

                    if (position == getCurrentPart()) {
                        playClicked();
                        makeAllFalse(getCurrentPart());
                        adapter.notifyDataSetChanged();
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });
        adapter.notifyDataSetChanged();
    }


    private void autoPlayMusic(Uri mediaPath) {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(MainScreen.this, mediaPath);
            mediaPlayer.prepare();
            //mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
            changeSeekbar();
            playBtn.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
            mediaPlayer.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(android.media.MediaPlayer mediaPlayer) {
                    if (MediaPlayer.currentIndex < 1) {
                        playNextSong();
                    } else {
                        MediaPlayer.currentIndex = 0;
                        Uri mediaPath = Uri.parse(songList.get(MediaPlayer.currentIndex).getPath());
                        getSongDetails(mediaPath,
                                songList.get(MediaPlayer.currentIndex).getTitle());
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playMusic(Uri mediaPath, long duration, int position) {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(MainScreen.this, mediaPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            // mediaPlayer.seekTo((int) duration);
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
            changeSeekbar();
            playBtn.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
            showNotification(R.drawable.ic_baseline_pause_circle_outline_24);
            mediaPlayer.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(android.media.MediaPlayer mediaPlayer) {
                    if (isAutoPlay) {
                        //isAutoPlay = false;
                        if (MediaPlayer.currentIndex < 1) {
                            playNextSong();
                        } else {
                            MediaPlayer.currentIndex = 0;
                            Uri mediaPath = Uri.parse(songList.get(MediaPlayer.currentIndex).getPath());
                            getSongDetails(mediaPath,
                                    songList.get(MediaPlayer.currentIndex).getTitle());
                        }
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (position == 0) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        mediaPlayer.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, duration);
        } else {
            mediaPlayer.seekTo((int) duration);
        }
    }

    private void changeSeekbar() {

        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        startTime.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));
        if (mediaPlayer.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    changeSeekbar();
                    if (pordCast) {
                        makeAllFalse(getCurrentPart());
                    }
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void playNextSong() {
        if (MediaPlayer.currentIndex == songList.size() - 1)
            return;
        MediaPlayer.currentIndex += 1;

        // Toast.makeText(MainScreen.this,"" + MediaPlayer.currentIndex,Toast.LENGTH_LONG).show();
       /* if (songList.get(MediaPlayer.currentIndex).getTitle().equals("Limitless")){
            if (Stash.getBoolean(Constants.IS_LIMITLESS_LOCKED, true)) {
                return;
            }
        }
        if (songList.get(MediaPlayer.currentIndex).getTitle().equals("Crush it")){
            if (Stash.getBoolean(Constants.IS_CRUSH_IT_LOCKED, true)) {
                return;
            }
        }*/

        mediaPlayer.reset();
        getSongDetails(Uri.parse(songList.get(MediaPlayer.currentIndex).getPath()),
                songList.get(MediaPlayer.currentIndex).getTitle());

    }

    private void playPreviousSong() {
        if (MediaPlayer.currentIndex == 0)
            return;
        MediaPlayer.currentIndex -= 1;
        /*if (songList.get(MediaPlayer.currentIndex).getTitle().equals("Limitless")){
            if (Stash.getBoolean(Constants.IS_LIMITLESS_LOCKED, true)) {
                return;
            }
        }
        if (songList.get(MediaPlayer.currentIndex).getTitle().equals("Crush it")){
            if (Stash.getBoolean(Constants.IS_CRUSH_IT_LOCKED, true)) {
                return;
            }
        }*/

        mediaPlayer.reset();
        getSongDetails(Uri.parse(songList.get(MediaPlayer.currentIndex).getPath()),
                songList.get(MediaPlayer.currentIndex).getTitle());
    }


    public void pausePlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playBtn.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
            showNotification(R.drawable.ic_baseline_play_circle_outline_24);
        } else {
            mediaPlayer.start();
            changeSeekbar();
            playBtn.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
            showNotification(R.drawable.ic_baseline_pause_circle_outline_24);
        }
        /*else {
            Uri mediaPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.limitless);
            getSongDetails(mediaPath, "Limitless");
        }*/

    }


    public static String convertToMMSS(String duration) {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(MainScreen.this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicService.AudioBinder binder = (MusicService.AudioBinder) iBinder;
        musicService = binder.getService();
        musicService.setCallback(MainScreen.this);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
    }

    private void showNotification(int playpauseBtn) {

        Intent intent = new Intent(this, MainScreen.class);
        PendingIntent pendingIntent = null;
        Intent playIntent = new Intent(this, NotificationReciever.class).setAction(PLAY_ACTION);
        PendingIntent playPendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_MUTABLE);
            playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent,
                    FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(songList.get(pos).getTitle())
                .addAction(playpauseBtn, "Play", playPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(sessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .build();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);

    }

    @Override
    public void playClicked() {
        pausePlay();
    }

    public void makeAllFalse(int posi) {
        for (int i = 0; i < tracksList.size(); i++) {
            if (i == posi) {
                if (mediaPlayer.isPlaying()) {
                    tracksList.get(i).setPlaying(true);
                } else {
                    tracksList.get(i).setPlaying(false);
                }

            } else {
                tracksList.get(i).setPlaying(false);
            }
            adapter.notifyDataSetChanged();
        }


    }

    public int getCurrentPart() {
        int part = 9;
        for (int i = 0; i < tracksList.size(); i++) {
            if (mediaPlayer.getCurrentPosition() < tracksList.get(i).getTime() || mediaPlayer.getCurrentPosition() == tracksList.get(i).getTime()) {
                int see = i - 1;
                if (see < 0) {
                    part = -1;
                    break;
                } else {
                    part = i - 1;
                    break;
                }

            }
        }
        if (part == 9) {
            part = tracksList.size() - 1;
        }
        return part;

    }


}
