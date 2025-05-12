package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.cscorner.autohub.OBD.Obd_fetch;
import com.cscorner.autohub.UserMechanic.MechanicActivityUser;
import com.cscorner.autohub.UserMechanic.NearestMechanicsActivity;
import com.cscorner.autohub.mechanic.MechanicActivity;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    ImageButton goToWashingPage, goToMechanicPage, expenseManagerButton, obd , geoFenceStatus;
    ImageView profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.main_page); // Linking main_page.xml

        // Initialize UI elements
        expenseManagerButton = findViewById(R.id.expenseManager);
        goToWashingPage = findViewById(R.id.washingCenter);
        goToMechanicPage = findViewById(R.id.mechanic);
        profileButton = findViewById(R.id.imageView10);
        obd = findViewById(R.id.obd);
        geoFenceStatus = findViewById(R.id.geoFenceStatus);

        // Set OnClickListener for Expense Manager
        expenseManagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ExpenseActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for Washing Center
        goToWashingPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Washing.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for Mechanic Assistance
        goToMechanicPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MechanicActivityUser.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for Profile Button
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Profile_userActivity.class);
                startActivity(intent);
            }


        });
        obd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ObdDataActivity_User.class);
                startActivity(intent);
            }
        });

        geoFenceStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GeoFenceActivity_User.class);
                startActivity(intent);
            }
        });
    }
}