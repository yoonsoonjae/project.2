package com.example.budgetapp;

public interface TransactionItem {
    int TYPE_DATE_HEADER = 0;
    int TYPE_TRANSACTION = 1;

    int getItemType();
}

