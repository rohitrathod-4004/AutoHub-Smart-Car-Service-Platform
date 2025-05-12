package com.cscorner.autohub.mechanic;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cscorner.autohub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MechanicEmergencyRequestsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String mechanicId = null;

    private RecyclerView recyclerView;
    TextView noRequestsTextView;
    private EmergencyRequestAdapter adapter;
    private List<EmergencyRequest> requestList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_emergency_requests);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        noRequestsTextView = findViewById(R.id.noRequestsTextView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EmergencyRequestAdapter(requestList, this);
        recyclerView.setAdapter(adapter);

        fetchMechanicId();
    }

    private void fetchMechanicId() {
        String userUid = auth.getCurrentUser().getUid();
        if (userUid == null) {
            Toast.makeText(this, "Error: Not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("WashingCenterOwners").document(userUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        mechanicId = documentSnapshot.getId();
                        listenForEmergencyRequests();
                    } else {
                        Toast.makeText(this, "Mechanic profile not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch mechanic data!", Toast.LENGTH_SHORT).show());
    }

    private void listenForEmergencyRequests() {
        db.collection("EmergencyRequests")
                .whereEqualTo("status", "Pending")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FirestoreDebug", "Error fetching emergency requests: ", error);
                        return;
                    }

                    requestList.clear();
                    long currentTime = System.currentTimeMillis();

                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            String requestId = doc.getId();
                            String userId = doc.getString("userId");
                            Long timestamp = doc.getLong("timestamp");
                            Long expiryTimestamp = doc.getLong("expiryTimestamp"); // Fetch expiry time

                            if (timestamp == null || expiryTimestamp == null) {
                                Log.e("FirestoreDebug", "Request " + requestId + " has a null timestamp!");
                                continue;
                            }

                            // Check if request is expired
                            if (currentTime >= expiryTimestamp) {
                                markRequestAsTimedOut(requestId);
                            } else {
                                EmergencyRequest request = new EmergencyRequest(requestId, userId, "Pending", timestamp , mechanicId);
                                request.setExpiryTimestamp(expiryTimestamp);
                                requestList.add(request);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    // Show "No Requests Found" if the list is empty
                    if (requestList.isEmpty()) {
                        noRequestsTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        noRequestsTextView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }


    // Mark a request as Timed Out
    private void markRequestAsTimedOut(String requestId) {
        db.collection("EmergencyRequests").document(requestId)
                .update("status", "Timed Out")
                .addOnSuccessListener(aVoid -> Log.d("FirestoreDebug", "Request marked as Timed Out"))
                .addOnFailureListener(e -> Log.e("FirestoreDebug", "Failed to mark as Timed Out", e));
    }


    // Accept Request and Disable Buttons
    public void acceptEmergencyRequest(String requestId) {
        if (requestId == null || mechanicId == null) {
            Toast.makeText(this, "Error: Missing mechanic or request ID!", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference requestRef = db.collection("EmergencyRequests").document(requestId);
        requestRef.update("status", "Accepted", "mechanicId", mechanicId)
                .addOnSuccessListener(aVoid -> {
                    for (EmergencyRequest request : requestList) {
                        if (request.getRequestId().equals(requestId)) {
                            request.setStatus("Accepted");
                            request.setMechanicId(mechanicId);
                            Toast.makeText(this, "Error: Missing mechanic or request ID!", Toast.LENGTH_SHORT).show();

                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to accept request!", Toast.LENGTH_SHORT).show());
    }
}

