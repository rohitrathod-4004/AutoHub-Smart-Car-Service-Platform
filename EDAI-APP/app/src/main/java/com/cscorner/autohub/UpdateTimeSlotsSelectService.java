package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateTimeSlotsSelectService extends AppCompatActivity {

    private TextView carWashingAppointment, doorStepWashing, pickupReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_time_slots_select_service); // replace with your layout name

        // Initialize the TextViews
        carWashingAppointment = findViewById(R.id.carWashingAppointment);
        doorStepWashing = findViewById(R.id.doorStepWashing);
        pickupReturn = findViewById(R.id.pickupReturn);

        // Set OnClickListener for carWashingAppointment TextView
        carWashingAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to UpdateTimeSlotsOwnerNormal Activity
                Intent intent = new Intent(UpdateTimeSlotsSelectService.this, UpdateTimeSlotsOwnerNormal.class);
                intent.putExtra("serviceType", "Car Washing Appointment");
                startActivity(intent);
            }
        });

        // Set OnClickListener for doorStepWashing TextView
        doorStepWashing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to UpdateTimeSlotsOwnerDoorStep Activity
                Intent intent = new Intent(UpdateTimeSlotsSelectService.this, UpdateTimeSlotsOwnerDoorStep.class);
                intent.putExtra("serviceType", "DoorStep Car Washing");
                startActivity(intent);
            }
        });

        // Set OnClickListener for pickupReturn TextView
        pickupReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to UpdateTimeSlotsOwnerPickupReturn Activity
                Intent intent = new Intent(UpdateTimeSlotsSelectService.this, UpdateTimeSlotsOwnerPickupReturn.class);
                intent.putExtra("serviceType", "Pick Up and Return Service");
                startActivity(intent);
            }
        });
    }
}