package com.example.layarkarya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;


public class MoviePlayerActivity extends AppCompatActivity {

    public Uri movieUri;
    public PlayerView playerView;
    public ExoPlayer exoPlayer;
    public ImageView bt_fullscreen;
    public ImageView btnClose;
    public boolean isFullScreen = false;
    public Handler handler;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private int movieCount;
    public static int count;
    public RewardedInterstitialAd rewardedInterstitialAd;
    public long ad = 4000;
    private boolean check = false;
    public int currCoin;

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

        rewardedInterstitialAd = null;

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        database.getInstance("https://layarkarya-65957-default-rtdb.asia-southeast1.firebasedatabase.app");

        databaseReference = FirebaseDatabase.getInstance("https://layarkarya-65957-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users")
                .child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                movieCount = dataSnapshot.child("movieWatched").getValue(int.class);
                count = movieCount + 1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().child("movieWatched").setValue(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        long position = player == null ? 0 : player.getCurrentPosition();
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

            if ((ad - 3000 <= position && position <= ad) && !check) {
                check = true;
                initAdvertisement();
            }
            handler.postDelayed(updateProgressAction, delayMs);
        }
    }

    private void initAdvertisement() {
        if (rewardedInterstitialAd != null) {
            return;
        } else {
            MobileAds.initialize(this);
            rewardedInterstitialAd.load(this, "ca-app-pub-3940256099942544/5354046379", new AdRequest.Builder()
                    .build(), new RewardedInterstitialAdLoadCallback() {

                @Override
                public void onAdLoaded(@NonNull RewardedInterstitialAd p0) {
                    rewardedInterstitialAd = p0;
                    rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            exoPlayer.setPlayWhenReady(true);
                            rewardedInterstitialAd = null;
                            check = false;
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            Log.d("MoviePlayerActivity: ", adError.getMessage());
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            handler.removeCallbacks(updateProgressAction);
                        }
                    });
                    LinearLayout sec_ad_countdown = findViewById(R.id.sect_ad_countdown);
                    TextView tx_ad_countdown = findViewById(R.id.tx_ad_countdown);
                    sec_ad_countdown.setVisibility(View.VISIBLE);
                    new CountDownTimer(4000, 1000) {

                        @Override
                        public void onTick(long l) {
                            tx_ad_countdown.setText("Ad in " + l/1000);
                        }

                        @Override
                        public void onFinish() {
                            sec_ad_countdown.setVisibility(View.GONE);
                            rewardedInterstitialAd.show(MoviePlayerActivity.this, rewardItem -> {
                                databaseReference = FirebaseDatabase.getInstance("https://layarkarya-65957-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users")
                                        .child(mAuth.getCurrentUser().getUid());
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        currCoin = dataSnapshot.child("coin").getValue(int.class);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int calculation = currCoin + 10;
                                        final int balanceRes = calculation;

                                        dataSnapshot.getRef().child("coin").setValue(balanceRes).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getBaseContext(), "10 coin has been added to your account!", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            });
                        }
                    }.start();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    rewardedInterstitialAd = null;
                    Log.d("ADD_ERROR", loadAdError.toString());
                }
            });
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