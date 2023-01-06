package com.example.layarkarya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.layarkarya.Adapter.MovieShowAdapter;
import com.example.layarkarya.Adapter.PageSliderAdapter;
import com.example.layarkarya.Model.MovieDetails;
import com.example.layarkarya.Model.MoviesItemClickListenerNew;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements MoviesItemClickListenerNew {
    public MovieShowAdapter movieShowAdapter;
    //  public Button btnLogOut;
    public FirebaseAuth mAuth;
    public DatabaseReference mDatabaseReference;
    public List<MovieDetails> uploads, uploadsListlatest, uploadsListpopular;
    public List<MovieDetails> actionmovies, horrormovies, comedymovies, romancemovie, adventuremovie;

    public ViewPager sliderpager;
    public ArrayList <MovieDetails> uploadsslider;

    public TabLayout indicator, tabActionMovies;
    public RecyclerView MoviesRV, moviesRvWeek, tab;
    public ProgressDialog progressDialog;

    public MovieUncatagorizedAdapter movieUncatagorizedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Discover");
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.action_bar);
//        btnLogOut = findViewById(R.id.btnLogout);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        inViews();
        allMovies();
        moviesViewTab();
//        btnLogOut.setOnClickListener(view ->{
//            mAuth.signOut();
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//        });

    }

    public void allMovies() {
        uploads = new ArrayList<>();
        uploadsListlatest = new ArrayList<>();
        uploadsListpopular = new ArrayList<>();
        actionmovies = new ArrayList<>();
        horrormovies = new ArrayList<>();
        uploadsslider = new ArrayList<>();
        adventuremovie = new ArrayList<>();
        comedymovies = new ArrayList<>();
        romancemovie = new ArrayList<>();

        mDatabaseReference = FirebaseDatabase.getInstance("https://layarkarya-65957-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("movies");
        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    MovieDetails upload = postSnapshot.getValue(MovieDetails.class);
                    MovieDetails slide = postSnapshot.getValue(MovieDetails.class);
                    if (upload.getMovie_type().equals("Latest Movie")) {
                        uploadsListlatest.add(upload);
                    }

                    else if (upload.getMovie_type().equals("Popular Movie")) {
                        uploadsListpopular.add(upload);
                    }

                    if (upload.getMovie_category().equals("Action")) {
                        actionmovies.add(upload);
                    }

                    else if (upload.getMovie_category().equals("Horror")) {
                        horrormovies.add(upload);
                    }

                    if (upload.getMovie_category().equals("Adventure")) {
                        adventuremovie.add(upload);
                    }

                    else if (upload.getMovie_category().equals("Comedy")) {
                        comedymovies.add(upload);
                    }

                    if (upload.getMovie_category().equals("Romance")) {
                        romancemovie.add(upload);
                    }

                    if (upload.getMovie_slide().equals("Slide Movie")) {
                        uploadsslider.add(slide);
                    }

                    uploads.add(upload);
                }
                initSlider();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }

    public void moviesViewTab() {
        getActionMovies();
        tabActionMovies.addTab(tabActionMovies.newTab().setText("Action"));
        tabActionMovies.addTab(tabActionMovies.newTab().setText("Adventure"));
        tabActionMovies.addTab(tabActionMovies.newTab().setText("Comedy"));
        tabActionMovies.addTab(tabActionMovies.newTab().setText("Romance"));
        tabActionMovies.addTab(tabActionMovies.newTab().setText("Horror"));
        tabActionMovies.setTabGravity(TabLayout.GRAVITY_FILL);
        tabActionMovies.setTabTextColors(ColorStateList.valueOf(Color.WHITE));

        tabActionMovies.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getActionMovies();
                        break;
                    case 1:
                        getAdventureMovies();
                        break;
                    case 2:
                        getComedyMovies();
                        break;
                    case 3:
                        getRomanceMovies();
                        break;
                    case 4:
                        getHorrorMovies();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void initSlider() {
        PageSliderAdapter pageSliderAdapter = new PageSliderAdapter(this, (ArrayList<MovieDetails>) uploadsslider);
        sliderpager.setAdapter(pageSliderAdapter);
        pageSliderAdapter.notifyDataSetChanged();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);
        indicator.setupWithViewPager(sliderpager, true);
    }

    public void inViews() {
        tabActionMovies = findViewById(R.id.tabActionMovies);
        sliderpager = findViewById(R.id.slider_pager);
        indicator = findViewById(R.id.indicator);
//        moviesRvWeek = findViewById(R.id.rvMovies_week);
//        MoviesRV = findViewById(R.id.rvMovies);
        tab = findViewById(R.id.tabrecyler);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    @Override
    public void onMovieClick(MovieDetails movieDetails, ImageView imageView) {

    }

    public class SliderTimer extends TimerTask {
        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sliderpager.getCurrentItem() < uploadsslider.size() - 1) {
                        sliderpager.setCurrentItem(sliderpager.getCurrentItem() + 1);
                    } else {
                        sliderpager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    private void getActionMovies() {
        movieShowAdapter = new MovieShowAdapter(this, actionmovies, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void getHorrorMovies() {
        movieShowAdapter = new MovieShowAdapter(this, horrormovies, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void getRomanceMovies() {
        movieShowAdapter = new MovieShowAdapter(this, romancemovie, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void getComedyMovies() {
        movieShowAdapter = new MovieShowAdapter(this, comedymovies, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void getAdventureMovies() {
        movieShowAdapter = new MovieShowAdapter(this, adventuremovie, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }
}