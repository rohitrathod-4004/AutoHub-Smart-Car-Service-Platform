package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivityAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login);

        // Hardcoded credentials
        final String ADMIN_EMAIL = "adminsars@gmail.com";
        final String ADMIN_PASSWORD = "SARS@123";

        // Get references to views
        EditText emailField = findViewById(R.id.enterEmailLoginadmin);
        EditText passwordField = findViewById(R.id.enterPasswordLoginadmin);
        Button loginButton = findViewById(R.id.login_Buttonadmin);
        ImageView toggleIcon = findViewById(R.id.toggleIconadmin);

        // Handle the login button click
        loginButton.setOnClickListener(v -> {
            String enteredEmail = emailField.getText().toString().trim();
            String enteredPassword = passwordField.getText().toString().trim();

            // Validate email and password
            if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(LoginActivityAdmin.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            } else if (!enteredEmail.equals(ADMIN_EMAIL)) {
                Toast.makeText(LoginActivityAdmin.this, "Incorrect email", Toast.LENGTH_SHORT).show();
            } else if (!enteredPassword.equals(ADMIN_PASSWORD)) {
                Toast.makeText(LoginActivityAdmin.this, "Incorrect password", Toast.LENGTH_SHORT).show();
            } else {
                // Successful login
                Toast.makeText(LoginActivityAdmin.this, "Login successful", Toast.LENGTH_SHORT).show();
//                 Add intent to navigate to another activity if needed
                Intent intent = new Intent(LoginActivityAdmin.this, MainActivityAdmin.class);
                startActivity(intent);
                finish();
            }
        });

        // Optional: Password visibility toggle
        toggleIcon.setOnClickListener(v -> {
            if (passwordField.getInputType() == 129) { // 129: Password type
                passwordField.setInputType(1); // 1: Plain text
                toggleIcon.setImageResource(R.drawable.eye); // Change icon to "eye closed"
            } else {
                passwordField.setInputType(129);
                toggleIcon.setImageResource(R.drawable.eye_closed); // Change icon to "eye open"
            }
            passwordField.setSelection(passwordField.getText().length()); // Keep cursor at end
        });
    }
}