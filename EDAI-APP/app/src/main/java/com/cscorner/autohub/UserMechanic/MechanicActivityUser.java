package com.cscorner.autohub.UserMechanic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cscorner.autohub.R;
import com.cscorner.autohub.UserMechanic.EmergencyActivity;
import com.cscorner.autohub.UserMechanic.NearestMechanicsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MechanicActivityUser extends AppCompatActivity {

    private Button nearbyMechanicsBtn, emergencyBtn, emergencyDetailsBtn;
    private FirebaseFirestore db;
    private String userId;
    private DocumentReference requestRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_user);

        nearbyMechanicsBtn = findViewById(R.id.nearbyMechanics);
        emergencyBtn = findViewById(R.id.emergency);
        emergencyDetailsBtn = findViewById(R.id.emergencyDetails);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
        }

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

        // Redirect to EmergencyRequestDetailsActivity
        emergencyDetailsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MechanicActivityUser.this, EmergencyRequestDetailsActivity.class);
            intent.putExtra("USER_ID", userId);  // Pass the userId to the new activity
            startActivity(intent);
        });
    }
}

