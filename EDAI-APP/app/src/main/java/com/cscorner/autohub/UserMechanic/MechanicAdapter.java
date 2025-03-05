package com.cscorner.autohub.UserMechanic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cscorner.autohub.R;
import com.cscorner.autohub.UserMechanic.MechanicModel;

import java.util.List;

public class MechanicAdapter extends RecyclerView.Adapter<MechanicAdapter.MechanicViewHolder> {

    private List<MechanicModel> mechanicList;
    private Context context;

    public MechanicAdapter(Context context, List<MechanicModel> mechanicList) {
        this.context = context;
        this.mechanicList = mechanicList;
    }

    @NonNull
    @Override
    public MechanicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mechanic, parent, false);
        return new MechanicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MechanicViewHolder holder, int position) {
        MechanicModel mechanic = mechanicList.get(position);
        holder.name.setText(mechanic.getName());
        holder.mobile.setText("Contact: " + mechanic.getMobile());
        holder.distance.setText("Distance: " + String.format("%.2f", mechanic.getDistance()) + " km");

        // Call Mechanic on Button Click
        holder.callButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + mechanic.getMobile()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mechanicList.size();
    }

    public static class MechanicViewHolder extends RecyclerView.ViewHolder {
        TextView name, mobile, distance;
        Button callButton;

        public MechanicViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.mechanicName);
            mobile = itemView.findViewById(R.id.mechanicMobile);
            distance = itemView.findViewById(R.id.mechanicDistance);
            callButton = itemView.findViewById(R.id.callMechanic);
        }
    }
}
