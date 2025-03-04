package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    ImageButton goToWashingPage ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.main_page); // Linking main_page.xml

        // Get reference to the ImageButton for expenseManager
        ImageButton expenseManagerButton = findViewById(R.id.expenseManager);
        ImageView profilebutton = findViewById(R.id.imageView10);

        // Set an onClickListener to redirect to ExpenseActivity
        expenseManagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent to go to ExpenseActivity
                Intent intent = new Intent(MainActivity.this, ExpenseActivity.class);
                startActivity(intent);
            }
        });
        goToWashingPage = findViewById(R.id.washingCenter);

        goToWashingPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToWashingPage = new Intent(MainActivity.this, WashingPage.class);
                startActivity(goToWashingPage);

            }
        });
        profilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoProfile = new Intent(MainActivity.this , Profile_userActivity.class);
                startActivity(gotoProfile);
            }
        });

    }
}

