package com.cscorner.autohub.UserMechanic;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.cscorner.autohub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EmergencyActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String userId;
    private DocumentReference requestRef;
    private static final int TIMEOUT_MS = 5 * 60 * 1000; // 5 minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
            createEmergencyRequest();
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void createEmergencyRequest() {
        long currentTime = System.currentTimeMillis();
        long expiryTime = currentTime + TIMEOUT_MS;

        Map<String, Object> request = new HashMap<>();
        request.put("userId", userId);
        request.put("mechanicId", null);
        request.put("status", "Pending");
        request.put("timestamp", currentTime);
        request.put("expiryTimestamp", expiryTime);

        // Use userId as the document ID
        requestRef = db.collection("EmergencyRequests").document(userId);

        requestRef.set(request).addOnSuccessListener(unused -> {
            startRequestTimeout(expiryTime);
            Toast.makeText(this, "Emergency request sent!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to send emergency request!", Toast.LENGTH_SHORT).show();
        });
    }

    private void startRequestTimeout(long expiryTime) {
        long delay = expiryTime - System.currentTimeMillis();
        new android.os.Handler().postDelayed(() -> {
            if (requestRef != null) {
                requestRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() &&
                            "Pending".equals(documentSnapshot.getString("status"))) {
                        requestRef.delete();
                        Toast.makeText(this, "No mechanic responded. Request removed.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }, delay);
    }
}
