package com.cscorner.autohub.UserMechanic;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cscorner.autohub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class EmergencyActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String userId = "USER_ID_HERE"; // Replace with actual user ID
    private static final int MAX_MECHANICS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        db = FirebaseFirestore.getInstance();

        // Fetch nearest 5 mechanics and send emergency request
        sendEmergencyAlert();
    }

    private void sendEmergencyAlert() {
        CollectionReference mechanicsRef = db.collection("Mechanics");

        mechanicsRef.whereEqualTo("availability", true) // Fetch only available mechanics
                .limit(MAX_MECHANICS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String mechanicId = document.getId();
                            sendRequestToMechanic(mechanicId);
                            count++;
                        }
                        if (count == 0) {
                            Toast.makeText(this, "No available mechanics nearby!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Emergency request sent to " + count + " mechanics.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to find mechanics!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendRequestToMechanic(String mechanicId) {
        CollectionReference requestsRef = db.collection("EmergencyRequests");

        // Create emergency request
        Map<String, Object> request = new HashMap<>();
        request.put("userId", userId);
        request.put("mechanicId", mechanicId);
        request.put("status", "Pending");
        request.put("timestamp", System.currentTimeMillis());

        // Save request to Firestore
        requestsRef.add(request).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentReference requestDoc = task.getResult();
                if (requestDoc != null) {
                    removeRequestAfterTimeout(requestDoc.getId());
                }
            }
        });
    }

    private void removeRequestAfterTimeout(String requestId) {
        new android.os.Handler().postDelayed(() -> {
            db.collection("EmergencyRequests").document(requestId).delete();
        }, 5 * 60 * 1000); // 5 minutes timeout
    }
}
