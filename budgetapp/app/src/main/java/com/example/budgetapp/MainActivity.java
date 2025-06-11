package com.example.budgetapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.graphics.Color;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//(캘린더, 통계) 디자인 적용
public class MainActivity extends AppCompatActivity {

    Button btnAdd, btnDelete;
    ImageButton btnPrevMonth, btnNextMonth, btnCalendar, btnChart;
    TextView tvCurrentMonth;
    RecyclerView rvTransactions;
    TransactionAdapter adapter;
    List<Transaction> transactionList = new ArrayList<>();

    Calendar calendar = Calendar.getInstance();
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        btnAdd = findViewById(R.id.btnAdd);
        btnDelete = findViewById(R.id.btnDelete);
        btnCalendar = findViewById(R.id.btnCalendar);
        rvTransactions = findViewById(R.id.rvTransactions);
        rvTransactions.addItemDecoration(new SpacingItemDecoration(16));
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        btnChart = findViewById(R.id.btnChart);

        dbHelper = new DBHelper(this);

        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(transactionList);
        rvTransactions.setAdapter(adapter);

        loadTransactionsForCurrentMonth();

        btnPrevMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            loadTransactionsForCurrentMonth();
        });

        btnNextMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, +1);
            loadTransactionsForCurrentMonth();
        });

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            addTransactionLauncher.launch(intent);
        });

        btnDelete.setOnClickListener(v -> {
            int selectedId = adapter.getSelectedTransactionId();

            if (selectedId != -1) {
                dbHelper.deleteTransaction(selectedId);
                loadTransactionsForCurrentMonth();
                Toast.makeText(MainActivity.this, "삭제 완료", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "삭제할 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        btnCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        //차트 버튼
        btnChart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChartActivity.class);
            startActivity(intent);
        });
    }

    private void calculateAndDisplayCurrentListSummary() {
        int totalIncome = 0;
        int totalExpense = 0;

        for (Transaction t : transactionList) {
            if ("수입".equals(t.type)) {
                totalIncome += t.amount;
            } else if ("지출".equals(t.type)) {
                totalExpense += t.amount;
            }
        }

        // UI 업데이트
        TextView tvIncome = findViewById(R.id.tvIncome);
        TextView tvExpense = findViewById(R.id.tvExpense);
        TextView tvNet = findViewById(R.id.tvNet);

        setColoredText(tvIncome, "수입  ","+" + totalIncome + "원", getResources().getColor(android.R.color.holo_blue_dark));
        setColoredText(tvExpense, "지출  ","-" + totalExpense + "원", getResources().getColor(android.R.color.holo_red_dark));
        setColoredText(tvNet, "합계  ", (totalIncome - totalExpense) + "원", Color.BLACK);
    }
    private void setColoredText(TextView textView, String label, String value, int color) {
        SpannableString spannableString = new SpannableString(label + value);
        spannableString.setSpan(new ForegroundColorSpan(color), label.length(),
                label.length() + value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
    }



    private final ActivityResultLauncher<Intent> addTransactionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadTransactionsForCurrentMonth();
                }
            });

    @SuppressLint("NotifyDataSetChanged")
    private void loadTransactionsForCurrentMonth() {
        transactionList.clear();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        String monthStr = (month < 10 ? "0" + month : String.valueOf(month));

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM transactions WHERE strftime('%Y-%m', date) = ? ORDER BY date";
        Cursor cursor = db.rawQuery(query, new String[]{year + "-" + monthStr});

        tvCurrentMonth.setText(month + "월");

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String type = cursor.getString(1);
            int amount = cursor.getInt(2);
            String category = cursor.getString(3);
            String memo = cursor.getString(4);
            String date = cursor.getString(5);
            transactionList.add(new Transaction(id, type, amount, category, memo, date));
        }
        cursor.close();

        adapter.updateTransactions(transactionList);
        calculateAndDisplayCurrentListSummary();
    }
}
