package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AvailableWashingCentersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private WashingCenterAdapter adapter;
    private List<WashingCenterOwner> ownerList;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.available_washing_centre_rcview);

        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ownerList = new ArrayList<>();
        adapter = new WashingCenterAdapter(ownerList, new WashingCenterAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(WashingCenterOwner owner) {
                // Create an intent to navigate to the WashingCenterDetailActivity
                Intent intent = new Intent(AvailableWashingCentersActivity.this, WashingCenterDetailActivity.class);

                // Pass the document ID of the selected washing center owner
                intent.putExtra("ownerId", owner.getDocumentId());

                // Start the activity
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        // Fetch washing center owners from Firestore
        fetchWashingCenterOwners();
    }

    private void fetchWashingCenterOwners() {
        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("WashingCenterOwners")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Clear the existing data in the ownerList
                            ownerList.clear();

                            // Loop through each document in the query snapshot
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                // Ensure that document ID is included
                                String documentId = document.getId();

                                String name = document.getString("name");
                                String username = document.getString("username");
                                String mobileNo = document.getString("mobileNo");
                                String address = document.getString("address");

                                // Retrieve integer values for service prices, handle null or invalid values
                                int doorStepWashingPrice = parseInteger(document.get("doorStepWashing"));
                                int pickupReturnWashingPrice = parseInteger(document.get("pickUpReturnWashing"));
                                int normalWashingPrice = parseInteger(document.get("normalWashing"));

                                // Create a new WashingCenterOwner object and add it to the list
                                WashingCenterOwner owner = new WashingCenterOwner(
                                        documentId, name, username, mobileNo, address,
                                        doorStepWashingPrice, pickupReturnWashingPrice, normalWashingPrice
                                );
                                ownerList.add(owner);
                            }

                            // Notify the adapter that data has changed
                            adapter.notifyDataSetChanged();
                        } else {
                            // Handle case when the query result is empty
                            showNoDataMessage();
                        }
                    } else {
                        // Handle error
                        task.getException().printStackTrace();
                        showErrorMessage();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void showNoDataMessage() {
        // Show a message or handle no data case if needed
    }

    private void showErrorMessage() {
        // Handle or show an error message in case of failure
    }

    /**
     * Safely parses a Firestore field into an integer.
     * Returns 0 if the field is null or not a valid number.
     */
    private int parseInteger(Object field) {
        if (field instanceof Number) {
            return ((Number) field).intValue();
        }
        return 0; // Default value if the field is null or invalid
    }
}
