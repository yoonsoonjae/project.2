package com.example.budgetapp;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ChartActivity extends AppCompatActivity {

    private Calendar calendar;
    private TextView tvCurrentMonth, tvIncome, tvExpense, tvNet, tvMaxIncome, tvMaxExpense;
    private PieChart pieChart;
    private DBHelper dbHelper;
    private ImageButton btnPrevMonth, btnNextMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        calendar = Calendar.getInstance();
        dbHelper = new DBHelper(this);

        pieChart = findViewById(R.id.pieChart);
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        tvIncome = findViewById(R.id.tvIncome);
        tvExpense = findViewById(R.id.tvExpense);
        tvNet = findViewById(R.id.tvNet);
        tvMaxIncome = findViewById(R.id.tvMaxIncome);
        tvMaxExpense = findViewById(R.id.tvMaxExpense);

        btnPrevMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateChart();
        });

        btnNextMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateChart();
        });

        findViewById(R.id.btnPrevActivity).setOnClickListener(v -> finish());

        updateChart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateChart();
    }

    private void updateChart() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        String yearMonth = String.format("%d-%02d", year, month);

        tvCurrentMonth.setText(year + "년 " + month + "월");

        Map<String, Integer> totals = dbHelper.getIncomeExpenseTotalsForMonth(yearMonth);

        int income = totals.getOrDefault("수입", 0);
        int expense = totals.getOrDefault("지출", 0);
        int net = income - expense;

        setColoredText(tvIncome, "수입  ", "+" + income + "원", getResources().getColor(android.R.color.holo_blue_dark));
        setColoredText(tvExpense, "지출  ", "-" + expense + "원", getResources().getColor(android.R.color.holo_red_dark));
        setColoredText(tvNet, "합계  ", net + "원", Color.BLACK);

        updateMaxTransaction(yearMonth);

        updatePieChart(income, expense);
    }

    private void updateMaxTransaction(String yearMonth) {
        updateMaxDetail(tvMaxIncome, dbHelper.getMaxTransactionDetailsForMonth(yearMonth, "수입"), "최대 수입", "💰", getResources().getColor(android.R.color.holo_blue_dark));
        updateMaxDetail(tvMaxExpense, dbHelper.getMaxTransactionDetailsForMonth(yearMonth, "지출"), "최대 지출", "💸", getResources().getColor(android.R.color.holo_red_dark));
    }

    private void updateMaxDetail(TextView tv, Map<String, String> data, String title, String icon, int color) {
        if (!data.isEmpty()) {
            String[] dateParts = data.get("date").split("-");
            String details = String.format("  %s  %d월 %d일  %s  ", icon,
                    Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[2]), data.get("category"));
            String value = data.get("amount") + "원";

            setColoredText(tv, title + "\n", details, value, color);
        } else {
            setColoredText(tv, title + "\n", "  " + icon + "  없음", "", color);
        }
    }

    private void updatePieChart(int income, int expense) {
        pieChart.clear();
        pieChart.setUsePercentValues(true);
        pieChart.setCenterText("수입/지출");
        pieChart.getDescription().setEnabled(false);

        List<PieEntry> entries = new ArrayList<>();
        PieDataSet dataSet;

        if (income == 0 && expense == 0) {
            entries.add(new PieEntry(1f, "데이터 없음"));
            dataSet = new PieDataSet(entries, "");
            dataSet.setColor(Color.rgb(102, 204, 0));
            setupLegend(Collections.singletonList(new LegendEntry("데이터 없음", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.rgb(102, 204, 0))));
        } else {
            entries.add(new PieEntry(income, "수입"));
            entries.add(new PieEntry(expense, "지출"));
            dataSet = new PieDataSet(entries, "");
            dataSet.setColors(Color.rgb(102, 204, 255), Color.rgb(255, 102, 102));
            setupLegend(Arrays.asList(
                    new LegendEntry("수입", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.rgb(102, 204, 255)),
                    new LegendEntry("지출", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.rgb(255, 102, 102))
            ));
        }

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(16f);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void setColoredText(TextView tv, String label, String details, String value, int color) {
        SpannableString spannable = new SpannableString(label + details + value);
        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, label.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (!value.isEmpty())
            spannable.setSpan(new ForegroundColorSpan(color), (label + details).length(), spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spannable);
    }
    private void setColoredText(TextView tv, String label, String value, int color) {
        String fullText = label + value;
        SpannableString spannable = new SpannableString(fullText);
        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, label.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(color), label.length(), fullText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spannable);
    }


    private void setupLegend(List<LegendEntry> legendEntries) {
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setTextSize(14f);
        legend.setTextColor(Color.DKGRAY);
        legend.setCustom(legendEntries);
    }
}

