package com.example.project;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LogHolder extends RecyclerView.ViewHolder{

    ImageView buttonImage;
    TextView buttonName,date,input;
    public LogHolder(@NonNull View itemView) {
        super(itemView);
        buttonImage = itemView.findViewById(R.id.buttonImage);
        buttonName = itemView.findViewById(R.id.buttonName);
        date = itemView.findViewById(R.id.date);
        input = itemView.findViewById(R.id.input);
    }
}
