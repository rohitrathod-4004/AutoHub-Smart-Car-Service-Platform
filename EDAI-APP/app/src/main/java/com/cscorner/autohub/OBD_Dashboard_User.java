
package com.cscorner.autohub;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.*;

import java.util.ArrayList;

public class OBD_Dashboard_User extends AppCompatActivity {

    private TextView textSpeed, textRPM, textCoolant, textFuel;
    private LineChart chart;
    private ArrayList<Entry> speedEntries = new ArrayList<>();
    private ArrayList<Entry> rpmEntries = new ArrayList<>();
    private int pointIndex = 0;

    private final Handler handler = new Handler();
    private final Runnable fetchTask = new Runnable() {
        @Override
        public void run() {
            fetchData();
            handler.postDelayed(this, 5000); // Repeat every 5 sec
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obd_dashboard_activity_user);

        textSpeed = findViewById(R.id.textSpeed);
        textRPM = findViewById(R.id.textRPM);
        textCoolant = findViewById(R.id.textCoolant);
        textFuel = findViewById(R.id.textFuel);
        chart = findViewById(R.id.chart);

        Description desc = new Description();
        desc.setText("Speed vs RPM");
        chart.setDescription(desc);

        handler.post(fetchTask); // Start periodic fetch
    }

    private void fetchData() {
        String url = "https://autohub-app-mongodbserver.onrender.com/get-latest-data";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String speed = response.getString("speed_kph");
                        String rpm = response.getString("rpm");
                        String coolant = response.getString("coolant_temp_c");
                        String fuel = response.getString("fuel_level_pct");

                        textSpeed.setText("Speed: " + speed + " km/h");
                        textRPM.setText("RPM: " + rpm);
                        textCoolant.setText("Coolant Temp: " + coolant + " °C");
                        textFuel.setText("Fuel Level: " + fuel + "%");

                        float speedVal = Float.parseFloat(speed);
                        float rpmVal = Float.parseFloat(rpm);
                        speedEntries.add(new Entry(pointIndex, speedVal));
                        rpmEntries.add(new Entry(pointIndex, rpmVal));
                        pointIndex++;

                        LineDataSet speedSet = new LineDataSet(speedEntries, "Speed");
                        LineDataSet rpmSet = new LineDataSet(rpmEntries, "RPM");
                        speedSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
                        rpmSet.setColor(getResources().getColor(android.R.color.holo_red_dark));

                        LineData data = new LineData(speedSet, rpmSet);
                        chart.setData(data);
                        chart.invalidate(); // Refresh chart

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(fetchTask); // Stop updates
    }
}

