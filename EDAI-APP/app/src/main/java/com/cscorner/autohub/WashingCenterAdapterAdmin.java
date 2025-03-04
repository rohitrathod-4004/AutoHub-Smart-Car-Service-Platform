package com.cscorner.autohub;

import android.content.Context;
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

public class WashingCenterAdapterAdmin extends RecyclerView.Adapter<WashingCenterAdapterAdmin.ViewHolder> {

    private List<WashingCenterAdmin> washingCenters;
    private Context context;

    public WashingCenterAdapterAdmin(List<WashingCenterAdmin> washingCenters, Context context) {
        this.washingCenters = washingCenters;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_washing_center_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WashingCenterAdmin center = washingCenters.get(position);
        holder.tvName.setText(center.getName());
        holder.tvMobileNo.setText(center.getMobileNo());
        holder.tvAddress.setText(center.getAddress());

        holder.btnAccept.setOnClickListener(v -> updateVerification(center.getId(), "accepted"));
        holder.btnReject.setOnClickListener(v -> updateVerification(center.getId(), "rejected"));
    }

    @Override
    public int getItemCount() {
        return washingCenters.size();
    }

    private void updateVerification(String documentId, String status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("WashingCenterOwners")
                .document(documentId)
                .update("verification", status)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Status updated to " + status, Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()
                );
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMobileNo, tvAddress;
        Button btnAccept, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvMobileNo = itemView.findViewById(R.id.tvMobileNo);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}