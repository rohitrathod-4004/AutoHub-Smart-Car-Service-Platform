package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_TIME_OUT = 3000; // 3 seconds delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        // Delay and navigate to LoginActivity
        new Handler().postDelayed(() -> {
            // Intent to navigate to LoginActivity
            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close the splash screen so that users cannot return to it
        }, SPLASH_SCREEN_TIME_OUT);
    }
}
