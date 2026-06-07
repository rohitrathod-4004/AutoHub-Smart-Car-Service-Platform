package com.cscorner.autohub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class DocumentDetailActivity extends AppCompatActivity {

    private ImageView documentImageView;
    private EditText documentNumberEditText;
    private Button uploadImageButton, saveButton;

    private String imageUrl;
    private String documentNumber;
    private String documentType; // e.g., "aadhaar", "license", "pan"
    private String userId;
    private Uri newImageUri;

    private FirebaseFirestore db;
    private StorageReference storageReference;

    private static final int IMAGE_PICKER_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_detail);

        // Initialize views
        documentImageView = findViewById(R.id.documentImageView);
        documentNumberEditText = findViewById(R.id.documentNumberEditText);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        saveButton = findViewById(R.id.saveButton);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Get data from intent
        Intent intent = getIntent();
        documentType = intent.getStringExtra("documentType");
        userId = intent.getStringExtra("userId");

        // Fetch and display document details
        fetchDocumentDetails();

        // Handle image upload
        uploadImageButton.setOnClickListener(v -> openImagePicker());

        // Handle save changes
        saveButton.setOnClickListener(v -> saveChanges());
    }

    private void fetchDocumentDetails() {
        db.collection("users").document(userId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                // Fetch document-specific fields
                documentNumber = snapshot.getString(documentType + "Number");
                imageUrl = snapshot.getString(documentType + "Image");

                // Set data to views
                if (!TextUtils.isEmpty(documentNumber)) {
                    documentNumberEditText.setText(documentNumber);
                }
                if (!TextUtils.isEmpty(imageUrl)) {
                    Glide.with(this).load(imageUrl).into(documentImageView);
                }
            } else {
                Toast.makeText(this, "No document details found!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch document details.", Toast.LENGTH_SHORT).show();
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == RESULT_OK && data != null) {
            newImageUri = data.getData();
            if (newImageUri != null) {
                documentImageView.setImageURI(newImageUri);
                Log.d("DEBUG", "Picked image URI: " + newImageUri.toString());
            } else {
                Log.e("DEBUG", "Image URI is null");
            }
        }
    }


    private void saveChanges() {
        String newDocumentNumber = documentNumberEditText.getText().toString().trim();

        if (TextUtils.isEmpty(newDocumentNumber)) {
            Toast.makeText(this, "Document number cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // If a new image is uploaded, save it to Firebase Storage
        if (newImageUri != null) {
            uploadNewImage(newDocumentNumber);
        } else {
            // Update Firestore directly
            updateDocumentData(newDocumentNumber, imageUrl);
        }
    }

    private void uploadNewImage(String newDocumentNumber) {
        StorageReference docRef = storageReference.child("images/" + documentType + "/" + userId + "_" + System.currentTimeMillis() + ".jpg");

        docRef.putFile(newImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    docRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String newImageUrl = uri.toString();
                        updateDocumentData(newDocumentNumber, newImageUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to upload image!", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateDocumentData(String documentNumber, String newImageUrl) {
        // Prepare updated data fields
        String fieldNumber = documentType + "Number";
        String fieldImage = documentType + "Image" ;

        if (!TextUtils.isEmpty(documentNumber)) {
            documentNumberEditText.setHint(documentNumber); // Set the fetched document number as the hint
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put(fieldNumber, documentNumber);
        if (newImageUrl != null) {
            updates.put(fieldImage, newImageUrl);
        }

        db.collection("users").document(userId).update(updates)
                .addOnSuccessListener(unused -> {
                    // Update UI after successful Firestore update
                    if (newImageUrl != null) {
                        imageUrl = newImageUrl;
                        Glide.with(this).load(newImageUrl).into(documentImageView);
                    }
                    documentNumberEditText.setText(documentNumber);
                    Toast.makeText(this, "Document updated successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity if needed
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update document!", Toast.LENGTH_SHORT).show();
                });
    }
}
