package com.example.layarkarya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.layarkarya.Model.UserDetailsModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileDetailsActivity extends AppCompatActivity {
    private TextInputEditText textPhone, textAddress, textCity, textProvince;
    private CardView btnSubmit;
    public FirebaseAuth mAuth;
    public String currentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        textPhone = findViewById(R.id.textPhone);
        textAddress = findViewById(R.id.textAddress);
        textCity = findViewById(R.id.textCity);
        textProvince = findViewById(R.id.textProvince);
        btnSubmit = findViewById(R.id.btnSubmit);

        mAuth = FirebaseAuth.getInstance();

        currentId = getIntent().getExtras().getString("currentId");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDetails();
            }
        });
    }

    private void createDetails() {
        String phone = textPhone.getText().toString();
        String address = textAddress.getText().toString();
        String city = textCity.getText().toString();
        String province = textProvince.getText().toString();

        if (phone.isEmpty()){
            textPhone.setError("Phone number cannot be empty!");
            textPhone.requestFocus();
        }
        if (address.isEmpty()){
            textAddress.setError("Please enter your address!");
            textAddress.requestFocus();
        }
        if(city.isEmpty()){
            textCity.setError("Enter your city!");
            textCity.requestFocus();
        }
        else if (province.isEmpty()) {
            textProvince.setError("Province cannot be empty!");
            textProvince.requestFocus();
        } else {
            UserDetailsModel userDetailsModel = new UserDetailsModel(phone, address, city, province, "");
            FirebaseDatabase.getInstance("https://layarkarya-65957-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("user_details").child(currentId)
                    .setValue(userDetailsModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ProfileDetailsActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProfileDetailsActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
        }
    }
}