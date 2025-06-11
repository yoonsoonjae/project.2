package com.example.budgetapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<TransactionItem> items = new ArrayList<>();
    private int selectedPosition = RecyclerView.NO_POSITION;

    public TransactionAdapter(List<Transaction> transactions) {
        updateTransactions(transactions);
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getItemType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TransactionItem.TYPE_DATE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_header, parent, false);
            return new DateHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
            return new TransactionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DateHeaderViewHolder) {
            ((DateHeaderViewHolder) holder).bind((DateHeader) items.get(position));
        } else {
            TransactionViewHolder tvh = (TransactionViewHolder) holder;
            Transaction t = (Transaction) items.get(position);
            tvh.bind(t);

            tvh.itemView.setOnClickListener(v -> setSelectedPosition(holder.getAdapterPosition()));
            tvh.itemView.setBackgroundColor(position == selectedPosition ? 0xFFDDDDDD : 0xFFFFFFFF);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectedTransactionId() {
        if (selectedPosition != RecyclerView.NO_POSITION && items.get(selectedPosition) instanceof Transaction) {
            return ((Transaction) items.get(selectedPosition)).id;
        }
        return -1;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateTransactions(List<Transaction> transactions) {
        items.clear();
        String lastDate = "";
        for (Transaction t : transactions) {
            if (!t.date.equals(lastDate)) {
                items.add(new DateHeader(t.date));
                lastDate = t.date;
            }
            items.add(t);
        }
        notifyDataSetChanged();
    }

    public static class DateHeader implements TransactionItem {
        String date;

        DateHeader(String date) {
            this.date = date;
        }

        @Override
        public int getItemType() {
            return TYPE_DATE_HEADER;
        }
    }

    public static class DateHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;

        DateHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        void bind(DateHeader header) {
            tvDate.setText(header.date + " [" + getDayOfWeek(header.date) + "]");
        }

        public static String getDayOfWeek(String date) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                Date d = sdf.parse(date);
                sdf.applyPattern("E");
                return sdf.format(d);
            } catch (Exception e) {
                return "";
            }
        }
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvCategory, tvAmount;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tvIcon);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }

        void bind(Transaction t) {
            tvIcon.setText(t.type.equals("수입") ? "💰" : "💸");
            tvCategory.setText(t.category);

            String amountText = String.format("%,d원", t.amount);
            tvAmount.setText(t.type.equals("수입") ? "+" + amountText : "-" + amountText);

            int color = t.type.equals("수입")
                    ? ContextCompat.getColor(itemView.getContext(), android.R.color.holo_blue_dark)
                    : ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark);

            tvAmount.setTextColor(color);
        }
    }
}
