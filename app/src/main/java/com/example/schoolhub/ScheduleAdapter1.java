package com.example.schoolhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScheduleAdapter1 extends RecyclerView.Adapter<ScheduleAdapter1.ScheduleViewHolder> {

    private List<TeacherScheduleActivity.ScheduleItem> scheduleItems;

    public ScheduleAdapter1(List<TeacherScheduleActivity.ScheduleItem> scheduleItems) {
        this.scheduleItems = scheduleItems;
    }


    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View scheduleItemView = inflater.inflate(R.layout.item_schedule1, parent, false);
        return new ScheduleViewHolder(scheduleItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        TeacherScheduleActivity.ScheduleItem item = scheduleItems.get(position);
        
        holder.textViewSubject.setText(item.getSubject());
        holder.textViewClass.setText(item.getClassName());
        holder.textViewTime.setText(item.getStartTime() + " - " + item.getEndTime());
        holder.textViewRoom.setText(item.getRoom());
    }

    @Override
    public int getItemCount() {
        return scheduleItems.size();
    }



    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewSubject;
        public TextView textViewClass;
        public TextView textViewTime;
        public TextView textViewRoom;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSubject = itemView.findViewById(R.id.textViewSubject);
            textViewClass = itemView.findViewById(R.id.textViewClass);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewRoom = itemView.findViewById(R.id.textViewRoom);
        }

    }


}
