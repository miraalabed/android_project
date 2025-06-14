package com.example.schoolhub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PreparationAdapter extends RecyclerView.Adapter<PreparationAdapter.PlanViewHolder> {

    List<PreparationPlan> list;

    public PreparationAdapter(List<PreparationPlan> list) {
        this.list = list;
    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preparation, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder, int position) {
        PreparationPlan p = list.get(position);
        holder.subject.setText(p.getSubject());
        holder.title.setText(p.getTitle());
        holder.content.setText(p.getContent());
        holder.date.setText(p.getDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView subject, title, content, date;

        public PlanViewHolder(View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.txt_subject);
            title = itemView.findViewById(R.id.txt_title);
            content = itemView.findViewById(R.id.txt_content);
            date = itemView.findViewById(R.id.txt_date);
        }
    }
}
