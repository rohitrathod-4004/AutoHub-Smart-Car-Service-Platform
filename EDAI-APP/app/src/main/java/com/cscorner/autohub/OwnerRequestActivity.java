package com.cscorner.autohub;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OwnerRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noDataTextView;
    private RequestAdapter adapter;
    private List<IncomingRequest> requestList;
    private String ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_request);

        recyclerView = findViewById(R.id.recyclerView);
        noDataTextView = findViewById(R.id.noDataTextView);
        requestList = new ArrayList<>();

        // Initialize the adapter and RecyclerView
        adapter = new RequestAdapter(requestList, this, null); // Pass null for initial ownerId
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Fetch ownerId and service requests
        fetchOwnerIdAndRequests();

        // Refresh functionality
        findViewById(R.id.refreshButton).setOnClickListener(v -> fetchOwnerIdAndRequests());
    }

    private void fetchOwnerIdAndRequests() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            ownerId = currentUser.getUid(); // Dynamically fetch logged-in user's UID
            adapter.setOwnerId(ownerId); // Pass ownerId to the adapter
            fetchRequests();
        } else {
            noDataTextView.setText("Error: Unable to fetch owner ID");
            noDataTextView.setVisibility(View.VISIBLE);
        }
    }

    private void fetchRequests() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("WashingCenterOwners")
                .document(ownerId)
                .collection("IncomingServiceRequest")
                .whereEqualTo("status", "received") // Fetch only "received" requests
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    requestList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            IncomingRequest request = doc.toObject(IncomingRequest.class);
                            if (request != null) {
                                request.setDocumentId(doc.getId()); // Store document ID
                                requestList.add(request);
                            }
                        }
                        noDataTextView.setVisibility(View.GONE);
                    } else {
                        noDataTextView.setText("No requests available");
                        noDataTextView.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    noDataTextView.setText("Error fetching data: " + e.getMessage());
                    noDataTextView.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                });
    }
}
