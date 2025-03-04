package com.cscorner.autohub;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AADHARCardActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageReference storageRef;
    private FirebaseFirestore db;
    private EditText aadharEditText;
    private TextView textView19;
    private Button uploadButton;
    private ImageView previewImageView; // Add this line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aadhar_card); // Linking aadhar_card.xml

        // Initialize Firebase Storage and Firestore
        storageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        // UI Elements
        aadharEditText = findViewById(R.id.editTextText4);
        textView19 = findViewById(R.id.textView19);
        uploadButton = findViewById(R.id.button5);
        previewImageView = findViewById(R.id.imageView2); // Initialize ImageView for preview

        // Set click listener to textView19 to pick an image
        textView19.setOnClickListener(view -> pickImageFromGallery());

        // Set click listener to upload button
        uploadButton.setOnClickListener(view -> {
            String aadharCardNumber = aadharEditText.getText().toString().trim();
            if (aadharCardNumber.isEmpty()) {
                Toast.makeText(AADHARCardActivity.this, "Please enter Aadhaar number", Toast.LENGTH_SHORT).show();
            } else {
                uploadImageToFirebaseStorage(aadharCardNumber); // Call to upload the selected image
            }
        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Aadhaar Card Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            previewImageView.setImageURI(imageUri); // Set the image URI to the ImageView
            Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToFirebaseStorage(String aadharCardNumber) {
        if (imageUri != null) {
            final String uniqueId = UUID.randomUUID().toString(); // Unique ID for image
            final StorageReference imageRef = storageRef.child("images/aadhaar_cards/" + uniqueId + ".jpg");
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            saveImageUrlToFirestore(aadharCardNumber, imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(AADHARCardActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(AADHARCardActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageUrlToFirestore(String aadharCardNumber, String imageUrl) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get the current user ID
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("aadharNumber", aadharCardNumber);
        userMap.put("aadhaarImage", imageUrl);

        db.collection("users").document(userId).set(userMap, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AADHARCardActivity.this, "Aadhaar Card uploaded successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AADHARCardActivity.this, DocumentationActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(AADHARCardActivity.this, "Error uploading Aadhaar Card", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
