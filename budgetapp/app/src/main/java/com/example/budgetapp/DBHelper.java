package com.example.budgetapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {

    // 데이터베이스 이름과 버전
    public static final String DB_NAME = "budget.db";
    public static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 테이블 생성
        db.execSQL("CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type TEXT NOT NULL, " +            // '수입' 또는 '지출'
                "amount INTEGER NOT NULL, " +
                "category TEXT, " +
                "memo TEXT, " +
                "date TEXT NOT NULL" +              // 날짜는 YYYY-MM-DD 형식
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 버전이 변경될 경우 테이블 재생성
        db.execSQL("DROP TABLE IF EXISTS transactions");
        onCreate(db);
    }

    public void deleteTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("transactions", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    public Cursor getMonthlyTransactions(String yearMonth) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT type, amount FROM transactions WHERE strftime('%Y-%m', date) = ?", new String[]{yearMonth + "%"});
    }

    public Map<String, Integer> getIncomeExpenseTotalsForMonth(String yearMonth) {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, Integer> totals = new HashMap<>();

        Cursor cursor = db.rawQuery("SELECT type, SUM(amount) AS total FROM transactions WHERE substr(date, 1, 7) = ? GROUP BY type",
                new String[]{yearMonth});

        if (cursor.moveToFirst()) {
            do {
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                int total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
                totals.put(type, total);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return totals;
    }

    public Map<String, String> getMaxTransactionDetailsForMonth(String yearMonth, String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT date, category, amount FROM transactions WHERE type = ? AND strftime('%Y-%m', date) = ? ORDER BY amount DESC LIMIT 1",
                new String[]{type, yearMonth});

        Map<String, String> result = new HashMap<>();
        if (cursor.moveToFirst()) {
            result.put("date", cursor.getString(0));     // YYYY-MM-DD
            result.put("category", cursor.getString(1));
            result.put("amount", cursor.getString(2));
        }
        cursor.close();
        return result;
    }


}