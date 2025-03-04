package com.cscorner.autohub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpenseHistoryAdapter extends RecyclerView.Adapter<ExpenseHistoryAdapter.ExpenseViewHolder> {

    private Context context;
    private List<ExpenseHistory> expenseList;

    public ExpenseHistoryAdapter(Context context, List<ExpenseHistory> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense_card, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        ExpenseHistory expense = expenseList.get(position);

        // Bind data to the views
        holder.categoryTextView.setText(expense.getCategory());
        holder.amountTextView.setText("₹" + expense.getAmount());
        holder.timeTextView.setText(expense.getTime());
        holder.dateTextView.setText(expense.getDate());
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        TextView categoryTextView, amountTextView, timeTextView, dateTextView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            timeTextView = itemView.findViewById(R.id.timeSlotTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
