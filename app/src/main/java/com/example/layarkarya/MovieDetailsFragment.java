package com.example.layarkarya;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailsFragment extends Fragment implements MoviesItemClickListenerNew {

    private ImageView movieThumbnail, movieCoverImg;
    public TextView tvTitle, tvDescription;
    public FloatingActionButton play_fab;
    public RecyclerView rvCast, rvSimilarMovie;

    private MovieShowAdapter movieShowAdapter;
    private DatabaseReference mDatabaseReference;

    private ArrayList<MovieDetails> uploads, actionmovie, horrormovie, comedymovie, romancemovie, adventuremovie;

    public String currentMovieUrl;
    public String currentMovieCategory;
    public String movieName;
    public View movieDetailsView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MovieDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieDetailsFragment newInstance(String param1, String param2) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        movieDetailsView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        inView();
        similarMovieRecycler();
        similarMovie();

        play_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Now playing: " + movieName, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), MoviePlayerActivity.class);
                intent.putExtra("movieUri", currentMovieUrl);

                startActivity(intent);
            }
        });
        return movieDetailsView;
    }

    private void similarMovie() {
        if (currentMovieCategory.equals("Action")) {
            movieShowAdapter = new MovieShowAdapter(getContext(), actionmovie, this);

            rvSimilarMovie.setAdapter(movieShowAdapter);
            rvSimilarMovie.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            movieShowAdapter.notifyDataSetChanged();
        }

        if (currentMovieCategory.equals("Horror")) {
            movieShowAdapter = new MovieShowAdapter(getContext(), horrormovie, this);

            rvSimilarMovie.setAdapter(movieShowAdapter);
            rvSimilarMovie.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            movieShowAdapter.notifyDataSetChanged();
        }

        if (currentMovieCategory.equals("Romance")) {
            movieShowAdapter = new MovieShowAdapter(getContext(), romancemovie, this);

            rvSimilarMovie.setAdapter(movieShowAdapter);
            rvSimilarMovie.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            movieShowAdapter.notifyDataSetChanged();
        }

        if (currentMovieCategory.equals("Adventure")) {
            movieShowAdapter = new MovieShowAdapter(getContext(), adventuremovie, this);

            rvSimilarMovie.setAdapter(movieShowAdapter);
            rvSimilarMovie.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            movieShowAdapter.notifyDataSetChanged();
        }

        if (currentMovieCategory.equals("Comedy")) {
            movieShowAdapter = new MovieShowAdapter(getContext(), comedymovie, this);

            rvSimilarMovie.setAdapter(movieShowAdapter);
            rvSimilarMovie.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),
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
        play_fab = movieDetailsView.findViewById(R.id.play_fab);
        tvTitle = movieDetailsView.findViewById(R.id.details_movie_title);
        tvDescription = movieDetailsView.findViewById(R.id.detail_movie_desc);
        movieThumbnail = movieDetailsView.findViewById(R.id.details_movie_img);
        movieCoverImg = movieDetailsView.findViewById(R.id.details_movie_cover);
        rvSimilarMovie = movieDetailsView.findViewById(R.id.recycler_similar_movies);
        Bundle mBundle = new Bundle();
        mBundle = getArguments();
        String movieTitle = mBundle.getString("title");
        String imgURL = mBundle.getString("imgURL");
        String imageCover = mBundle.getString("imgCover");
        String moviesDetailText = mBundle.getString("movieDetails");
        String movieUrl = mBundle.getString("movieUrl");
        String movieCategory = mBundle.getString("movieCategory");
        currentMovieUrl = movieUrl;
        currentMovieCategory = movieCategory;
        movieName = movieTitle;
        Glide.with(this).load(imgURL).into(movieThumbnail);
        Glide.with(this).load(imageCover).into(movieCoverImg);
        tvTitle.setText(movieTitle);
        tvDescription.setText(moviesDetailText);
    }

    @Override
    public void onMovieClick(MovieDetails movieDetails, ImageView imageView) {
        tvTitle.setText(movieDetails.getMovie_name());
        Glide.with(this).load(movieDetails.getMovie_thumbnail()).into(movieThumbnail);
        Glide.with(this).load(movieDetails.getMovie_thumbnail()).into(movieCoverImg);
        tvDescription.setText(movieDetails.getMovie_description());
        currentMovieUrl = movieDetails.getMovie_url();
        currentMovieCategory = movieDetails.getMovie_category();
//        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MovieDetailsActivity.this,
//                imageView, "sharedName");
//        options.toBundle();
    }
}