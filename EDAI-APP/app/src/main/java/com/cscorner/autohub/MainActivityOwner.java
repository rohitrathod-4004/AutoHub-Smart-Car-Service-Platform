package com.cscorner.autohub;

import com.cscorner.autohub.mechanic.MechanicActivity;
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

    private ImageButton expenseManagerButton, goToWashingPage, mechanicAssistance;
    private TextView expenseText, washingTextView, notVerified, mechanicalText;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.owner_main);

        // Initialize Firebase Firestore and Auth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        expenseManagerButton = findViewById(R.id.expenseManager);
        goToWashingPage = findViewById(R.id.washingCenter);
        mechanicAssistance = findViewById(R.id.mechanicAssistance);
        expenseText = findViewById(R.id.expenseText);
        washingTextView = findViewById(R.id.washingText);
        mechanicalText = findViewById(R.id.mechanicalText);
        notVerified = findViewById(R.id.verificationTextView);

        // Initially hide elements
        expenseManagerButton.setVisibility(View.GONE);
        expenseText.setVisibility(View.GONE);
        goToWashingPage.setVisibility(View.GONE);
        washingTextView.setVisibility(View.GONE);
        mechanicAssistance.setVisibility(View.GONE);
        mechanicalText.setVisibility(View.GONE);
        notVerified.setVisibility(View.GONE);

        // Fetch user profession and verification status
        fetchUserDetails();

        // Set OnClickListeners
        expenseManagerButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivityOwner.this, ExpenseActivityOwner.class);
            startActivity(intent);
        });

        goToWashingPage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivityOwner.this, WashingPageOwner.class);
            startActivity(intent);
        });

        mechanicAssistance.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivityOwner.this, MechanicActivity.class);
            startActivity(intent);
        });
    }

    private void fetchUserDetails() {
        String currentUserId = auth.getCurrentUser().getUid(); // Get current user's UID
        DocumentReference docRef = db.collection("WashingCenterOwners").document(currentUserId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String profession = documentSnapshot.getString("profession");
                String verificationStatus = documentSnapshot.getString("verification");

                if ("accepted".equals(verificationStatus)) {
                    // Show verified user elements
                    expenseManagerButton.setVisibility(View.VISIBLE);
                    expenseText.setVisibility(View.VISIBLE);
                    notVerified.setVisibility(View.GONE);

                    if ("Washing Center Owner".equals(profession)) {
                        goToWashingPage.setVisibility(View.VISIBLE);
                        washingTextView.setVisibility(View.VISIBLE);
                    } else if ("Mechanic".equals(profession)) {
                        mechanicAssistance.setVisibility(View.VISIBLE);
                        mechanicalText.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Show not verified message
                    notVerified.setVisibility(View.VISIBLE);
                }
            } else {
                // Handle case where document does not exist
                notVerified.setText("Verification status not found!");
                notVerified.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(e -> {
            // Handle Firestore fetch error
            notVerified.setText("Failed to fetch user details!");
            notVerified.setVisibility(View.VISIBLE);
        });
    }
}
