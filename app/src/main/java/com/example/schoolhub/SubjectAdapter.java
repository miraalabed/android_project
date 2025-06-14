package com.example.schoolhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class SubjectAdapter extends ArrayAdapter<Subject> {
    Context context;
    List<Subject> subjects;

    public SubjectAdapter(Context context, List<Subject> subjects) {
        super(context, 0, subjects);
        this.context = context;
        this.subjects = subjects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.subject_item, parent, false);
        }

        Subject subject = subjects.get(position);

        TextView nameView = convertView.findViewById(R.id.textViewSubjectName);
        TextView descView = convertView.findViewById(R.id.textViewSubjectDescription);

        nameView.setText(subject.name + " - " + subject.teacher);
        descView.setText(subject.description);

        return convertView;
    }
}
