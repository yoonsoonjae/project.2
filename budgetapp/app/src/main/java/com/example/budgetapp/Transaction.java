package com.example.budgetapp;

public class Transaction implements TransactionItem {
    public int id;
    public String type;
    public int amount;
    public String category;
    public String memo;
    public String date;

    public Transaction(int id, String type, int amount, String category, String memo, String date) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.memo = memo;
        this.date = date;
    }

    @Override
    public int getItemType() {
        return TYPE_TRANSACTION;
    }
}
