package com.cscorner.autohub;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ExpenseHistoryActivityOwner extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noDataTextView;
    private ExpenseHistoryAdapter adapter;
    private List<ExpenseHistory> expenseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_history);

        recyclerView = findViewById(R.id.recyclerView);
        noDataTextView = findViewById(R.id.noDataTextView);
        expenseList = new ArrayList<>();

        adapter = new ExpenseHistoryAdapter(this, expenseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fetchExpenseHistory();
    }

    private void fetchExpenseHistory() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("WashingCenterOwners")
                .document(userId)
                .collection("expenseHistory")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    expenseList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        queryDocumentSnapshots.getDocuments().forEach(document -> {
                            ExpenseHistory expense = document.toObject(ExpenseHistory.class);
                            expenseList.add(expense);
                        });
                        noDataTextView.setVisibility(View.GONE);
                    } else {
                        noDataTextView.setText("No expenses found.");
                        noDataTextView.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    noDataTextView.setText("Error fetching data: " + e.getMessage());
                    noDataTextView.setVisibility(View.VISIBLE);
                });
    }
}
