package com.example.layarkarya.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.layarkarya.Model.MovieDetails;
import com.example.layarkarya.Model.MoviesItemClickListenerNew;
import com.example.layarkarya.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieShowAdapter extends RecyclerView.Adapter<MovieShowAdapter.MyViewHolder> {
    private Context mContext;
    private List<MovieDetails> uploads;

    MoviesItemClickListenerNew moviesItemClickListenerNew;

    public MovieShowAdapter(Context mContext, List<MovieDetails> uploads, MoviesItemClickListenerNew listener) {
        this.mContext = mContext;
        this.uploads = uploads;
        this.moviesItemClickListenerNew = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_item_new,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieShowAdapter.MyViewHolder holder, int position) {
        MovieDetails movieDetails = uploads.get(position);

        holder.tvTitle.setText(uploads.get(position).getMovie_name());
        Glide.with(mContext).load(uploads.get(position).getMovie_thumbnail()).into(holder.movieImg);
//        Picasso.get().load(movieDetails.getMovie_thumbnail()).into(holder.movieImg);
    }

    @Override
    public int getItemCount() {
        return (uploads != null) ? uploads.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView movieImg;
        ConstraintLayout container;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.item_movie_title);
            movieImg = itemView.findViewById(R.id.item_movie_img);
            container = itemView.findViewById(R.id.container);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moviesItemClickListenerNew.onMovieClick(uploads.get(getAdapterPosition()), movieImg);
                }
            });
        }
    }
}
