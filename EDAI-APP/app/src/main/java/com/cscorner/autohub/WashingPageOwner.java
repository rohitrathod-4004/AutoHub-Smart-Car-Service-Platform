package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WashingPageOwner extends AppCompatActivity {

    TextView viewAppointments, viewRequest, updateDetails, updateTimeSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_washing_page);

        viewAppointments = findViewById(R.id.textView52);
        viewRequest = findViewById(R.id.textView51);
        updateDetails = findViewById(R.id.updateDetails);
        updateTimeSlots = findViewById(R.id.adjustTimeSlots);

        updateDetails.setOnClickListener(v -> {
            Intent goToServiceSelectionActivityOwner = new Intent(WashingPageOwner.this, ServiceSelectionActivityOwner.class);
            startActivity(goToServiceSelectionActivityOwner);
        });

        viewRequest.setOnClickListener(v -> {
            Intent goToRequestActivity = new Intent(WashingPageOwner.this, OwnerRequestActivity.class);
            startActivity(goToRequestActivity);
        });

        viewAppointments.setOnClickListener(v -> {
            Intent goToAppointmentsActivity = new Intent(WashingPageOwner.this, AppointmentsActivity.class);
            startActivity(goToAppointmentsActivity); // Opens the new RecyclerView
        });
        updateTimeSlots .setOnClickListener(v -> {
            Intent goToAppointmentsActivity = new Intent(WashingPageOwner.this, UpdateTimeSlotsSelectService.class);
            startActivity(goToAppointmentsActivity); // Opens the new RecyclerView
        });
    }
}
