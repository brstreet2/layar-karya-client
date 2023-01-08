package com.example.layarkarya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;


public class MoviePlayerActivity extends AppCompatActivity {

    public Uri movieUri;
    public PlayerView playerView;
    public ExoPlayer exoPlayer;
    public ImageView bt_fullscreen;
    public ImageView btnClose;
    public boolean isFullScreen = false;
    public Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_player);
        handler = new Handler(Looper.getMainLooper());

        playerView = findViewById(R.id.player);
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        bt_fullscreen = playerView.findViewById(R.id.bt_fullscreen);
        btnClose = playerView.findViewById(R.id.exo_close);

        bt_fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFullScreen) {
                    bt_fullscreen.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_fullscreen_exit));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                } else {
                    bt_fullscreen.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_fullscreen));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                isFullScreen = !isFullScreen;
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoviePlayerActivity.this.finish();
            }
        });

        exoPlayer = new ExoPlayer.Builder(this)
                .setSeekBackIncrementMs(5000)
                .setSeekForwardIncrementMs(5000)
                .build();
        playerView.setPlayer(exoPlayer);

        playerView.setKeepScreenOn(true);

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                }

                if (!exoPlayer.getPlayWhenReady()) {
                    handler.removeCallbacks(updateProgressAction);
                } else {
                    onProgress();
                }
            }
        });

        Intent intent = getIntent();
        if(intent != null ){
            String uri_str = intent.getStringExtra("movieUri");
            movieUri = Uri.parse(uri_str);
        }
        MediaItem media = MediaItem.fromUri(movieUri);
        exoPlayer.setMediaItem(media);
        exoPlayer.prepare();
        exoPlayer.play();

    }

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            onProgress();
        }
    };

    private void onProgress () {
        ExoPlayer player = exoPlayer;
        long position = player == null ? 0 : player.getContentPosition();
        handler.removeCallbacks(updateProgressAction);
        int playbackState = player == null ? Player.STATE_IDLE : player.getPlaybackState();

        if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
            long delayMs;
            if (player.getPlayWhenReady() && playbackState == Player.STATE_READY) {
                delayMs = 1000 - position % 1000;
                if (delayMs < 200) {
                    delayMs += 1000;
                }
            } else {
                delayMs = 1000;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        exoPlayer.pause();
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bt_fullscreen.performClick();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        exoPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoPlayer.release();
    }

}