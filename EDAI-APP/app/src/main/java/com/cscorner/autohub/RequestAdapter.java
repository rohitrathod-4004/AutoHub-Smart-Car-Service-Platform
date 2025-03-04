package com.cscorner.autohub;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private final List<IncomingRequest> requestList;
    private final Context context;
    private String ownerId;

    public RequestAdapter(List<IncomingRequest> requestList, Context context, String ownerId) {
        this.requestList = requestList;
        this.context = context;
        this.ownerId = ownerId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IncomingRequest request = requestList.get(position);

        // Bind data to views
        holder.nameTextView.setText(request.getName() != null ? request.getName() : "Unknown");
        holder.timeSlotTextView.setText(request.getRequestedTimeSlot() != null ? request.getRequestedTimeSlot() : "TimeSlot9");
        holder.addressTextView.setText(request.getAddress() != null ? request.getAddress() : "No Address");
        holder.categoryTextView.setText(request.getServiceType() != null ? request.getServiceType() : "DoorStopWashing");

        // Accept button click
        holder.acceptButton.setOnClickListener(v -> updateStatus(position, "accepted"));

        // Reject button click
        holder.rejectButton.setOnClickListener(v -> updateStatus(position, "rejected"));
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    private void updateStatus(int position, String newStatus) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        IncomingRequest request = requestList.get(position);

        String documentId = request.getDocumentId();
        String userId = request.getUserId();
        String serviceType = request.getServiceType();
        String requestedTimeSlot = request.getRequestedTimeSlot();

        if (documentId == null || ownerId == null) {
            Log.e("RequestAdapter", "Document ID or Owner ID is null!");
            Toast.makeText(context, "Error: Missing data. Cannot process request.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update status in WashingCenterOwners collection
        db.collection("WashingCenterOwners")
                .document(ownerId)
                .collection("IncomingServiceRequest")
                .document(documentId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Request " + newStatus, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e("RequestAdapter", "Failed to update owner status: " + e.getMessage()));

        // Update status in Users collection
        if (userId != null) {
            db.collection("users")
                    .document(userId)
                    .collection("appointments")
                    .document(documentId)
                    .update("status", newStatus)
                    .addOnFailureListener(e -> Log.e("RequestAdapter", "Failed to update user status: " + e.getMessage()));
        }

        // Handle available slots for accepted requests
        if ("accepted".equals(newStatus) && serviceType != null && requestedTimeSlot != null) {
            updateAvailableSlots(db, serviceType, requestedTimeSlot);
        }
    }

    private void updateAvailableSlots(FirebaseFirestore db, String serviceType, String timeSlot) {
        db.collection("WashingCenterOwners")
                .document(ownerId)
                .collection(serviceType)
                .document(timeSlot)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long availableSlots = documentSnapshot.getLong("availableSlots");
                        if (availableSlots != null && availableSlots > 0) {
                            db.collection("WashingCenterOwners")
                                    .document(ownerId)
                                    .collection(serviceType)
                                    .document(timeSlot)
                                    .update("availableSlots", "availableSlots - 1");
                        }
                    }
                });
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, timeSlotTextView, addressTextView, categoryTextView;
        Button acceptButton, rejectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            timeSlotTextView = itemView.findViewById(R.id.timeSlotTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }
}
