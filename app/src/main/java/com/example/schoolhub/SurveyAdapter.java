package com.example.schoolhub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.SurveyViewHolder> {

    private Context context;
    private List<Survey> surveyList;
    private int studentId;

    public SurveyAdapter(Context context, List<Survey> surveyList, int studentId) {
        this.context = context;
        this.surveyList = surveyList;
        this.studentId = studentId;
    }

    @NonNull
    @Override
    public SurveyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_survey, parent, false);
        return new SurveyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyViewHolder holder, int position) {
        Survey survey = surveyList.get(position);

        holder.textTitle.setText(survey.getTitle());
        holder.textStatus.setText("Status: " + survey.getStatus());
        holder.textDate.setText("Date: " + survey.getDate());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SurveyDetailsActivity.class);
            intent.putExtra("survey_id", survey.getId());
            intent.putExtra("title", survey.getTitle());
            intent.putExtra("student_id", studentId);

            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return surveyList.size();
    }

    static class SurveyViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textStatus, textDate;

        public SurveyViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title);
            textStatus = itemView.findViewById(R.id.text_status);
            textDate = itemView.findViewById(R.id.text_date);
        }
    }
}
