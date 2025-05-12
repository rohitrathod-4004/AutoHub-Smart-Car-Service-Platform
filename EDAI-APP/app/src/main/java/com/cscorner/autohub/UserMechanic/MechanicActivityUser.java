package com.cscorner.autohub.UserMechanic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.cscorner.autohub.R;
import com.cscorner.autohub.UserMechanic.EmergencyActivity;
import com.cscorner.autohub.UserMechanic.NearestMechanicsActivity;

public class MechanicActivityUser extends AppCompatActivity {

    private Button nearbyMechanicsBtn, emergencyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_user);

        nearbyMechanicsBtn = findViewById(R.id.nearbyMechanics);
        emergencyBtn = findViewById(R.id.emergency);

        // Redirect to NearestMechanicActivity
        nearbyMechanicsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MechanicActivityUser.this, NearestMechanicsActivity.class);
            startActivity(intent);
        });

        // Redirect to EmergencyActivity
        emergencyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MechanicActivityUser.this, EmergencyActivity.class);
            startActivity(intent);
        });
    }
}
