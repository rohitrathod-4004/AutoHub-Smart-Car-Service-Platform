package com.cscorner.autohub;

public class ExpenseHistory {
    private String category;
    private int amount;
    private String time;
    private String date;

    // Default constructor for Firestore
    public ExpenseHistory() {}

    public ExpenseHistory(String category, int amount, String time, String date) {
        this.category = category;
        this.amount = amount;
        this.time = time;
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
