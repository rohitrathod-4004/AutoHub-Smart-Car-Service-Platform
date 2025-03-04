package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;

    // Firebase Authentication and Firestore instances
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        // Initialize FirebaseAuth and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Find the fields by their IDs
        emailEditText = findViewById(R.id.enterEmailLogin);
        passwordEditText = findViewById(R.id.enterPasswordLogin);
        Button loginButton = findViewById(R.id.login_Button);
        TextView signupTextView = findViewById(R.id.signUp_Button_Textview);
        TextView areYouAOwner = findViewById(R.id.areYouAOwner);
        ImageView passwordToggle = findViewById(R.id.toggleIcon);
        ImageView adminLogin = findViewById(R.id.adminLogin);

        setupPasswordToggle(passwordToggle, passwordEditText);
        // Set onClickListener for the Login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });


        // Set onClickListener for the Signup TextView
        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SignupActivity for Users
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SignupActivity for Users
                Intent intent = new Intent(LoginActivity.this, LoginActivityAdmin.class);
                startActivity(intent);
            }
        });
        areYouAOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SignupActivity for Users
                Intent intent = new Intent(LoginActivity.this, LoginActivityOwner.class);
                startActivity(intent);
            }
        });
    }

    private void setupPasswordToggle(ImageView toggleIcon, EditText passwordField) {
        toggleIcon.setOnClickListener(new View.OnClickListener() {
            private boolean isPasswordVisible = false;

            public void onClick(View v) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    toggleIcon.setImageResource(R.drawable.eye); // Replace with the visible icon
                } else {
                    passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    toggleIcon.setImageResource(R.drawable.hidden); // Replace with the hidden icon
                }
                passwordField.setSelection(passwordField.getText().length()); // Maintain cursor position
            }
        });
    }

    // Function to handle user login
    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Simple validation
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Please enter email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Please enter password");
            return;
        }

        // Firebase Authentication for login
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Check user role in Firestore
                        String userId = firebaseAuth.getCurrentUser().getUid();
                        firestore.collection("users").document(userId)
                                .get()
                                .addOnCompleteListener(roleTask -> {
                                    if (roleTask.isSuccessful()) {
                                        DocumentSnapshot document = roleTask.getResult();
                                        if (document.exists()) {
                                            String role = document.getString("role");
                                            if ("user".equals(role)) {
                                                // Role matches, navigate to User Dashboard
                                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // Role mismatch
                                                firebaseAuth.signOut();
                                                Toast.makeText(LoginActivity.this, "Access denied: You are not a user", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            firebaseAuth.signOut();
                                            Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Error checking user role", Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        // Login failed
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}