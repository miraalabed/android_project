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

public class AssignmentAdapter1 extends RecyclerView.Adapter<AssignmentAdapter1.AssignmentViewHolder> {
    private Context context;
    private List<Assignment> assignmentList;

    public AssignmentAdapter1(Context context, List<Assignment> assignmentList) {
        this.context = context;
        this.assignmentList = assignmentList;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.assignment_item, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);
        holder.tvTitle.setText(assignment.getTitle());
        holder.tvDueDate.setText("Due: " + assignment.getDueDate());

        holder.btnReply.setOnClickListener(v -> {
            Intent intent = new Intent(context, AssignmentDetailsActivity.class);
            intent.putExtra("assignmentId", assignment.getId());
            intent.putExtra("title", assignment.getTitle());
            intent.putExtra("description", assignment.getDescription());
            intent.putExtra("dueDate", assignment.getDueDate());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDueDate;
        Button btnReply;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvAssignmentTitle);
            tvDueDate = itemView.findViewById(R.id.tvAssignmentDueDate);
            btnReply = itemView.findViewById(R.id.btnReply);
        }
    }
}