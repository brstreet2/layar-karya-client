package com.example.layarkarya;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.layarkarya.Adapter.MovieShowAdapter;
import com.example.layarkarya.Adapter.PageSliderAdapter;
import com.example.layarkarya.Model.MovieDetails;
import com.example.layarkarya.Model.MoviesItemClickListenerNew;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment implements MoviesItemClickListenerNew {
    public MovieShowAdapter movieShowAdapter;
    public FirebaseAuth mAuth;
    public DatabaseReference mDatabaseReference;
    public List<MovieDetails> uploads, uploadsListlatest, uploadsListpopular;
    public List<MovieDetails> actionmovies, horrormovies, comedymovies, romancemovie, adventuremovie;

    public ViewPager sliderpager;
    public ArrayList<MovieDetails> uploadsslider;

    public TabLayout indicator, tabActionMovies;
    public RecyclerView MoviesRV, moviesRvWeek, tab;
    public ProgressDialog progressDialog;
    public View discoverView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiscoverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoverFragment newInstance(String param1, String param2) {
        DiscoverFragment fragment = new DiscoverFragment();
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
        discoverView = inflater.inflate(R.layout.fragment_discover, container, false);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getContext());
        inViews();
        allMovies();
        moviesViewTab();
        return discoverView;
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
        PageSliderAdapter pageSliderAdapter = new PageSliderAdapter(getContext(), (ArrayList<MovieDetails>) uploadsslider);
        sliderpager.setAdapter(pageSliderAdapter);
        pageSliderAdapter.notifyDataSetChanged();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new DiscoverFragment.SliderTimer(), 4000, 6000);
        indicator.setupWithViewPager(sliderpager, true);
    }

    public void inViews() {
        tabActionMovies = discoverView.findViewById(R.id.tabActionMovies);
        sliderpager = discoverView.findViewById(R.id.slider_pager);
        indicator = discoverView.findViewById(R.id.indicator);
//        moviesRvWeek = findViewById(R.id.rvMovies_week);
//        MoviesRV = findViewById(R.id.rvMovies);
        tab = discoverView.findViewById(R.id.tabrecyler);
    }

    public void onMovieClick(MovieDetails movie, ImageView imageView) {
        Fragment movieDetails = new MovieDetailsFragment();
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("title", movie.getMovie_name());
        bundle.putString("imgURL", movie.getMovie_thumbnail());
        bundle.putString("movieDetails", movie.getMovie_description());
        bundle.putString("imgCover", movie.getMovie_thumbnail());
        bundle.putString("movieUrl", movie.getMovie_url());
        bundle.putString("movieCategory", movie.getMovie_category());
        movieDetails.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_container, movieDetails);
        fragmentTransaction.commit();

//        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, imageView, "sharedName");
//        startActivity(intent, options.toBundle());
    }

    public class SliderTimer extends TimerTask {
        @Override
        public void run() {
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
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
    }

    private void getActionMovies() {
        movieShowAdapter = new MovieShowAdapter(getContext(), actionmovies, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void getHorrorMovies() {
        movieShowAdapter = new MovieShowAdapter(getContext(), horrormovies, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void getRomanceMovies() {
        movieShowAdapter = new MovieShowAdapter(getContext(), romancemovie, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void getComedyMovies() {
        movieShowAdapter = new MovieShowAdapter(getContext(), comedymovies, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void getAdventureMovies() {
        movieShowAdapter = new MovieShowAdapter(getContext(), adventuremovie, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }
}