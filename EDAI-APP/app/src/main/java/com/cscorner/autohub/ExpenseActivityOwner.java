package com.cscorner.autohub;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ExpenseActivityOwner extends AppCompatActivity {

    private ImageView addImage;
    private TextView fuelExpenseView, tollFineExpenseView, maintenanceExpenseView, miscellaneousExpenseView, totalExpenseView, currentMonthExpenseView;
    private TextView viewHistory;
    private ImageButton refetchButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private DocumentReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_page);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        addImage = findViewById(R.id.imageButton2);
        fuelExpenseView = findViewById(R.id.fuelAmount);
        tollFineExpenseView = findViewById(R.id.tollFineAmount);
        maintenanceExpenseView = findViewById(R.id.maintainanceAmount);
        miscellaneousExpenseView = findViewById(R.id.miscellaneousAmount);
        totalExpenseView = findViewById(R.id.totalExpenseAmount);
        currentMonthExpenseView = findViewById(R.id.currentMonthExpenseAmount);
        refetchButton = findViewById(R.id.refetchDataFromDatabase);
        viewHistory = findViewById(R.id.viewHistory);

        viewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExpenseActivityOwner.this, ExpenseHistoryActivityOwner.class);
                startActivity(intent);
            }
        });


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            fetchUserDocument(email);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        addImage.setOnClickListener(v -> openAddExpenseDialog());
        refetchButton.setOnClickListener(v -> fetchExpenseData());
    }

    private void fetchUserDocument(String email) {
        db.collection("WashingCenterOwners").whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        userRef = queryDocumentSnapshots.getDocuments().get(0).getReference();
                        fetchExpenseData();
                    } else {
                        Toast.makeText(this, "No document found for this user", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ExpenseActivity", "Error fetching user document", e);
                    Toast.makeText(this, "Failed to find user document", Toast.LENGTH_LONG).show();
                });
    }

    private void fetchExpenseData() {
        if (userRef != null) {
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    checkAndResetMonthlyExpenses(documentSnapshot);

                    int fuelExpense = documentSnapshot.contains("fuelExpense") ? documentSnapshot.getLong("fuelExpense").intValue() : 0;
                    int tollFineExpense = documentSnapshot.contains("tollFineExpense") ? documentSnapshot.getLong("tollFineExpense").intValue() : 0;
                    int maintenanceExpense = documentSnapshot.contains("maintenanceExpense") ? documentSnapshot.getLong("maintenanceExpense").intValue() : 0;
                    int miscExpense = documentSnapshot.contains("miscellaneousExpense") ? documentSnapshot.getLong("miscellaneousExpense").intValue() : 0;

                    int totalExpense = fuelExpense + tollFineExpense + maintenanceExpense + miscExpense;
                    int totalExpenseFromFirestore = documentSnapshot.contains("totalExpense") ? documentSnapshot.getLong("totalExpense").intValue() : 0;

                    fuelExpenseView.setText(String.valueOf(fuelExpense));
                    tollFineExpenseView.setText(String.valueOf(tollFineExpense));
                    maintenanceExpenseView.setText(String.valueOf(maintenanceExpense));
                    miscellaneousExpenseView.setText(String.valueOf(miscExpense));
                    totalExpenseView.setText(String.valueOf(totalExpenseFromFirestore));
                    currentMonthExpenseView.setText(String.valueOf(totalExpense));
                } else {
                    Toast.makeText(this, "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Log.e("ExpenseActivity", "Error fetching data", e);
                Toast.makeText(this, "Failed to fetch data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        } else {
            Toast.makeText(this, "User document not initialized", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAndResetMonthlyExpenses(DocumentSnapshot documentSnapshot) {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);

        // Get the last updated month from Firestore
        int lastUpdatedMonth = documentSnapshot.contains("lastUpdatedMonth") ? documentSnapshot.getLong("lastUpdatedMonth").intValue() : -1;

        if (currentMonth != lastUpdatedMonth) {
            // Reset expenses
            userRef.update("fuelExpense", 0,
                            "tollFineExpense", 0,
                            "maintenanceExpense", 0,
                            "miscellaneousExpense", 0,
                            "lastUpdatedMonth", currentMonth)
                    .addOnSuccessListener(aVoid -> Log.d("ExpenseActivity", "Monthly expenses reset successfully"))
                    .addOnFailureListener(e -> Log.e("ExpenseActivity", "Failed to reset monthly expenses", e));
        }
    }

    private void openAddExpenseDialog() {
        Dialog dialog = new Dialog(ExpenseActivityOwner.this);
        dialog.setContentView(R.layout.expense_dialogbox);

        EditText edtAmount = dialog.findViewById(R.id.edtAmount);
        Spinner spinner = dialog.findViewById(R.id.spinner);
        Button btnDate = dialog.findViewById(R.id.btnDate);
        Button btnTime = dialog.findViewById(R.id.btnTime);
        Button btnAction = dialog.findViewById(R.id.btnAction);
        TextView tvSelectedDate = dialog.findViewById(R.id.tvSelectedDate);
        TextView tvSelectedTime = dialog.findViewById(R.id.tvSelectedTime);

        String[] categories = {"Fuel", "Maintenance", "Toll / Fine", "Miscellaneous"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinner.setAdapter(adapter);

        btnDate.setOnClickListener(v -> {
            // Open Date Picker Dialog
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        tvSelectedDate.setText("Selected Date: " + selectedDate);
                        tvSelectedDate.setVisibility(View.VISIBLE);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        btnTime.setOnClickListener(v -> {
            // Open Time Picker Dialog
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        String selectedTime = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute);
                        tvSelectedTime.setText("Selected Time: " + selectedTime);
                        tvSelectedTime.setVisibility(View.VISIBLE);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false);
            timePickerDialog.show();
        });

        btnAction.setOnClickListener(v1 -> {
            String amountText = edtAmount.getText().toString();
            String category = spinner.getSelectedItem().toString();

            if (!amountText.isEmpty() && tvSelectedDate.getVisibility() == View.VISIBLE && tvSelectedTime.getVisibility() == View.VISIBLE) {
                int amount = Integer.parseInt(amountText);
                String selectedDate = tvSelectedDate.getText().toString().replace("Selected Date: ", "");
                String selectedTime = tvSelectedTime.getText().toString().replace("Selected Time: ", "");
                updateExpenseInFirebase(category, amount, selectedDate, selectedTime);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please enter amount and select date/time", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void updateExpenseInFirebase(String category, int amount, String selectedDate, String selectedTime) {
        if (userRef == null) {
            Toast.makeText(this, "User document not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        String fieldToUpdate = "";
        switch (category) {
            case "Fuel":
                fieldToUpdate = "fuelExpense";
                break;
            case "Maintenance":
                fieldToUpdate = "maintenanceExpense";
                break;
            case "Toll / Fine":
                fieldToUpdate = "tollFineExpense";
                break;
            case "Miscellaneous":
                fieldToUpdate = "miscellaneousExpense";
                break;
        }

        if (!fieldToUpdate.isEmpty()) {
            String finalFieldToUpdate = fieldToUpdate;
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                int currentExpense = documentSnapshot.contains(finalFieldToUpdate) ? documentSnapshot.getLong(finalFieldToUpdate).intValue() : 0;
                int updatedExpense = currentExpense + amount;

                userRef.update(finalFieldToUpdate, updatedExpense)
                        .addOnSuccessListener(aVoid -> {
                            int totalExpense = (documentSnapshot.contains("totalExpense") ? documentSnapshot.getLong("totalExpense").intValue() : 0) + amount;

                            // Update the total expense
                            userRef.update("totalExpense", totalExpense)
                                    .addOnSuccessListener(aVoid2 -> {
                                        // Add the expense to the expense history subcollection with date and time
                                        addExpenseToHistory(category, amount, selectedDate, selectedTime);
                                        Toast.makeText(this, "Expense and total updated successfully", Toast.LENGTH_SHORT).show();
                                        fetchExpenseData();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("ExpenseActivity", "Failed to update total expense", e);
                                        Toast.makeText(this, "Failed to update total expense: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.e("ExpenseActivity", "Failed to update expense", e);
                            Toast.makeText(this, "Failed to update expense: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            });
        }
    }

    private void addExpenseToHistory(String category, int amount, String selectedDate, String selectedTime) {
        Map<String, Object> expense = new HashMap<>();
        expense.put("category", category);
        expense.put("amount", amount);
        expense.put("date", selectedDate);
        expense.put("time", selectedTime);
        expense.put("timestamp", System.currentTimeMillis());

        userRef.collection("expenseHistory").add(expense)
                .addOnSuccessListener(documentReference -> Log.d("ExpenseActivity", "Expense added to history"))
                .addOnFailureListener(e -> {
                    Log.e("ExpenseActivity", "Failed to add expense to history", e);
                    Toast.makeText(this, "Failed to add expense to history", Toast.LENGTH_SHORT).show();
                });
    }

}