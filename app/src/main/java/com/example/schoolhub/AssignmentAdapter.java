package com.example.schoolhub;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;

public class AssignmentAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<AssignmentModel> assignments;

    public AssignmentAdapter(Context context, ArrayList<AssignmentModel> assignments) {
        this.context = context;
        this.assignments = assignments;
    }

    @Override
    public int getCount() {
        return assignments.size();
    }

    @Override
    public Object getItem(int position) {
        return assignments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return assignments.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(assignments.get(position).getTitle());

        return convertView;
    }
}
