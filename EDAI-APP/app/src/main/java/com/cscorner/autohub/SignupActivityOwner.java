
package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivityOwner extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String selectedProfession = "Washing Center Owner"; // Default profession

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_signup);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Find views by ID (updated to match XML)
        Button signupButton = findViewById(R.id.btn_signup_owner);
        EditText nameEditText = findViewById(R.id.owner_name);
        EditText emailEditText = findViewById(R.id.owner_email);
        EditText passwordEditText = findViewById(R.id.owner_password);
        EditText phoneEditText = findViewById(R.id.owner_phone);
        EditText shopNameEditText = findViewById(R.id.owner_shop_name);
        EditText shopLocationEditText = findViewById(R.id.owner_shop_location);
        Spinner professionSpinner = findViewById(R.id.profession_spinner);

        // Set up the profession dropdown (Spinner)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.profession_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        professionSpinner.setAdapter(adapter);

        // Capture selected profession
        professionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProfession = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedProfession = "Washing Center Owner"; // Default selection
            }
        });

        // Set click listener for signup
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String shopName = shopNameEditText.getText().toString().trim();
                String shopLocation = shopLocationEditText.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() ||
                        phone.isEmpty() || shopName.isEmpty() || shopLocation.isEmpty()) {
                    Toast.makeText(SignupActivityOwner.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    registerOwner(email, password, name, phone, shopName, shopLocation, selectedProfession);
                }
            }
        });
    }

    private void registerOwner(String email, String password, String name, String phone, String shopName, String shopLocation, String profession) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String ownerId = mAuth.getCurrentUser().getUid();
                        saveOwnerToFirestore(ownerId, name, phone, email, shopName, shopLocation, profession);
                    } else {
                        Toast.makeText(SignupActivityOwner.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveOwnerToFirestore(String ownerId, String name, String phone, String email, String shopName, String shopLocation, String profession) {
        Map<String, Object> ownerData = new HashMap<>();
        ownerData.put("name", name);
        ownerData.put("phone", phone);
        ownerData.put("email", email);
        ownerData.put("shopName", shopName);
        ownerData.put("shopLocation", shopLocation);
        ownerData.put("profession", profession);
        ownerData.put("role", "owner");

        // Add expenses only for Washing Center Owners
        if (profession.equals("Washing Center Owner")) {
            ownerData.put("fuelExpense", 0);
            ownerData.put("tollFineExpense", 0);
            ownerData.put("maintenanceExpense", 0);
            ownerData.put("miscellaneousExpense", 0);
            ownerData.put("totalCurrentMonthExpense", 0);
            ownerData.put("totalExpense", 0);
            ownerData.put("doorStepWashing", 0);
            ownerData.put("pickUpReturnWashing", 0);
            ownerData.put("normalWashing", 0);
            ownerData.put("verification", "received");
        }

        db.collection("WashingCenterOwners").document(ownerId)
                .set(ownerData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (profession.equals("Washing Center Owner")) {
                            createTimeSlotSubcollections(ownerId);
                        } else {
                            handleOtherProfession(ownerId); // Placeholder for other professions
                        }
                        Intent intent = new Intent(SignupActivityOwner.this, MainActivityOwner.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignupActivityOwner.this, "Failed to save owner details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createTimeSlotSubcollections(String ownerId) {
        Map<String, Object> timeSlotData = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            timeSlotData.put("timeSlot" + i, 0);
        }

        db.collection("WashingCenterOwners").document(ownerId)
                .collection("TimeSlotsPickupReturn").document("Default")
                .set(timeSlotData);
        db.collection("WashingCenterOwners").document(ownerId)
                .collection("TimeSlotsDoorStep").document("Default")
                .set(timeSlotData);
        db.collection("WashingCenterOwners").document(ownerId)
                .collection("TimeSlotsNormal").document("Default")
                .set(timeSlotData);
    }

    private void handleOtherProfession(String ownerId) {
        // Future implementation for different professions
        Log.d("Signup", "Other profession selected: " + selectedProfession);
    }
}
