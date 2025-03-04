package com.cscorner.autohub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppointmentHistoryAdapter extends RecyclerView.Adapter<AppointmentHistoryAdapter.AppointmentViewHolder> {

    private Context context;
    private List<AppointmentHistory> appointmentList;

    public AppointmentHistoryAdapter(Context context, List<AppointmentHistory> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the new CardView layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentHistory appointment = appointmentList.get(position);

        // Bind data to the views
        holder.nameTextView.setText(appointment.getName());
        holder.dateTextView.setText(appointment.getRequestedDate());
        holder.timeTextView.setText(getTimeSlotString(appointment.getSelectedTimeSlot())); // Displaying mapped time slot
        holder.addressTextView.setText(appointment.getAddress());

        // Dynamically set icon based on status
        if ("accepted".equalsIgnoreCase(appointment.getStatus())) {
            holder.statusIcon.setImageResource(R.drawable.ic_check); // Green checkmark icon
        } else if ("rejected".equalsIgnoreCase(appointment.getStatus())) {
            holder.statusIcon.setImageResource(R.drawable.ic_close); // Red cross icon
        }
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, dateTextView, timeTextView, addressTextView;
        ImageView statusIcon;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);

            // Link views from item_appointment_card.xml
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeSlotTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            statusIcon = itemView.findViewById(R.id.statusIcon);
        }
    }

    private String getTimeSlotString(String timeSlot) {
        if (timeSlot == null || timeSlot.isEmpty()) {
            return "Unknown time slot"; // Default message for null or empty values
        }

        switch (timeSlot) {
            case "timeSlot1":
                return "9:00 AM - 10:00 AM";
            case "timeSlot2":
                return "10:00 AM - 11:00 AM";
            case "timeSlot3":
                return "11:00 AM - 12:00 PM";
            case "timeSlot4":
                return "12:00 PM - 1:00 PM";
            case "timeSlot5":
                return "1:00 PM - 2:00 PM";
            case "timeSlot6":
                return "2:00 PM - 3:00 PM";
            case "timeSlot7":
                return "3:00 PM - 4:00 PM";
            case "timeSlot8":
                return "4:00 PM - 5:00 PM";
            case "timeSlot9":
                return "5:00 PM - 6:00 PM";
            case "timeSlot10":
                return "6:00 PM - 7:00 PM";
            default:
                return "Unknown time slot";
        }
    }

}
