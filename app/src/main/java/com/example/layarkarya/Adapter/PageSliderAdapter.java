package com.example.layarkarya.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.layarkarya.Model.SideSlider;
import com.example.layarkarya.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PageSliderAdapter extends PagerAdapter {

    public Context context;
    public ArrayList<SideSlider> mList;

    public PageSliderAdapter(Context context, ArrayList<SideSlider> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = (View) inflater.inflate(R.layout.slider_item, null);
        ImageView sliderImage = (ImageView) layout.findViewById(R.id.slider_img);
        TextView sliderTitle = (TextView) layout.findViewById(R.id.slider_title);

        FloatingActionButton floatingActionButton = layout.findViewById(R.id.floatingButton);
        Glide.with(context).load(mList.get(position).getMovie_thumbnail()).into(sliderImage);
        sliderTitle.setText(mList.get(position).getMovie_name() + "\n" + mList.get(position).getMovie_description());

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // We'll code something here for the play button (Exoplayer play Media)
            }
        });

        container.addView(layout);

        return layout;
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

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
