package com.cscorner.autohub;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments_history);

        recyclerView = findViewById(R.id.appointmentHistoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        appointmentList = new ArrayList<>();
        adapter = new AppointmentAdapter(this, appointmentList);
        recyclerView.setAdapter(adapter);

        fetchAppointmentHistory();
    }

    private void fetchAppointmentHistory() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("HistoryActivity", "User ID: " + userId); // Check if the user ID is correct
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference appointmentsRef = db.collection("users")
                .document(userId)
                .collection("appointments");

        appointmentsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (DocumentSnapshot doc : querySnapshot) {
                        // Fetch individual fields
                        String selectedTimeslot = doc.getString("requestedTimeSlot");
                        String washingCenterName = doc.getString("washingCenterName");
                        String category = doc.getString("serviceType");
                        String status = doc.getString("status");

                        Log.d("HistoryActivity", "Fetched: " + selectedTimeslot + ", " + status); // Check data
                        // Create an Appointment object
                        Appointment appointment = new Appointment(selectedTimeslot, washingCenterName, status,category );

                        // Add to the list
                        appointmentList.add(appointment);
                    }
                    // Notify adapter about data changes
                    adapter.notifyDataSetChanged();
                }
            } else {
                Log.e("HistoryActivity", "Error fetching data", task.getException());
                // Log or handle the error
            }
        });
    }
}