package com.cscorner.autohub.mechanic;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.cscorner.autohub.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.Nullable;


public class MechanicEmergencyRequestsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String mechanicId = "MECHANIC_ID_HERE"; // Replace with actual mechanic ID
    private Button acceptRequestBtn;
    private TextView emergencyInfo;
    private String requestId = null; // Holds the active request ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_emergency_requests);

        db = FirebaseFirestore.getInstance();
        emergencyInfo = findViewById(R.id.emergency_info);
        acceptRequestBtn = findViewById(R.id.accept_request);

        // Listen for emergency requests in real-time
        listenForEmergencyRequests();

        acceptRequestBtn.setOnClickListener(v -> {
            if (requestId != null) {
                acceptEmergencyRequest(requestId);
            } else {
                Toast.makeText(this, "No active emergency requests!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenForEmergencyRequests() {
        db.collection("EmergencyRequests")
                .whereEqualTo("mechanicId", mechanicId)
                .whereEqualTo("status", "Pending")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        if (value != null && !value.isEmpty()) {
                            for (QueryDocumentSnapshot doc : value) {
                                requestId = doc.getId();
                                String userId = doc.getString("userId");
                                emergencyInfo.setText("Emergency request from user: " + userId);
                                acceptRequestBtn.setVisibility(View.VISIBLE);
                            }
                        } else {
                            requestId = null;
                            emergencyInfo.setText("No active emergency requests.");
                            acceptRequestBtn.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void acceptEmergencyRequest(String requestId) {
        DocumentReference requestRef = db.collection("EmergencyRequests").document(requestId);

        requestRef.update("status", "Accepted").addOnSuccessListener(aVoid -> {
            Toast.makeText(MechanicEmergencyRequestsActivity.this, "Request accepted!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(MechanicEmergencyRequestsActivity.this, "Failed to accept request!", Toast.LENGTH_SHORT).show();
        });
    }
}
