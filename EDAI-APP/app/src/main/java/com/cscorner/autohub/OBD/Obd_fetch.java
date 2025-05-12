package com.cscorner.autohub.OBD;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.cscorner.autohub.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Obd_fetch extends AppCompatActivity {

    private TextView tvData;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obd_data_activity);

        tvData = findViewById(R.id.tvData);
        tvData.setText("Fetching Data...");

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance("https://console.firebase.google.com/project/autohub-339d7").getReference("ESP32/dummyData");

        // Fetch data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    StringBuilder data = new StringBuilder();
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String key = childSnapshot.getKey();
                        Object value = childSnapshot.getValue();
                        data.append(key).append(": ").append(value).append("\n");
                    }
                    tvData.setText(data.toString());
                } else {
                    tvData.setText("No data found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Database Error: " + error.getMessage());
                tvData.setText("Failed to load data.");
            }
        });
    }
}
