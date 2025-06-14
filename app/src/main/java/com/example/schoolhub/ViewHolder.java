package com.example.schoolhub;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView text1, text2;

    public ViewHolder(View itemView) {
        super(itemView);
        text1 = itemView.findViewById(android.R.id.text1);
        text2 = itemView.findViewById(android.R.id.text2);
    }
}