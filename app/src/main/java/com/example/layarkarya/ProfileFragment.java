package com.example.layarkarya;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    public FirebaseAuth mAuth;
    private View profileFragment;
    private DatabaseReference databaseReference;
    private TextView displayName, coinDisplay, displayEmail, contentCountDisplay, movieWatchedDisplay, displayPhone, displayLocation;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        profileFragment = inflater.inflate(R.layout.fragment_profile, container, false);
        displayName = profileFragment.findViewById(R.id.displayName);
        coinDisplay = profileFragment.findViewById(R.id.coinWealth);
        displayEmail = profileFragment.findViewById(R.id.displayEmail);
        displayPhone = profileFragment.findViewById(R.id.phoneDisplay);
        displayLocation = profileFragment.findViewById(R.id.locationDisplay);
        contentCountDisplay = profileFragment.findViewById(R.id.contentCountDisplay);
        movieWatchedDisplay = profileFragment.findViewById(R.id.movieWatchDisplay);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        displayEmail.setText(user.getEmail());

        databaseReference = FirebaseDatabase.getInstance("https://layarkarya-65957-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users")
                .child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String firstName = dataSnapshot.child("fullName").getValue(String.class);
                String lastName = dataSnapshot.child("lastName").getValue(String.class);
                displayName.setText(firstName + " " + lastName);

                int coinWealth = dataSnapshot.child("coin").getValue(int.class);
                coinDisplay.setText(String.valueOf(coinWealth));

                int contentCount = dataSnapshot.child("contentCount").getValue(int.class);
                contentCountDisplay.setText(String.valueOf(contentCount));

                int movieWatchedCount = dataSnapshot.child("movieWatched").getValue(int.class);
                movieWatchedDisplay.setText(String.valueOf(movieWatchedCount));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance("https://layarkarya-65957-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("user_details")
                .child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String phone = dataSnapshot.child("phone").getValue(String.class);
                displayPhone.setText(phone);

                String location = dataSnapshot.child("address").getValue(String.class) + ", "
                        + dataSnapshot.child("city").getValue(String.class) + ", "
                        + dataSnapshot.child("province").getValue(String.class);
                displayLocation.setText(location);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return profileFragment;
    }
}