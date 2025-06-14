package com.example.schoolhub;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<TaskItem> taskList;
    private Context context;

    public TaskAdapter(Context context, List<TaskItem> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskItem item = taskList.get(position);
        holder.taskTitle.setText(item.getTitle());
        holder.taskIcon.setImageResource(item.getIconRes());

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = null;

            switch (item.getTitle()) {
                case "Add New Student":
                    intent = new Intent(context, AddStudentActivity.class);
                    break;
                case "Add New Teacher":
                    intent = new Intent(context, AddTeacherActivity.class);
                    break;
                case "Add New Subject":
                    intent = new Intent(context, AddSubjectActivity.class);
                    break;
                case "Build Class Schedule":
                    intent = new Intent(context, BuildStudentScheduleActivity.class);
                    break;
                case "Build Teacher Schedule":
                    intent = new Intent(context, BuildTeacherScheduleActivity.class);
                    break;
                case "View Users":
                    intent = new Intent(context, ViewUsersActivity.class);
                    break;
                case "View Subjects":
                    intent = new Intent(context, ViewSubjectsActivity.class);
                    break;
                case "Assign Exam":
                    intent = new Intent(context, AssignExam.class);
                    break;
                case "View Survey Results":
                    intent = new Intent(context, SurveyListActivity.class);
                    break;
                case "Send Survey to Student":
                    intent = new Intent(context, SendSurveyActivity.class);
                    break;
            }

            if (intent != null) {
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        ImageView taskIcon;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskIcon = itemView.findViewById(R.id.taskIcon);
        }
    }
}
