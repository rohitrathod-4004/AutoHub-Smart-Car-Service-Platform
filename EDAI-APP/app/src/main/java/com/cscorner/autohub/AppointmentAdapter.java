package com.cscorner.autohub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cscorner.autohub.Appointment;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private Context context;
    private List<Appointment> appointmentList;

    public AppointmentAdapter(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);

        holder.washingCenterNameTextView.setText( appointment.getWashingCenterName());
        holder.timeSlotTextView.setText("Time Slot: " + appointment.getRequestedTimeSlot());
        holder.statusTextView.setText("Status: " + appointment.getStatus());
        holder.serviceTypeTextView.setText("Service type : " + appointment.getServiceType());

        // Dynamically set icon and color based on status
        String status = appointment.getStatus();
        if ("Accepted".equalsIgnoreCase(status)) {
            holder.statusTextView.setTextColor(context.getResources().getColor(R.color.green));
            holder.statusIcon.setImageResource(R.drawable.ic_check);
        } else if ("Rejected".equalsIgnoreCase(status)) {
            holder.statusTextView.setTextColor(context.getResources().getColor(R.color.red));
            holder.statusIcon.setImageResource(R.drawable.ic_close);
        } else {
            holder.statusTextView.setTextColor(context.getResources().getColor(R.color.orange));
            holder.statusIcon.setImageResource(R.drawable.ic_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView washingCenterNameTextView, timeSlotTextView, statusTextView, serviceTypeTextView;
        ImageView statusIcon;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            washingCenterNameTextView = itemView.findViewById(R.id.appointmentCenterName);
            timeSlotTextView = itemView.findViewById(R.id.appointmentTimeSlot);
            statusTextView = itemView.findViewById(R.id.appointmentStatus);
            statusIcon = itemView.findViewById(R.id.statusIcon);
            serviceTypeTextView = itemView.findViewById(R.id.serviceType);
        }
    }
}