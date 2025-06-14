package com.example.schoolhub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private List<ScheduleItem> scheduleList;

    public ScheduleAdapter(List<ScheduleItem> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public void updateData(List<ScheduleItem> newList) {
        this.scheduleList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textPeriod, textSubject, textTeacher, textLessonTitle;

        public ViewHolder(View view) {
            super(view);
            textPeriod = view.findViewById(R.id.text_period);
            textSubject = view.findViewById(R.id.text_subject);
            textTeacher = view.findViewById(R.id.text_teacher);
            textLessonTitle = view.findViewById(R.id.text_lesson_title);
        }
    }

    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScheduleItem item = scheduleList.get(position);

        holder.textPeriod.setText("Period " + item.getPeriodNumber());
        holder.textSubject.setText("Subject: " + item.getSubjectName());
        holder.textTeacher.setText("Teacher: " + item.getTeacherName());
        holder.textLessonTitle.setText("Time: " + item.getStartTime() + " - " + item.getEndTime());
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }
}