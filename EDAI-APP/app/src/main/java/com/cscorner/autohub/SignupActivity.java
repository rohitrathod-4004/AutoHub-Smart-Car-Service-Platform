package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Button signupButton = findViewById(R.id.signUp_button);
        EditText nameEditText = findViewById(R.id.nameSignUp_EditText);
        EditText mobileNoEditText = findViewById(R.id.mobileNoSignUp_EditText);
        EditText emailEditText = findViewById(R.id.emailSignUp_EditText);
        EditText usernameEditText = findViewById(R.id.usernameSignUp_EditText);
        EditText addressEditText = findViewById(R.id.addressSignUp_EditText);
        EditText passwordEditText = findViewById(R.id.passwordSignUp_EditText);
        EditText confirmPasswordEditText = findViewById(R.id.confirmPasswordSignUp_EditText);
        ImageView passwordEyeIcon = findViewById(R.id.imageView6);
        ImageView confirmPasswordEyeIcon = findViewById(R.id.imageView14);

        // Password toggle for visibility
        setupPasswordToggle(passwordEyeIcon, passwordEditText);
        setupPasswordToggle(confirmPasswordEyeIcon, confirmPasswordEditText);

        signupButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String mobileNo = mobileNoEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            boolean allFieldsValid = validateFields(
                    nameEditText, name,
                    mobileNoEditText, mobileNo,
                    emailEditText, email,
                    usernameEditText, username,
                    addressEditText, address,
                    passwordEditText, password,
                    confirmPasswordEditText, confirmPassword
            );
            // Validate input fields
            if (!allFieldsValid) {
                Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                Toast.makeText(SignupActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
            } else {
                checkExistingUser(email, username, mobileNo, password, name, address);
            }
        });
    }
    private boolean validateFields(Object... editTextsAndValues) {
        boolean allValid = true;

        for (int i = 0; i < editTextsAndValues.length; i += 2) {
            EditText editText = (EditText) editTextsAndValues[i];
            String value = (String) editTextsAndValues[i + 1];

            if (TextUtils.isEmpty(value)) {
                editText.setBackgroundResource(R.drawable.edit_text_error_background);
                allValid = false;
            } else if (editText.getId() == R.id.mobileNoSignUp_EditText && !isValidMobileNumber(value)) {
                editText.setBackgroundResource(R.drawable.edit_text_error_background);
                Toast.makeText(this, "Mobile number must be 10 digits", Toast.LENGTH_SHORT).show();
                allValid = false;
            } else if (editText.getId() == R.id.passwordSignUp_EditText && !isValidPassword(value)) {
                editText.setBackgroundResource(R.drawable.edit_text_error_background);
                Toast.makeText(this, "Password must contain at least one uppercase letter, one lowercase letter, and one digit", Toast.LENGTH_SHORT).show();
                allValid = false;
            } else {
                editText.setBackgroundResource(R.drawable.edit_text_success_background);
            }
        }

        return allValid;
    }

    private boolean isValidMobileNumber(String mobileNo) {
        // Check if the mobile number is exactly 10 digits
        return mobileNo.matches("\\d{10}");
    }

    private boolean isValidPassword(String password) {
        // Ensure the password is at least 6 characters long, contains at least one letter, and one digit
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d).{6,}$");
    }

    private void setupPasswordToggle(ImageView toggleIcon, EditText passwordField) {
        toggleIcon.setOnClickListener(new View.OnClickListener() {
            private boolean isPasswordVisible = false;

            @Override
            public void onClick(View v) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    passwordField.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    toggleIcon.setImageResource(R.drawable.eye); // Use separate icons for clarity
                } else {
                    passwordField.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    toggleIcon.setImageResource(R.drawable.hidden);
                }
                passwordField.setSelection(passwordField.getText().length());
            }
        });
    }

    private void checkExistingUser(String email, String username, String mobileNo, String password, String name, String address) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        Toast.makeText(SignupActivity.this, "Email already in use", Toast.LENGTH_SHORT).show();
                    } else {
                        db.collection("users")
                                .whereEqualTo("username", username)
                                .get()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful() && task2.getResult() != null && !task2.getResult().isEmpty()) {
                                        Toast.makeText(SignupActivity.this, "Username already in use", Toast.LENGTH_SHORT).show();
                                    } else {
                                        db.collection("users")
                                                .whereEqualTo("mobileNo", mobileNo)
                                                .get()
                                                .addOnCompleteListener(task3 -> {
                                                    if (task3.isSuccessful() && task3.getResult() != null && !task3.getResult().isEmpty()) {
                                                        Toast.makeText(SignupActivity.this, "Mobile number already in use", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        registerUser(email, password, name, username, mobileNo, address);
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    private void registerUser(String email, String password, String name, String username, String mobileNo, String address) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("email", email);
                        user.put("username", username);
                        user.put("mobileNo", mobileNo);
                        user.put("address", address);

                        db.collection("users")
                                .document(mAuth.getCurrentUser().getUid())
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(SignupActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();

                                    // Redirect to documentation page
                                    Intent intent = new Intent(SignupActivity.this, DocumentVerificationActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(SignupActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                    Log.e("SignupActivity", "Error saving user data", e);
                                });
                    } else {
                        Toast.makeText(SignupActivity.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}