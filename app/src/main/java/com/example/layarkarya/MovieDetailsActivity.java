package com.example.layarkarya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.layarkarya.Adapter.MovieShowAdapter;
import com.example.layarkarya.Model.MovieDetails;
import com.example.layarkarya.Model.MoviesItemClickListenerNew;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity implements MoviesItemClickListenerNew {
    private ImageView movieThumbnail, movieCoverImg;
    public TextView tvTitle, tvDescription;
    public FloatingActionButton play_fab;
    public RecyclerView rvCast, rvSimilarMovie;

    private MovieShowAdapter movieShowAdapter;
    private DatabaseReference mDatabaseReference;

    private ArrayList<MovieDetails> uploads, actionmovie, horrormovie, comedymovie, romancemovie, adventuremovie;

    public String currentMovieUrl;
    public String currentMovieCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        inView();
        similarMovieRecycler();
        similarMovie();
    }

    private void similarMovie() {
        if (currentMovieCategory.equals("Action")) {
            movieShowAdapter = new MovieShowAdapter(this, actionmovie, this);

            rvSimilarMovie.setAdapter(movieShowAdapter);
            rvSimilarMovie.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            movieShowAdapter.notifyDataSetChanged();
        }

        if (currentMovieCategory.equals("Horror")) {
            movieShowAdapter = new MovieShowAdapter(this, horrormovie, this);

            rvSimilarMovie.setAdapter(movieShowAdapter);
            rvSimilarMovie.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            movieShowAdapter.notifyDataSetChanged();
        }

        if (currentMovieCategory.equals("Romance")) {
            movieShowAdapter = new MovieShowAdapter(this, romancemovie, this);

            rvSimilarMovie.setAdapter(movieShowAdapter);
            rvSimilarMovie.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            movieShowAdapter.notifyDataSetChanged();
        }

        if (currentMovieCategory.equals("Adventure")) {
            movieShowAdapter = new MovieShowAdapter(this, adventuremovie, this);

            rvSimilarMovie.setAdapter(movieShowAdapter);
            rvSimilarMovie.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            movieShowAdapter.notifyDataSetChanged();
        }

        if (currentMovieCategory.equals("Comedy")) {
            movieShowAdapter = new MovieShowAdapter(this, comedymovie, this);

            rvSimilarMovie.setAdapter(movieShowAdapter);
            rvSimilarMovie.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            movieShowAdapter.notifyDataSetChanged();
        }
    }

    private void similarMovieRecycler() {
        uploads = new ArrayList<>();
        horrormovie = new ArrayList<>();
        actionmovie = new ArrayList<>();
        comedymovie = new ArrayList<>();
        romancemovie = new ArrayList<>();
        adventuremovie = new ArrayList<>();

        mDatabaseReference = FirebaseDatabase.getInstance("https://layarkarya-65957-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("movies");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postsnapshot : snapshot.getChildren()) {
                    MovieDetails upload = postsnapshot.getValue(MovieDetails.class);
                    if (upload.getMovie_category().equals("Action")) {
                        actionmovie.add(upload);
                    }

                    if (upload.getMovie_category().equals("Horror")) {
                        horrormovie.add(upload);
                    }

                    if (upload.getMovie_category().equals("Comedy")) {
                        comedymovie.add(upload);
                    }

                    if (upload.getMovie_category().equals("Romance")) {
                        romancemovie.add(upload);
                    }

                    if (upload.getMovie_category().equals("Adventure")) {
                        adventuremovie.add(upload);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inView() {
        play_fab = findViewById(R.id.play_fab);
        tvTitle = findViewById(R.id.details_movie_title);
        tvDescription = findViewById(R.id.detail_movie_desc);
        movieThumbnail = findViewById(R.id.details_movie_img);
        movieCoverImg = findViewById(R.id.details_movie_cover);
        rvSimilarMovie = findViewById(R.id.recycler_similar_movies);
        String movieTitle = getIntent().getExtras().getString("title");
        String imgURL = getIntent().getExtras().getString("imgURL");
        String imageCover = getIntent().getExtras().getString("imgCover");
        String moviesDetailText = getIntent().getExtras().getString("movieDetails");
        String movieUrl = getIntent().getExtras().getString("movieUrl");
        String movieCategory = getIntent().getExtras().getString("movieCategory");
        currentMovieUrl = movieUrl;
        currentMovieCategory = movieCategory;
        Glide.with(this).load(imgURL).into(movieThumbnail);
        Glide.with(this).load(imageCover).into(movieCoverImg);
        tvTitle.setText(movieTitle);
        tvDescription.setText(moviesDetailText);
        getSupportActionBar().setTitle(movieTitle);
    }

    @Override
    public void onMovieClick(MovieDetails movieDetails, ImageView imageView) {
        tvTitle.setText(movieDetails.getMovie_name());
        getSupportActionBar().setTitle(movieDetails.getMovie_name());
        Glide.with(this).load(movieDetails.getMovie_thumbnail()).into(movieThumbnail);
        Glide.with(this).load(movieDetails.getMovie_thumbnail()).into(movieCoverImg);
        tvDescription.setText(movieDetails.getMovie_description());
        currentMovieUrl = movieDetails.getMovie_url();
        currentMovieCategory = movieDetails.getMovie_category();
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MovieDetailsActivity.this,
                imageView, "sharedName");
        options.toBundle();
    }
}