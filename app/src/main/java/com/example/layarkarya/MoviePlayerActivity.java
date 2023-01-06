package com.example.layarkarya;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.layarkarya.Services.FloatingWidgetService;
import com.zoyi.com.google.android.exoplayer2.ExoPlayer;
import com.zoyi.com.google.android.exoplayer2.ExoPlayerFactory;
import com.zoyi.com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.zoyi.com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.zoyi.com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.zoyi.com.google.android.exoplayer2.source.MediaSource;
import com.zoyi.com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.zoyi.com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.zoyi.com.google.android.exoplayer2.trackselection.TrackSelector;
import com.zoyi.com.google.android.exoplayer2.ui.PlayerView;
import com.zoyi.com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.zoyi.com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.zoyi.com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.zoyi.com.google.android.exoplayer2.util.Util;


public class MoviePlayerActivity extends AppCompatActivity {

    public Uri movieUri;
    public PlayerView playerView;
    public ExoPlayer exoPlayer;
    public ExtractorsFactory extractorsFactory;
    public ImageView exo_floating_widget;
    public View exoPlayback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_movie_player);
        hideActionBar();
        playerView = findViewById(R.id.playerView);
        exoPlayback = LayoutInflater.from(this).inflate(R.layout.exo_playback_control_view, null);
        exo_floating_widget = (ImageView) exoPlayback.findViewById(R.id.exo_floating_widget);

        Intent intent = getIntent();

        if(intent != null ){
            String uri_str = intent.getStringExtra("movieUri");
            movieUri = Uri.parse(uri_str);
        }

        exo_floating_widget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exoPlayer.setPlayWhenReady(false);
                exoPlayer.release();
                Intent serviceIntent = new Intent(MoviePlayerActivity.this,
                        FloatingWidgetService.class);
                serviceIntent.putExtra("videoUri", movieUri.toString());
                startService(serviceIntent);
            }
        });

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector
                (new AdaptiveTrackSelection.Factory(bandwidthMeter));
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this,trackSelector);
        extractorsFactory = new DefaultExtractorsFactory();
        playVideo();

    }

    private void hideActionBar() {

        getSupportActionBar().hide();
    }

    private void setFullScreen() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    private void playVideo(){

        try {
            String playerInfo = Util.getUserAgent(this,"MovieAppClient");
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,playerInfo);
            MediaSource mediaSource = new ExtractorMediaSource(
                    movieUri,dataSourceFactory,extractorsFactory,null,null);

            playerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.release();
    }
}