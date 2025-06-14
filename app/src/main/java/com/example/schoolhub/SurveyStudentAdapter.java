package com.example.schoolhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class SurveyStudentAdapter extends ArrayAdapter<SurveyStudent> {
    private final Context context;
    private final List<SurveyStudent> students;

    public SurveyStudentAdapter(Context context, List<SurveyStudent> students) {
        super(context, 0, students);
        this.context = context;
        this.students = students;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.list_student_response, parent, false);

        TextView nameView = convertView.findViewById(R.id.textViewStudentName);
        TextView dateView = convertView.findViewById(R.id.textViewSubmittedDate);

        SurveyStudent student = students.get(position);
        nameView.setText(student.getName());
        dateView.setText("Submitted on: " + student.getDate());

        return convertView;
    }
}

