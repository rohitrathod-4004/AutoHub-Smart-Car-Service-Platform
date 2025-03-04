package com.cscorner.autohub.mechanic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.cscorner.autohub.R;

public class MechanicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mechanic_dashboard_main);

        // Initialize Buttons
        Button seeHistory = findViewById(R.id.seeHistory);
        Button beActive = findViewById(R.id.be_active);

        // Set Click Listeners
        seeHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MechanicActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        beActive.setOnClickListener(v -> {
            Intent intent = new Intent(MechanicActivity.this, AvailabilityActivity.class);
            startActivity(intent);
        });
    }
}
