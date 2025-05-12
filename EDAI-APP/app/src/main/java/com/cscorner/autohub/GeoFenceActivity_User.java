
package com.cscorner.autohub;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

public class GeoFenceActivity_User extends AppCompatActivity {

    private TextView textLatitude, textLongitude, textStatus;
    private TextView textGeoCenterLat, textGeoCenterLon;
    private EditText editThreshold;
    private Button buttonSetGeofence, buttonSetThreshold;

    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference databaseRef;

    private final String SERVER_URL = "https://autohub-app-mongodbserver.onrender.com/get-latest-geo-fence-data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fence_user);

        // Link views
        textLatitude = findViewById(R.id.textServerLatitude);
        textLongitude = findViewById(R.id.textServerLongitude);
        textStatus = findViewById(R.id.textGeoFenceStatus);
        textGeoCenterLat = findViewById(R.id.textGeoCenterLat);
        textGeoCenterLon = findViewById(R.id.textGeoCenterLon);
        editThreshold = findViewById(R.id.editThreshold);
        buttonSetGeofence = findViewById(R.id.btnSetCurrentLocation);
        buttonSetThreshold = findViewById(R.id.btnSetThreshold);

        // Setup location client and database reference
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        databaseRef = FirebaseDatabase.getInstance().getReference("carSensorData");

        fetchServerData();

        buttonSetGeofence.setOnClickListener(v -> setCurrentLocationAsGeofence());
        buttonSetThreshold.setOnClickListener(v -> {
            String threshold = editThreshold.getText().toString().trim();
            if (!threshold.isEmpty()) {
                databaseRef.child("threshold_loc").setValue(threshold)
                        .addOnSuccessListener(unused ->
                                Toast.makeText(this, "Threshold updated!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to update threshold", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Please enter a threshold value", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchServerData() {
        StringRequest request = new StringRequest(Request.Method.GET, SERVER_URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        String latitude = json.getString("latitude");
                        String longitude = json.getString("longitude");
                        String status = json.getString("geo_fence_status");

                        textLatitude.setText("Latitude: " + latitude);
                        textLongitude.setText("Longitude: " + longitude);
                        textStatus.setText("Status: " + status);

                        if (status.equalsIgnoreCase("Safe")) {
                            textStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        } else {
                            textStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        }

                    } catch (Exception e) {
                        Log.e("GeoFenceFetch", "Parsing error", e);
                        Toast.makeText(this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("GeoFenceFetch", "Server error", error);
                    Toast.makeText(this, "Failed to fetch server data", Toast.LENGTH_SHORT).show();
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,  // timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(request);
    }

    private void setCurrentLocationAsGeofence() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();

                databaseRef.child("latitude_geofence").setValue(String.valueOf(lat));
                databaseRef.child("longitude_geofence").setValue(String.valueOf(lon));

                textGeoCenterLat.setText("Latitude: " + lat);
                textGeoCenterLon.setText("Longitude: " + lon);

                Toast.makeText(this, "Geofence center set to current location!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to get current location", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("GeoFenceLocation", "Location fetch failed", e);
            Toast.makeText(this, "Error getting location", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setCurrentLocationAsGeofence();
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
        }
    }
}

