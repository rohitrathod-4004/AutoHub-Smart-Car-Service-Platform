package com.cscorner.autohub.UserMechanic;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.cscorner.autohub.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmergencyRequestDetailsActivity extends AppCompatActivity {

    private TextView mechanicNameTextView, mechanicPhoneTextView, requestStatusTextView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_request_details);

        mechanicNameTextView = findViewById(R.id.mechanicName);
        mechanicPhoneTextView = findViewById(R.id.mechanicPhone);
        requestStatusTextView = findViewById(R.id.requestStatus);

        db = FirebaseFirestore.getInstance();

        // Get the userId from Intent passed by MechanicActivityUser
        String userId = getIntent().getStringExtra("USER_ID");

        if (userId != null && !userId.isEmpty()) {
            fetchEmergencyRequestDetails(userId);
        } else {
            Toast.makeText(this, "User ID not provided!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchEmergencyRequestDetails(String userId) {
        DocumentReference requestRef = db.collection("EmergencyRequests").document(userId);

        requestRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String status = documentSnapshot.getString("status");
                String mechanicId = documentSnapshot.getString("mechanicId");

                requestStatusTextView.setText("Request Status: " + (status != null ? status : "Unknown"));

                if (mechanicId != null && !mechanicId.isEmpty()) {
                    fetchMechanicDetails(mechanicId);
                } else {
                    mechanicNameTextView.setText("Mechanic Name: Not Assigned");
                    mechanicPhoneTextView.setText("Phone: N/A");
                }

            } else {
                Toast.makeText(this, "Emergency request not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to fetch emergency request.", Toast.LENGTH_SHORT).show()
        );
    }

    private void fetchMechanicDetails(String mechanicId) {
        DocumentReference mechanicRef = db.collection("WashingCenterOwners").document(mechanicId);

        mechanicRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String mechanicName = documentSnapshot.getString("name");
                String mechanicPhone = documentSnapshot.getString("phone");

                mechanicNameTextView.setText("Mechanic Name: " + (mechanicName != null ? mechanicName : "Unknown"));
                mechanicPhoneTextView.setText("Phone: " + (mechanicPhone != null ? mechanicPhone : "N/A"));
            } else {
                mechanicNameTextView.setText("Mechanic Name: Not Found");
                mechanicPhoneTextView.setText("Phone: N/A");
            }
        }).addOnFailureListener(e -> {
            mechanicNameTextView.setText("Mechanic Name: Error");
            mechanicPhoneTextView.setText("Phone: Error");
        });
    }
}
