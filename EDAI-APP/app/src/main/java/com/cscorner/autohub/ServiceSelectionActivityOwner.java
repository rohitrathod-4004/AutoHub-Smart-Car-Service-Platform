package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ServiceSelectionActivityOwner extends AppCompatActivity {

    private CheckBox checkBoxService1, checkBoxService2, checkBoxService3;
    private EditText priceService1, priceService2, priceService3;
    private Button submitButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_selection);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        checkBoxService1 = findViewById(R.id.checkbox_service1);
        checkBoxService2 = findViewById(R.id.checkbox_service2);
        checkBoxService3 = findViewById(R.id.checkbox_service3);

        priceService1 = findViewById(R.id.price_service1);
        priceService2 = findViewById(R.id.price_service2);
        priceService3 = findViewById(R.id.price_service3);

        submitButton = findViewById(R.id.submit_button);

        // Load existing data from Firestore
        fetchAndPopulateData();

        // Toggle price input visibility based on checkbox state
        checkBoxService1.setOnCheckedChangeListener((buttonView, isChecked) ->
                priceService1.setVisibility(isChecked ? View.VISIBLE : View.GONE)
        );

        checkBoxService2.setOnCheckedChangeListener((buttonView, isChecked) ->
                priceService2.setVisibility(isChecked ? View.VISIBLE : View.GONE)
        );

        checkBoxService3.setOnCheckedChangeListener((buttonView, isChecked) ->
                priceService3.setVisibility(isChecked ? View.VISIBLE : View.GONE)
        );

        // Handle submit button click
        submitButton.setOnClickListener(v -> submitServiceData());
    }

    private void fetchAndPopulateData() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("WashingCenterOwners").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Fetch values from Firestore
                        int price1 = documentSnapshot.getLong("doorStepWashing").intValue();
                        int price2 = documentSnapshot.getLong("pickUpReturnWashing").intValue();
                        int price3 = documentSnapshot.getLong("normalWashing").intValue();

                        // Populate UI
                        if (price1 > 0) {
                            checkBoxService1.setChecked(true);
                            priceService1.setVisibility(View.VISIBLE);
                            priceService1.setText(String.valueOf(price1));
                        }

                        if (price2 > 0) {
                            checkBoxService2.setChecked(true);
                            priceService2.setVisibility(View.VISIBLE);
                            priceService2.setText(String.valueOf(price2));
                        }

                        if (price3 > 0) {
                            checkBoxService3.setChecked(true);
                            priceService3.setVisibility(View.VISIBLE);
                            priceService3.setText(String.valueOf(price3));
                        }
                    } else {
                        Toast.makeText(this, "No data found for current user.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch data.", Toast.LENGTH_SHORT).show());
    }

    private void submitServiceData() {
        StringBuilder selectedServices = new StringBuilder("Selected Services:\n");

        // Collect data for the services selected and their prices
        String price1 = null, price2 = null, price3 = null;
        boolean isValid = true;

        if (checkBoxService1.isChecked()) {
            price1 = priceService1.getText().toString().trim();
            if (price1.isEmpty()) {
                Toast.makeText(this, "Please enter price for Car Washing at Doorstep", Toast.LENGTH_SHORT).show();
                isValid = false;
            } else {
                selectedServices.append("Car Washing at Doorstep: ₹").append(price1).append("\n");
            }
        }

        if (checkBoxService2.isChecked()) {
            price2 = priceService2.getText().toString().trim();
            if (price2.isEmpty()) {
                Toast.makeText(this, "Please enter price for Pick up and Return Washing Service", Toast.LENGTH_SHORT).show();
                isValid = false;
            } else {
                selectedServices.append("Pick up and Return Washing Service: ₹").append(price2).append("\n");
            }
        }

        if (checkBoxService3.isChecked()) {
            price3 = priceService3.getText().toString().trim();
            if (price3.isEmpty()) {
                Toast.makeText(this, "Please enter price for Car Washing Appointment", Toast.LENGTH_SHORT).show();
                isValid = false;
            } else {
                selectedServices.append("Car Washing Appointment: ₹").append(price3).append("\n");
            }
        }

        // If there is no valid service selected, return
        if (!isValid) {
            return;
        }

        // Display selected services in a toast
        Toast.makeText(this, selectedServices.toString(), Toast.LENGTH_LONG).show();

        // Prepare data to save in Firestore
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("WashingCenterOwners").document(userId)
                .update(
                        "doorStepWashing", checkBoxService1.isChecked() ? Integer.parseInt(price1) : 0,
                        "pickUpReturnWashing", checkBoxService2.isChecked() ? Integer.parseInt(price2) : 0,
                        "normalWashing", checkBoxService3.isChecked() ? Integer.parseInt(price3) : 0
                )
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Data added successfully, show a toast message
                        Toast.makeText(ServiceSelectionActivityOwner.this, "Services and prices added successfully!", Toast.LENGTH_SHORT).show();

                        // Intent to return to the previous page (initial page)
                        Intent intent = new Intent(ServiceSelectionActivityOwner.this, MainActivityOwner.class);
                        startActivity(intent);

                        // Finish the current activity so user cannot go back to the form
                        finish();
                    } else {
                        Toast.makeText(ServiceSelectionActivityOwner.this, "Failed to add services", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
