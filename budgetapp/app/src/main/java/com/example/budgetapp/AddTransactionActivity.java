package com.example.budgetapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

public class AddTransactionActivity extends AppCompatActivity {

    EditText etAmount, etCategory, etMemo, etDate;
    RadioButton rbIncome, rbExpense;
    Button btnSave;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        etAmount = findViewById(R.id.etAmount);
        etCategory = findViewById(R.id.etCategory);
        etMemo = findViewById(R.id.etMemo);
        etDate = findViewById(R.id.etDate);
        rbIncome = findViewById(R.id.rbIncome);
        rbExpense = findViewById(R.id.rbExpense);
        btnSave = findViewById(R.id.btnSave);

        dbHelper = new DBHelper(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTransaction();
                setResult(RESULT_OK);
                finish();
            }
        });

        // 이전 버튼 추가 및 클릭 이벤트 처리
        findViewById(R.id.btnPrevActivity).setOnClickListener(v -> {
            finish(); // 현재 액티비티를 종료하고 이전 액티비티로 돌아감
        });
    }

    private void saveTransaction() {
        String type = rbIncome.isChecked() ? "수입" : "지출";
        int amount = Integer.parseInt(etAmount.getText().toString());
        String category = etCategory.getText().toString();
        String memo = etMemo.getText().toString();
        String date = etDate.getText().toString();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("type", type);
        values.put("amount", amount);
        values.put("category", category);
        values.put("memo", memo);
        values.put("date", date);
        db.insert("transactions", null, values);
    }
}