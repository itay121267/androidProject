package com.example.project;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogHolder> {

    Context context;
    private List<LogEntry> logs;
    public LogAdapter(Context context, List<LogEntry> logs) {
        this.context = context;
        this.logs = logs;
    }


    @NonNull
    @Override
    public LogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LogHolder(LayoutInflater.from(context).inflate(R.layout.log_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LogHolder holder, int position) {
        String bName = logs.get(position).getButtonName();
        holder.buttonName.setText(logs.get(position).getButtonName());
        holder.date.setText(logs.get(position).getFormattedTimestamp());
        holder.input.setText(logs.get(position).getInput());
        int imageResId = holder.itemView.getContext().getResources()
                .getIdentifier(bName, "drawable", holder.itemView.getContext().getPackageName());
        holder.buttonImage.setImageResource(imageResId);

    }

    @Override
    public int getItemCount() {
        return logs.size();
    }
}
