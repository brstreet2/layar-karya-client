package com.example.layarkarya.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.layarkarya.Model.MovieDetails;
import com.example.layarkarya.MoviePlayerActivity;
import com.example.layarkarya.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PageSliderAdapter extends PagerAdapter {

    private Context mContext ;
    private ArrayList<MovieDetails> mList ;

    public PageSliderAdapter(Context mContext, ArrayList<MovieDetails> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View slideLayout = inflater.inflate(R.layout.slider_item,null);
        ImageView slideImg = slideLayout.findViewById(R.id.slider_img);
        TextView slideText = slideLayout.findViewById(R.id.slider_title);

        FloatingActionButton floatingActionButton = slideLayout.findViewById(R.id.floatingButton);
        Glide.with(mContext).load(mList.get(position).getMovie_thumbnail()).into(slideImg);
        slideText.setText(mList.get(position).getMovie_name()+"\n"+mList.get(position).getMovie_description());

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String video_url =  mList.get(position).getMovie_url();
                Intent intent = new Intent(mContext , MoviePlayerActivity.class);
                intent.putExtra("movieUri",video_url);
                mContext.startActivity(intent);
            }
        });
        container.addView(slideLayout);
        return slideLayout;
    }

    @Override
    public int getCount() {
        return (mList != null) ? mList.size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

}
