package com.example.schoolhub;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TeacherAssignmentAdapter extends RecyclerView.Adapter<TeacherAssignmentAdapter.ViewHolder> {

    private Context context;
    private List<Assignment> assignmentList;

    public TeacherAssignmentAdapter(Context context, List<Assignment> assignmentList) {
        this.context = context;
        this.assignmentList = assignmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.teacher_assignment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);
        holder.title.setText(assignment.getTitle());
        holder.dueDate.setText("Due: " + assignment.getDueDate());

        holder.viewRepliesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, AssignmentRepliesActivity.class);
            intent.putExtra("assignmentId", assignment.getId());
            intent.putExtra("title", assignment.getTitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, dueDate;
        Button viewRepliesBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvAssignmentTitle);
            dueDate = itemView.findViewById(R.id.tvAssignmentDueDate);
            viewRepliesBtn = itemView.findViewById(R.id.btnViewReplies);
        }
    }
}
