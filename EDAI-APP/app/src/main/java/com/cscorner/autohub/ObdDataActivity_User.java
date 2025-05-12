package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

public class ObdDataActivity_User extends AppCompatActivity {

    // Define TextViews for telemetry data display
    TextView textSpeed, textRPM, textCoolant, textThrottle, textMAF, textOdometer, textFuel, textFault, textGeoFence, textTimestamp;
    Handler handler = new Handler();
    Runnable fetchTask;
    private static final String TAG = "ObdDataActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obd_data_user);

        initViews();  // Initialize the views
        startFetching();  // Start periodic data fetching

        // Set up the button to navigate to the dashboard
        findViewById(R.id.buttonDashboard).setOnClickListener(v -> {
            startActivity(new Intent(this, OBD_Dashboard_User.class)); // Navigate to Dashboard
        });
    }

    // Initialize the TextViews
    private void initViews() {
        textSpeed = findViewById(R.id.textSpeed);
        textRPM = findViewById(R.id.textRPM);
        textCoolant = findViewById(R.id.textCoolant);
        textThrottle = findViewById(R.id.textThrottle);
        textMAF = findViewById(R.id.textMAF);
        textOdometer = findViewById(R.id.textOdometer);
        textFuel = findViewById(R.id.textFuel);
        textFault = findViewById(R.id.textFault);
        textGeoFence = findViewById(R.id.textGeoFence);
        textTimestamp = findViewById(R.id.textTimestamp);
    }

    // Start periodic data fetching
    private void startFetching() {
        fetchTask = new Runnable() {
            @Override
            public void run() {
                fetchData();
                handler.postDelayed(this, 5000); // Repeat every 5 seconds
            }
        };
        handler.post(fetchTask);
    }

    // Fetch data from the server
    private void fetchData() {
        String url = "https://autohub-app-mongodbserver.onrender.com/get-latest-data";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d(TAG, "API_RAW: " + response);

                    try {
                        // Parse the JSON response
                        JSONObject json = new JSONObject(response);

                        // Update TextViews with fetched data
                        textSpeed.setText("Speed: " + json.getString("speed_kph") + " km/h");
                        textRPM.setText("RPM: " + json.getString("rpm"));
                        textCoolant.setText("Coolant Temp: " + json.getString("coolant_temp_c") + " °C");
                        textThrottle.setText("Throttle: " + json.getString("throttle_pos_pct") + " %");
                        textMAF.setText("MAF: " + json.getString("maf_gs") + " g/s");
                        textOdometer.setText("Odometer: " + json.getString("odometer_km") + " km");
                        textFuel.setText("Fuel Level: " + json.getString("fuel_level_pct") + " %");
                        textFault.setText("Fault: " + json.getString("fault_code") + " - " + json.getString("fault_description"));
                        textGeoFence.setText("Geo Fence Status: " + json.getString("geo_fence_status"));
                        textTimestamp.setText("Timestamp: " + json.getString("timestamp"));
                    } catch (Exception e) {
                        Log.e(TAG, "JSON Parsing Error: ", e);
                        showErrorText("Invalid data format");
                    }
                },
                error -> {
                    Log.e(TAG, "Volley Error: ", error);
                    showErrorText("Failed to load data");
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    // Display error message if data fetch fails
    private void showErrorText(String message) {
        textSpeed.setText(message);
        textRPM.setText("");
        textCoolant.setText("");
        textThrottle.setText("");
        textMAF.setText("");
        textOdometer.setText("");
        textFuel.setText("");
        textFault.setText("");
        textGeoFence.setText("");
        textTimestamp.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(fetchTask); // Stop updates when activity is destroyed
    }
}
