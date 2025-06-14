package com.example.schoolhub;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;

public class StudentAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<StudentModel> students;

    public StudentAdapter(Context context, ArrayList<StudentModel> students) {
        this.context = context;
        this.students = students;
    }

    @Override
    public int getCount() {
        return students.size();
    }

    @Override
    public Object getItem(int position) {
        return students.get(position);
    }

    @Override
    public long getItemId(int position) {
        return students.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(students.get(position).getName());

        return convertView;
    }
}
