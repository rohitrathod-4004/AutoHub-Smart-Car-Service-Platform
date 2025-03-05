package com.cscorner.autohub.UserMechanic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cscorner.autohub.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NearestMechanicsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noMechanicText;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseFirestore db;
    private List<MechanicModel> mechanicList;
    private com.cscorner.autohub.UserMechanic.MechanicAdapter mechanicAdapter;

    private static final int LOCATION_PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_mechanics);

        recyclerView = findViewById(R.id.nearestMechanicRecyclerView);
        noMechanicText = findViewById(R.id.noMechanicText);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mechanicList = new ArrayList<>();
        mechanicAdapter = new com.cscorner.autohub.UserMechanic.MechanicAdapter(NearestMechanicsActivity.this,mechanicList);
        recyclerView.setAdapter(mechanicAdapter);

        fetchUserLocation();
    }

    private void fetchUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double userLat = location.getLatitude();
                            double userLng = location.getLongitude();
                            fetchNearestMechanics(userLat, userLng);
                        } else {
                            Toast.makeText(NearestMechanicsActivity.this, "Failed to get location!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void fetchNearestMechanics(double userLat, double userLng) {
        db.collection("WashingCenterOwners")
                .whereEqualTo("availability", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mechanicList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            double mechLat = document.getDouble("latitude");
                            double mechLng = document.getDouble("longitude");

                            double distance = calculateDistance(userLat, userLng, mechLat, mechLng);
                            if (distance <= 10) { // 10 km radius
                                mechanicList.add(new MechanicModel(
                                        document.getString("name"),
                                        document.getString("mobile"),
                                        distance
                                ));
                            }
                        }

                        if (mechanicList.isEmpty()) {
                            noMechanicText.setVisibility(View.VISIBLE);
                        } else {
                            noMechanicText.setVisibility(View.GONE);
                            mechanicAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0] / 1000; // Convert to KM
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchUserLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
