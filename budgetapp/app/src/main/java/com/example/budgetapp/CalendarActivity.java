package com.example.budgetapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView;
    RecyclerView rvTransactions;
    TransactionAdapter adapter;
    List<Transaction> transactionList;
    DBHelper dbHelper;

    TextView tvIncome, tvExpense, tvNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        rvTransactions = findViewById(R.id.rvTransactions);
        tvIncome = findViewById(R.id.tvIncome);
        tvExpense = findViewById(R.id.tvExpense);
        tvNet = findViewById(R.id.tvNet);

        dbHelper = new DBHelper(this);
        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(transactionList);

        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            showTransactionsByDate(selectedDate);
        });

        findViewById(R.id.btnPrevActivity).setOnClickListener(v -> finish());

        Calendar calendar = Calendar.getInstance();
        String today = String.format("%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));

        calendarView.setDate(calendar.getTimeInMillis(), false, true);

        showTransactionsByDate(today);
    }

    private void showTransactionsByDate(String date) {
        transactionList.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM transactions WHERE date = ? ORDER BY date DESC",
                new String[]{date});

        int totalIncome = 0;
        int totalExpense = 0;

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String type = cursor.getString(1);
            int amount = cursor.getInt(2);
            String category = cursor.getString(3);
            String memo = cursor.getString(4);
            String dateStr = cursor.getString(5);

            transactionList.add(new Transaction(id, type, amount, category, memo, dateStr));

            if ("수입".equals(type)) {
                totalIncome += amount;
            } else if ("지출".equals(type)) {
                totalExpense += amount;
            }
        }
        cursor.close();

        adapter.updateTransactions(transactionList);  // 중요: DateHeader 포함 업데이트
        updateSummary(totalIncome, totalExpense);
    }

    private void updateSummary(int totalIncome, int totalExpense) {
        setColoredText(tvIncome, "수입  ", "+" + totalIncome + "원",
                getResources().getColor(android.R.color.holo_blue_dark));
        setColoredText(tvExpense, "지출  ", "-" + totalExpense + "원",
                getResources().getColor(android.R.color.holo_red_dark));
        setColoredText(tvNet, "합계  ", (totalIncome - totalExpense) + "원", Color.BLACK);
    }

    private void setColoredText(TextView textView, String label, String value, int color) {
        SpannableString spannableString = new SpannableString(label + value);
        spannableString.setSpan(new ForegroundColorSpan(color),
                label.length(), label.length() + value.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
    }
}



