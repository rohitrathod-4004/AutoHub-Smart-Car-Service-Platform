package com.cscorner.autohub;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class AppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noDataTextView;
    private AppointmentHistoryAdapter adapter;
    private List<AppointmentHistory> appointmentList;
    private String ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        // Initialize RecyclerView and no data TextView
        recyclerView = findViewById(R.id.recyclerView);
        noDataTextView = findViewById(R.id.noDataTextView);
        appointmentList = new ArrayList<>();

        // Set up the RecyclerView and adapter
        adapter = new AppointmentHistoryAdapter(this, appointmentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fetchOwnerIdAndAppointments();

        findViewById(R.id.refreshButton).setOnClickListener(v -> fetchOwnerIdAndAppointments());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchOwnerIdAndAppointments();
    }


    private void fetchOwnerIdAndAppointments() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            ownerId = currentUser.getUid();
            fetchAppointments();
        } else {
            noDataTextView.setText("Error: Unable to fetch owner ID");
            noDataTextView.setVisibility(View.VISIBLE);
        }
    }

    private void fetchAppointments() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("WashingCenterOwners")
                .document(ownerId)
                .collection("IncomingServiceRequest")
                .whereIn("status", List.of("accepted", "rejected"))
//                .orderBy("requestedDate", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    appointmentList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            AppointmentHistory appointment = new AppointmentHistory();
                            appointment.setName(doc.getString("name"));
                            appointment.setAddress(doc.getString("address"));
                            appointment.setStatus(doc.getString("status"));
                            appointment.setSelectedTimeSlot(doc.getString("requestedTimeSlot"));

                            appointmentList.add(appointment);
                        }
                        noDataTextView.setVisibility(View.GONE);
                    } else {
                        noDataTextView.setText("No appointments available");
                        noDataTextView.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    noDataTextView.setText("Error fetching data: " + e.getMessage());
                    noDataTextView.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                });
    }

    public void acceptAppointment(String appointmentId, String selectedTimeSlot) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Update the appointment's time slot in the Firestore document
        db.collection("WashingCenterOwners")
                .document(ownerId)
                .collection("IncomingServiceRequest")
                .document(appointmentId)
                .update("selectedTimeSlot", selectedTimeSlot)
                .addOnSuccessListener(aVoid -> {
                    // Update the time slot in the corresponding subcollection
                    updateTimeSlotForCategory(selectedTimeSlot, "Car Washing at Doorstep");
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    private void updateTimeSlotForCategory(String selectedTimeSlot, String category) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String timeSlotPath = getTimeSlotPath(category);

        // Reference to the specific time slot document
        db.collection("WashingCenterOwners")
                .document(ownerId)
                .collection(timeSlotPath)
                .document("Default")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Fetch the current time slot value
                        Integer currentSlotCount = documentSnapshot.getLong(selectedTimeSlot).intValue();

                        // Check if the time slot value is greater than 0
                        if (currentSlotCount > 0) {
                            // Decrease the time slot value by 1
                            db.collection("WashingCenterOwners")
                                    .document(ownerId)
                                    .collection(timeSlotPath)
                                    .document("Default")
                                    .update(selectedTimeSlot, currentSlotCount - 1)
                                    .addOnSuccessListener(aVoid -> {
                                        // Successfully updated time slot
                                        Log.d("TimeSlotUpdate", "Time slot updated successfully");
                                        Toast.makeText(this, "DONE", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("TimeSlotUpdate", "Error updating time slot", e);
                                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Time slot is already 0 or below, do nothing
                            Log.d("TimeSlotUpdate", "Time slot is already 0 or below");
                            Toast.makeText(this, "already 0", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Document does not exist, handle this case if necessary
                        Log.e("TimeSlotUpdate", "Time slot document not found");
                        Toast.makeText(this, "error, doc don't exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("TimeSlotUpdate", "Error fetching time slot document", e);
                    Toast.makeText(this, "error fetching", Toast.LENGTH_SHORT).show();
                });
    }


    private String getTimeSlotPath(String category) {
        switch (category) {
            case "Doorstep":
                return "TimeSlotsDoorStep";
            case "PickupReturn":
                return "TimeSlotsPickupReturn";
            case "Normal":
                return "TimeSlotsNormal";
            default:
                return "";
        }
    }
}
