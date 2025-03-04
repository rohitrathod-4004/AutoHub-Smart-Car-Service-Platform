package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivityOwner extends AppCompatActivity {

    private ImageButton expenseManagerButton, goToWashingPage;
    private TextView expenseText, washingTextView, notVerified;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.owner_main); // Linking owner_main.xml

        // Initialize Firebase Firestore and Auth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        expenseManagerButton = findViewById(R.id.expenseManager);
        goToWashingPage = findViewById(R.id.washingCenter);
        expenseText = findViewById(R.id.expenseText);
        washingTextView = findViewById(R.id.washingText);
        notVerified = findViewById(R.id.verificationTextView);

        // Initially hide elements
        expenseManagerButton.setVisibility(View.GONE);
        expenseText.setVisibility(View.GONE);
        goToWashingPage.setVisibility(View.GONE);
        washingTextView.setVisibility(View.GONE);
        notVerified.setVisibility(View.GONE);

        // Fetch verification status from Firestore
        fetchVerificationStatus();

        // Set OnClickListeners
        expenseManagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityOwner.this, ExpenseActivityOwner.class);
                startActivity(intent);
            }
        });

        goToWashingPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityOwner.this, WashingPageOwner.class);
                startActivity(intent);
            }
        });
    }

    private void fetchVerificationStatus() {
        String currentUserId = auth.getCurrentUser().getUid(); // Get current user's UID
        DocumentReference docRef = db.collection("WashingCenterOwners").document(currentUserId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String verificationStatus = documentSnapshot.getString("verification");

                if ("accepted".equals(verificationStatus)) {
                    // Show verified user elements
                    expenseManagerButton.setVisibility(View.VISIBLE);
                    expenseText.setVisibility(View.VISIBLE);
                    goToWashingPage.setVisibility(View.VISIBLE);
                    washingTextView.setVisibility(View.VISIBLE);
                    notVerified.setVisibility(View.GONE);
                } else {
                    // Show not verified message
                    notVerified.setVisibility(View.VISIBLE);
                    expenseManagerButton.setVisibility(View.GONE);
                    expenseText.setVisibility(View.GONE);
                    goToWashingPage.setVisibility(View.GONE);
                    washingTextView.setVisibility(View.GONE);
                }
            } else {
                // Handle case where document does not exist
                notVerified.setText("Verification status not found!");
                notVerified.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(e -> {
            // Handle Firestore fetch error
            notVerified.setText("Failed to fetch verification status!");
            notVerified.setVisibility(View.VISIBLE);
        });
    }
}