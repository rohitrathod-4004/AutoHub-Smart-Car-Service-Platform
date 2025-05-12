package com.cscorner.autohub.mechanic;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cscorner.autohub.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class EmergencyRequestAdapter extends RecyclerView.Adapter<EmergencyRequestAdapter.ViewHolder> {

    private List<EmergencyRequest> requestList;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public EmergencyRequestAdapter(List<EmergencyRequest> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emergency_request, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmergencyRequest request = requestList.get(position);
        holder.userId.setText("User: " + request.getUserId());
        holder.status.setText("Status: " + request.getStatus());

        long currentTime = System.currentTimeMillis();
        long timeLeft = request.getExpiryTimestamp() - currentTime;

        // Cancel previous timer if exists
        if (holder.countDownTimer != null) {
            holder.countDownTimer.cancel();
        }

        if (timeLeft > 0) {
            holder.countDownTimer = new CountDownTimer(timeLeft, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
                    holder.timer.setText(String.format("Expires in: %dm %ds", minutes, seconds));
                }

                @Override
                public void onFinish() {
                    holder.timer.setText("Expired!");
                    markRequestAsTimedOut(request.getRequestId());
                }
            }.start();
        } else {
            holder.timer.setText("Expired!");
            markRequestAsTimedOut(request.getRequestId());
        }

        holder.acceptRequest.setOnClickListener(v -> acceptRequest(request.getRequestId() , request.getMechanicId()));
        holder.ignoreRequest.setOnClickListener(v -> {
            requestList.remove(position);
            notifyItemRemoved(position);
        });
    }


    // Set the updated request list
    public void setEmergencyRequests(List<EmergencyRequest> newList) {
        requestList.clear();
        requestList.addAll(newList);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userId, status, timer;
        Button acceptRequest, ignoreRequest;
        CountDownTimer countDownTimer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userId = itemView.findViewById(R.id.userId);
            status = itemView.findViewById(R.id.status);
            timer = itemView.findViewById(R.id.timer);
            acceptRequest = itemView.findViewById(R.id.acceptRequest);
            ignoreRequest = itemView.findViewById(R.id.ignoreRequest);
        }
    }

    // Accept Request and update Firestore
    private void acceptRequest(String requestId , String mechanicId) {
        db.collection("EmergencyRequests").document(requestId)
                .update("status", "Accepted")
                .addOnSuccessListener(aVoid -> {
                    // Notify UI
                    notifyDataSetChanged();
                });
        db.collection("EmergencyRequests").document(requestId)
                .update("mechanicId",mechanicId)
                .addOnSuccessListener(aVoid -> {
                    // Notify UI
                    notifyDataSetChanged();
                });
    }

    // Mark a request as Timed Out
    private void markRequestAsTimedOut(String requestId) {
        db.collection("EmergencyRequests").document(requestId)
                .update("status", "Timed Out");
    }
}
