package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.cscorner.autohub.R;

public class MainActivityAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main_page);

        // Get reference to the button
        Button verifyWashingCentersButton = findViewById(R.id.verifyWashingCenters);

        // Set a click listener on the button
        verifyWashingCentersButton.setOnClickListener(v -> {
            // Create an intent to navigate to VerifyWashingCenters
            Intent intent = new Intent(MainActivityAdmin.this, VerifyWashingCenters.class);
            startActivity(intent);
        });
    }
}