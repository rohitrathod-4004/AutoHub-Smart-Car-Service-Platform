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

public class PANCardActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView panCardPreview;
    private EditText panCardNumberInput;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pan_card);

        panCardPreview = findViewById(R.id.imageView3); // Pan card preview ImageView
        panCardNumberInput = findViewById(R.id.editTextText5); // Pan card number input field
        TextView uploadYourDocument = findViewById(R.id.textView22);
        Button continueButton = findViewById(R.id.button6);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();

        // Select image for Pan Card
        uploadYourDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        // Upload Pan Card image and save to Firestore
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String panCardNumber = panCardNumberInput.getText().toString().trim();

                if (imageUri != null && !panCardNumber.isEmpty()) {
                    uploadImageToFirebaseStorage(panCardNumber);
                } else {
                    Toast.makeText(PANCardActivity.this, "Please select an image and enter PAN number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            panCardPreview.setImageURI(imageUri);
        }
    }

    private void uploadImageToFirebaseStorage(final String panCardNumber) {
        final String uniqueId = UUID.randomUUID().toString(); // Unique ID for image
        final StorageReference imageRef = storageRef.child("images/pan_cards/" + uniqueId);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                String downloadUrl = downloadUri.toString();
                                savePanCardInfoToFirestore(panCardNumber, downloadUrl);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PANCardActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void savePanCardInfoToFirestore(String panCardNumber, String imageUrl) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId)
                .update("panCardNumber", panCardNumber, "panCardImage", imageUrl)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PANCardActivity.this, "PAN Card info saved", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PANCardActivity.this, DocumentationActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(PANCardActivity.this, "Error saving info", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
