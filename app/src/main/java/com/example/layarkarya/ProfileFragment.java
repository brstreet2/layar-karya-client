package com.example.layarkarya;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

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
    private ShapeableImageView userAva;
    private Uri avaUri;
    private StorageReference storageReference;
    private StorageReference storageAva;
    private StorageTask mStorageTask;
    private String avaUrl;
    private DatabaseReference updateDb;
    private String userAvaUrl;
    private CardView uploadPicture;
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
        userAva = profileFragment.findViewById(R.id.userAva);
        uploadPicture = profileFragment.findViewById(R.id.uploadPictureText);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        displayEmail.setText(user.getEmail());

        uploadPicture.setVisibility(View.GONE);

        storageAva = FirebaseStorage.getInstance().getReference().child("user_avatar");
        updateDb = FirebaseDatabase.getInstance("https://layarkarya-65957-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("user_details").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

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

                userAvaUrl = dataSnapshot.child("ava_url").getValue(String.class);

                if (userAvaUrl.equals("")) {
                    userAva.setImageResource(R.drawable.ic_baseline_account_circle_24);
                } else {
                    Picasso.get().load(userAvaUrl).into(userAva);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userAva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFiles();
            }
        });

        uploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseUpload(view);
            }
        });

        return profileFragment;
    }

    public void openFiles() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 102);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 102 && resultCode == getActivity().RESULT_OK && data.getData() != null) {
            avaUri = data.getData();
            try {
                String thumbnailName = getFilename(avaUri);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), avaUri);
                userAva.setImageBitmap(bitmap);
                uploadPicture.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("Range")
    private String getFilename(Uri uri) {
        String res = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    res = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
            if (res == null) {
                res = uri.getPath();
                int cutt = res.lastIndexOf('/');
                if (cutt != -1) {
                    res = res.substring(cutt + 1);
                }
            }
        }

        return res;
    }

    private void uploadFiles() {
        if (avaUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Movie is still uploading...");
            progressDialog.show();
            String avaTitle = UUID.randomUUID().toString();
            final StorageReference storageReference = storageAva.child(avaTitle + "." + getFileExtension(avaUri));
            storageReference.putFile(avaUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            avaUrl = uri.toString();
                            updateDb.child("ava_url").setValue(avaUrl);
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Avatar Uploaded", Toast.LENGTH_SHORT).show();
                            uploadPicture.setVisibility(View.GONE);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");;
                }
            });
        }
    }

    public void firebaseUpload(View view){
        if (uploadPicture.getVisibility() == View.GONE) {
            Toast.makeText(getContext(), "Please select a file!", Toast.LENGTH_SHORT).show();
        } else {
            if (mStorageTask != null && mStorageTask.isInProgress()) {
                Toast.makeText(getContext(), "File is uploading...", Toast.LENGTH_SHORT).show();
            } else {
                uploadFiles();
            }
        }
    }

    public String getFileExtension(Uri uri){
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
}