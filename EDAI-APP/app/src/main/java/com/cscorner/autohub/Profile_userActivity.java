package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile_userActivity extends AppCompatActivity {

    private ImageView profilePhoto;
    private TextView nameTextView, emailTextView, usernameTextView, mobileNoTextView;
    private LinearLayout aadhaarCardLayout, licenseLayout, panCardLayout;
    


    private String aadhaarImageUrl, licenseImageUrl, panCardImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        profilePhoto = findViewById(R.id.profilePhoto);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        mobileNoTextView = findViewById(R.id.mobileNoTextView);
        aadhaarCardLayout = findViewById(R.id.aadhaarCardLayout);
        licenseLayout = findViewById(R.id.licenseLayout);
        panCardLayout = findViewById(R.id.panCardLayout);
        

        fetchUserData();
    }

    private void fetchUserData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Fetch fields
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                String username = documentSnapshot.getString("username");
                String mobileNo = documentSnapshot.getString("mobileNo");
                String profilePhotoUrl = documentSnapshot.getString("profilePhoto");
                aadhaarImageUrl = documentSnapshot.getString("aadhaarImage");
                licenseImageUrl = documentSnapshot.getString("drivingLicenseImage");
                panCardImageUrl = documentSnapshot.getString("panCardImage");

                // Set data to views
                nameTextView.setText(name);
                emailTextView.setText(email);
                usernameTextView.setText(username);
                mobileNoTextView.setText("Mobile: " + mobileNo);

                // Load profile photo
                Glide.with(Profile_userActivity.this)
                        .load(profilePhotoUrl)
                        .placeholder(R.drawable.profile_icon)
                        .into((ImageView) findViewById(R.id.profilePhoto));


                // Handle document image loading using Glide as well

                // Set click listeners for each document layout
                aadhaarCardLayout.setOnClickListener(v -> navigateToDocumentDetail("aadhaar"));
                licenseLayout.setOnClickListener(v -> navigateToDocumentDetail("drivingLicense"));
                panCardLayout.setOnClickListener(v -> navigateToDocumentDetail("panCard"));
            }
        });
    }

    private void navigateToDocumentDetail(String documentType) {
        Intent intent = new Intent(Profile_userActivity.this, DocumentDetailActivity.class);
        intent.putExtra("documentType", documentType);
        intent.putExtra("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        startActivity(intent);
    }
}
