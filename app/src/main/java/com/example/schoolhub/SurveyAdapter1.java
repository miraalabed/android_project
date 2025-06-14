package com.example.schoolhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class SurveyAdapter1 extends ArrayAdapter<SurveyModel> {
    private Context context;
    private ArrayList<SurveyModel> surveys;

    public SurveyAdapter1(Context context, ArrayList<SurveyModel> surveys) {
        super(context, 0, surveys);
        this.context = context;
        this.surveys = surveys;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SurveyModel survey = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.survey_item, parent, false);
        }

        TextView title = convertView.findViewById(R.id.textViewSurveyName);
        TextView date = convertView.findViewById(R.id.textViewDate);

        title.setText(survey.getTitle());
        date.setText("Created in: " + survey.getCreatedAt());

        return convertView;
    }
}
