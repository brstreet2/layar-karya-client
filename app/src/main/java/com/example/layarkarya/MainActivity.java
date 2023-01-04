package com.example.layarkarya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.layarkarya.Adapter.MovieShowAdapter;
import com.example.layarkarya.Adapter.PageSliderAdapter;
import com.example.layarkarya.Model.MovieDetails;
import com.example.layarkarya.Model.MoviesItemClickListenerNew;
import com.example.layarkarya.Model.SideSlider;
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
    private List<MovieDetails> movies, moviesLatest, moviesPopular;
    private List<MovieDetails> actionMovie, horrorMovie, comedyMovie, romanceMovie, adventureMovie;

    private ViewPager sliderPager;
    private List <SideSlider> moviesSlider;
    private TabLayout indicator, tabMovieAction;
    private RecyclerView moviesRv, moviesRvWeek, tab;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
//        btnLogOut = findViewById(R.id.btnLogout);
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        inViews();
        allMovies();
        initPopularMovies();
        initWeekMovies();
        moviesViewTab();
//        btnLogOut.setOnClickListener(view ->{
//            mAuth.signOut();
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//        });

    }

    private void allMovies() {
        movies = new ArrayList<>();
        moviesLatest = new ArrayList<>();
        moviesPopular = new ArrayList<>();
        actionMovie = new ArrayList<>();
        horrorMovie = new ArrayList<>();
        moviesSlider = new ArrayList<>();
        adventureMovie = new ArrayList<>();
        comedyMovie = new ArrayList<>();
        romanceMovie = new ArrayList<>();

        mDatabaseReference = FirebaseDatabase.getInstance("https://layarkarya-65957-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("movies");
        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    MovieDetails movieuploaded = postSnapshot.getValue(MovieDetails.class);
                    SideSlider slide = postSnapshot.getValue(SideSlider.class);
                    if (movieuploaded.getMovie_type().equals("Latest Movie")) {
                        moviesLatest.add(movieuploaded);
                    }

                    else if (movieuploaded.getMovie_type().equals("Popular Movie")) {
                        moviesPopular.add(movieuploaded);
                    }

                    if (movieuploaded.getMovie_slide().equals("Slide Movie")) {
                        moviesSlider.add(slide);
                    }

                    if (movieuploaded.getMovie_category().equals("Action")) {
                        actionMovie.add(movieuploaded);
                    }

                    else if (movieuploaded.getMovie_category().equals("Romance")) {
                        romanceMovie.add(movieuploaded);
                    }

                    if (movieuploaded.getMovie_category().equals("Adventure")) {
                        adventureMovie.add(movieuploaded);
                    }

                    else if (movieuploaded.getMovie_category().equals("Comedy")) {
                        comedyMovie.add(movieuploaded);
                    }

                    if (movieuploaded.getMovie_category().equals("Horror")) {
                        horrorMovie.add(movieuploaded);
                    }

                    movies.add(movieuploaded);
                }
                initSlider();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initSlider() {
        PageSliderAdapter pageSliderAdapter = new PageSliderAdapter(this, (ArrayList<SideSlider>) moviesSlider);
        sliderPager.setAdapter(pageSliderAdapter);
        pageSliderAdapter.notifyDataSetChanged();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);
        indicator.setupWithViewPager(sliderPager, true);
    }

    private void initWeekMovies() {
        movieShowAdapter = new MovieShowAdapter(this, moviesLatest, this);
        moviesRvWeek.setAdapter(movieShowAdapter);
        moviesRvWeek.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void initPopularMovies() {
        movieShowAdapter = new MovieShowAdapter(this, moviesPopular, this);
        moviesRv.setAdapter(movieShowAdapter);
        moviesRv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void moviesViewTab() {
        getActionMovies();
        tabMovieAction.addTab(tabMovieAction.newTab().setText("Action"));
        tabMovieAction.addTab(tabMovieAction.newTab().setText("Adventure"));
        tabMovieAction.addTab(tabMovieAction.newTab().setText("Comedy"));
        tabMovieAction.addTab(tabMovieAction.newTab().setText("Romance"));
        tabMovieAction.addTab(tabMovieAction.newTab().setText("Horror"));
        tabMovieAction.setTabGravity(TabLayout.GRAVITY_FILL);
        tabMovieAction.setTabTextColors(ColorStateList.valueOf(Color.WHITE));

        tabMovieAction.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

    private void inViews() {
        tabMovieAction = findViewById(R.id.tabActionMovies);
        sliderPager = findViewById(R.id.slider_pager);
        indicator = findViewById(R.id.indicator);
        moviesRvWeek = findViewById(R.id.rvMovies_week);
        moviesRv = findViewById(R.id.rvMovies);
        tab = findViewById(R.id.tabRecycler);

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
                    if (sliderPager.getCurrentItem() < moviesSlider.size() - 1) {
                        sliderPager.setCurrentItem(sliderPager.getCurrentItem() + 1);
                    } else {
                        sliderPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    private void getActionMovies() {
        movieShowAdapter = new MovieShowAdapter(this, actionMovie, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void getHorrorMovies() {
        movieShowAdapter = new MovieShowAdapter(this, horrorMovie, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void getRomanceMovies() {
        movieShowAdapter = new MovieShowAdapter(this, romanceMovie, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void getComedyMovies() {
        movieShowAdapter = new MovieShowAdapter(this, comedyMovie, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void getAdventureMovies() {
        movieShowAdapter = new MovieShowAdapter(this, adventureMovie, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }
}