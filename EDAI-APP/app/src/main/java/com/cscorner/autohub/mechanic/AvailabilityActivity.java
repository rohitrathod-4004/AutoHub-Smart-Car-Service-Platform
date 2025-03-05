package com.cscorner.autohub.mechanic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cscorner.autohub.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class AvailabilityActivity extends AppCompatActivity {

    private Switch availabilitySwitch;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Handler locationUpdateHandler;
    private Runnable locationUpdateRunnable;
    private boolean isTracking = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mechanic_availability_activity);

        // Initialize Firebase and Location services
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationUpdateHandler = new Handler();

        // UI Elements
        availabilitySwitch = findViewById(R.id.availabilitySwitch);
        findViewById(R.id.availabilityText);

        // Fetch stored availability status from Firestore
        fetchAvailabilityStatus();

        // Toggle Switch Listener
        availabilitySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> showConfirmationDialog(isChecked));

        // Location update every 5 minutes if available
        locationUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isTracking) {
                    updateMechanicLocation();
                    locationUpdateHandler.postDelayed(this, 300000); // 5 minutes interval
                }
            }
        };
    }

    private void fetchAvailabilityStatus() {
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        db.collection("WashingCenterOwners").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                boolean availability = documentSnapshot.getBoolean("availability") != null && Boolean.TRUE.equals(documentSnapshot.getBoolean("availability"));
                availabilitySwitch.setChecked(availability);
                availabilitySwitch.setText(availability ? "Online" : "Offline");
            }
        });
    }

    private void showConfirmationDialog(boolean isChecked) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Availability Change")
                .setMessage(isChecked ? "Are you sure you want to go online?" : "Are you sure you want to go offline?")
                .setPositiveButton("Yes", (dialog, which) -> updateAvailability(isChecked))
                .setNegativeButton("No", (dialog, which) -> {
                    availabilitySwitch.setChecked(!isChecked);
                    dialog.dismiss();
                })
                .show();
    }

    private void updateAvailability(boolean isAvailable) {
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        Map<String, Object> updates = new HashMap<>();
        updates.put("availability", isAvailable);

        db.collection("WashingCenterOwners").document(userId).update(updates).addOnSuccessListener(aVoid -> {
            availabilitySwitch.setText(isAvailable ? "Online" : "Offline");
            Toast.makeText(this, "Availability updated", Toast.LENGTH_SHORT).show();
            if (isAvailable) {
                startLocationUpdates();
            } else {
                stopLocationUpdates();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to update availability", Toast.LENGTH_SHORT).show());
    }

    private void startLocationUpdates() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        isTracking = true;
        locationUpdateHandler.post(locationUpdateRunnable);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 5 seconds
        locationRequest.setFastestInterval(2000); // 2 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    updateLocationInDatabase(location);
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        isTracking = false;
        locationUpdateHandler.removeCallbacks(locationUpdateRunnable);
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void updateMechanicLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                updateLocationInDatabase(location);
            }
        });
    }

    private void updateLocationInDatabase(Location location) {
        String userId = auth.getCurrentUser().getUid();
        Map<String, Object> updates = new HashMap<>();
        updates.put("latitude", location.getLatitude());
        updates.put("longitude", location.getLongitude());

        db.collection("WashingCenterOwners").document(userId).update(updates).addOnSuccessListener(aVoid ->
                Toast.makeText(this, "Location Updated", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
}
