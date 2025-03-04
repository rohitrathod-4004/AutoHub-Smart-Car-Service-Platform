package com.cscorner.autohub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.UUID;

public class DrivingLicenseActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView drivingLicencePreview;
    private EditText drivingLicenceNumberInput;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driving_licence); // Update if your layout name is different

        drivingLicencePreview = findViewById(R.id.drivingLicencePreview);
        drivingLicenceNumberInput = findViewById(R.id.drivingLicenceNumber);
        TextView uploadYourDocument = findViewById(R.id.uploadYourDocument);
        Button continueButton = findViewById(R.id.continue_button);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();

        // Image selection
        uploadYourDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        // Upload image and save to Firestore
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String drivingLicenseNumber = drivingLicenceNumberInput.getText().toString().trim();

                if (imageUri != null && !drivingLicenseNumber.isEmpty()) {
                    uploadImageToFirebaseStorage(drivingLicenseNumber);
                } else {
                    Toast.makeText(DrivingLicenseActivity.this, "Please select an image and enter DL number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            drivingLicencePreview.setImageURI(imageUri);
        }
    }

    private void uploadImageToFirebaseStorage(final String drivingLicenseNumber) {
        final String uniqueId = UUID.randomUUID().toString(); // Create a unique ID for the image
        final StorageReference imageRef = storageRef.child("images/driving_licenses/" + uniqueId);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                String downloadUrl = downloadUri.toString();
                                saveDrivingLicenseInfoToFirestore(drivingLicenseNumber, downloadUrl);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DrivingLicenseActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveDrivingLicenseInfoToFirestore(String drivingLicenseNumber, String imageUrl) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId)
                .update("drivingLicenseNumber", drivingLicenseNumber, "drivingLicenseImage", imageUrl)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(DrivingLicenseActivity.this, "Driving License info saved", Toast.LENGTH_SHORT).show();
                            // Start DocumentationActivity and finish this activity
                            Intent intent = new Intent(DrivingLicenseActivity.this, DocumentationActivity.class);
                            startActivity(intent);
                            finish(); // Close this activity
                        } else {
                            Toast.makeText(DrivingLicenseActivity.this, "Error saving info", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
