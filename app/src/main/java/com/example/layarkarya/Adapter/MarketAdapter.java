package com.example.layarkarya.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layarkarya.Model.ProductModel;
import com.example.layarkarya.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.ViewHolder> {
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private int currCoin;
    public MarketAdapter(Context context, ArrayList<ProductModel> product) {
        this.context = context;
        this.product = product;
    }

    private ArrayList<ProductModel> product;
    private Context context;

    @NonNull
    @Override
    public MarketAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.list_market, parent,false);

        return new MarketAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.productTitle.setText(product.get(position).getProduct_name());
        holder.productDesc.setText(product.get(position).getProduct_description());
        holder.productPrice.setText(String.valueOf(product.get(position).getProduct_price()));
        Picasso.get().load(product.get(position).getProduct_img()).resize(300, 0).into(holder.imgMedia, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                holder.progressBar.setVisibility(View.GONE);
            }
        });

        holder.buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setIcon(R.drawable.ic_baseline_local_grocery_store_24)
                        .setTitle("Purchase Confirmation")
                        .setMessage("Purchase this item for " + String.valueOf(product.get(position).getProduct_price()) + " coin?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAuth = FirebaseAuth.getInstance();
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
                                                int calc = (currCoin - product.get(position).getProduct_price());
                                                final int calcResult = calc;
                                                if (calcResult < 0) {
                                                    new AlertDialog.Builder(context)
                                                            .setIcon(R.drawable.ic_baseline_warning_amber_24)
                                                            .setTitle("Purchase Failed")
                                                            .setMessage("Sorry, you don't have enough coin!")
                                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                                }
                                                            })
                                                            .show();
                                                } else {
                                                    dataSnapshot.getRef().child("coin").setValue(calcResult).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            new AlertDialog.Builder(context)
                                                                    .setIcon(R.drawable.ic_baseline_check_24)
                                                                    .setTitle("Purchase Success")
                                                                    .setMessage("Your code is: " + product.get(position).getProduct_item())
                                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                                        }
                                                                    })
                                                                    .show();
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                            }
                        }).setNegativeButton("No", null)
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return product.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView productTitle;
        private TextView productDesc;
        private TextView productPrice;
        private ImageView imgMedia;
        private ProgressBar progressBar;
        private CardView buyBtn;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = (TextView) itemView.findViewById(R.id.productTitle);
            productDesc = (TextView) itemView.findViewById(R.id.productDesc);
            productPrice = (TextView) itemView.findViewById(R.id.productPrice);
            imgMedia = (ImageView) itemView.findViewById(R.id.productImg);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            buyBtn = (CardView) itemView.findViewById(R.id.btnBuy);
        }
    }
}
