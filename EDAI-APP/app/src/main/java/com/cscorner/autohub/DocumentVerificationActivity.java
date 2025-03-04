package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DocumentVerificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document_verification_page); // Linking document_verification_page.xml

        Button yesButton = findViewById(R.id.yesButton);
        Button skipButton = findViewById(R.id.skipButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to DocumentationActivity
                Intent intent = new Intent(DocumentVerificationActivity.this, DocumentationActivity.class);
                startActivity(intent);
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(DocumentVerificationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
