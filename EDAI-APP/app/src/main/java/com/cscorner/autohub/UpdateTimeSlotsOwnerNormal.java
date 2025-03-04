package com.cscorner.autohub;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateTimeSlotsOwnerNormal extends AppCompatActivity {

    private CheckBox[] checkBoxes = new CheckBox[10];
    private EditText[] priceFields = new EditText[10];
    private Button submitButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_time_slots_owner);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        checkBoxes[0] = findViewById(R.id.checkbox_timeslot1);
        checkBoxes[1] = findViewById(R.id.checkbox_timeslot2);
        checkBoxes[2] = findViewById(R.id.checkbox_timeslot3);
        checkBoxes[3] = findViewById(R.id.checkbox_timeslot4);
        checkBoxes[4] = findViewById(R.id.checkbox_timeslot5);
        checkBoxes[5] = findViewById(R.id.checkbox_timeslot6);
        checkBoxes[6] = findViewById(R.id.checkbox_timeslot7);
        checkBoxes[7] = findViewById(R.id.checkbox_timeslot8);
        checkBoxes[8] = findViewById(R.id.checkbox_timeslot9);
        checkBoxes[9] = findViewById(R.id.checkbox_timeslot10);

        priceFields[0] = findViewById(R.id.num_workers_timeslot1);
        priceFields[1] = findViewById(R.id.num_workers_timeslot2);
        priceFields[2] = findViewById(R.id.num_workers_timeslot3);
        priceFields[3] = findViewById(R.id.num_workers_timeslot4);
        priceFields[4] = findViewById(R.id.num_workers_timeslot5);
        priceFields[5] = findViewById(R.id.num_workers_timeslot6);
        priceFields[6] = findViewById(R.id.num_workers_timeslot7);
        priceFields[7] = findViewById(R.id.num_workers_timeslot8);
        priceFields[8] = findViewById(R.id.num_workers_timeslot9);
        priceFields[9] = findViewById(R.id.num_workers_timeslot10);

        submitButton = findViewById(R.id.submit_button);

        // Fetch data and populate UI
        fetchAndPopulateData();

        // Set visibility based on checkbox state
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            checkBoxes[i].setOnCheckedChangeListener((buttonView, isChecked) ->
                    priceFields[finalI].setVisibility(isChecked ? View.VISIBLE : View.GONE)
            );
        }

        // Handle submit button click
        submitButton.setOnClickListener(v -> submitTimeSlotData());
    }

    private void fetchAndPopulateData() {
        String userId = mAuth.getCurrentUser().getUid();

        String[] firebasePaths = {
                "timeSlot1", "timeSlot2", "timeSlot3", "timeSlot4", "timeSlot5",
                "timeSlot6", "timeSlot7", "timeSlot8", "timeSlot9", "timeSlot10"
        };

        db.collection("WashingCenterOwners").document(userId)
                .collection("TimeSlotsNormal").document("Default")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        for (int i = 0; i < 10; i++) {
                            int price = documentSnapshot.contains(firebasePaths[i])
                                    ? documentSnapshot.getLong(firebasePaths[i]).intValue()
                                    : 0;

                            if (price > 0) {
                                checkBoxes[i].setChecked(true);
                                priceFields[i].setVisibility(View.VISIBLE);
                                priceFields[i].setText(String.valueOf(price));
                            }
                        }
                    } else {
                        Toast.makeText(this, "No data found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch data.", Toast.LENGTH_SHORT).show());
    }

    private void submitTimeSlotData() {
        String userId = mAuth.getCurrentUser().getUid();

        String[] firebasePaths = {
                "timeSlot1", "timeSlot2", "timeSlot3", "timeSlot4", "timeSlot5",
                "timeSlot6", "timeSlot7", "timeSlot8", "timeSlot9", "timeSlot10"
        };

        boolean isValid = true;
        for (int i = 0; i < 10; i++) {
            if (checkBoxes[i].isChecked() && priceFields[i].getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter a price for Time Slot " + (i + 1), Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        }

        if (!isValid) return;

        // Prepare data to save
        for (int i = 0; i < 10; i++) {
            int price = checkBoxes[i].isChecked() ? Integer.parseInt(priceFields[i].getText().toString()) : 0;

            db.collection("WashingCenterOwners").document(userId)
                    .collection("TimeSlotsDoorStep").document("Default")
                    .update(firebasePaths[i], price)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Data updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to update data.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
