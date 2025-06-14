package com.example.schoolhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ExamsAdapter extends RecyclerView.Adapter<ExamsAdapter.ExamViewHolder> {

    Context context;
    ArrayList<Exam> examList;

    public ExamsAdapter(Context context, ArrayList<Exam> examList) {
        this.context = context;
        this.examList = examList;
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exam_item, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        Exam exam = examList.get(position);

        holder.subjectName.setText(exam.getSubjectName());
        holder.examType.setText(exam.getExamType());
        holder.examDate.setText("" + exam.getExamDate());
        holder.description.setText(exam.getDescription().isEmpty() ? "No description" : exam.getDescription());
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    static class ExamViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName, examType, examDate, description;

        public ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.exam_subject);
            examType = itemView.findViewById(R.id.exam_type);
            examDate = itemView.findViewById(R.id.exam_date);
            description = itemView.findViewById(R.id.exam_description);
        }
    }
}
