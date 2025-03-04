package com.cscorner.autohub;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class VerifyWashingCenters extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WashingCenterAdapterAdmin adapter;
    private List<WashingCenterAdmin> washingCenterList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_washing_centers);

        recyclerView = findViewById(R.id.recyclerViewWashingCenters);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WashingCenterAdapterAdmin(washingCenterList, this);
        recyclerView.setAdapter(adapter);

        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> fetchData());

        db = FirebaseFirestore.getInstance();

        fetchData();
    }

    private void fetchData() {
        db.collection("WashingCenterOwners")
                .whereEqualTo("verification", "received")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    washingCenterList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        WashingCenterAdmin center = document.toObject(WashingCenterAdmin.class);
                        center.setId(document.getId()); // Save document ID for updates
                        washingCenterList.add(center);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(VerifyWashingCenters.this, "Error fetching data", Toast.LENGTH_SHORT).show()
                );
    }
}