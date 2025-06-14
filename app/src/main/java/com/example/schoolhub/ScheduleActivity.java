package com.example.schoolhub;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ScheduleAdapter adapter;
    List<ScheduleItem> fullScheduleList = new ArrayList<>();
    List<ScheduleItem> filteredList = new ArrayList<>();
    TextView selectedDayView = null;

    int classGrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        recyclerView = findViewById(R.id.recycler_schedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScheduleAdapter(filteredList);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        classGrade = prefs.getInt("class_grade", 0);

        setupDayButtons();
        loadScheduleForClass(classGrade);
    }

    private void loadScheduleForClass(int classGrade) {
        String url = "http://10.0.2.2/api/get_schedule.php?class_grade=" + classGrade;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("success")) {
                            JSONArray array = json.getJSONArray("data");
                            fullScheduleList.clear();
                            filteredList.clear();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                ScheduleItem item = new ScheduleItem(
                                        obj.getString("day"),
                                        obj.getInt("period_number"),
                                        obj.getString("subject_name"),
                                        obj.getString("teacher_name"),
                                        obj.getString("start_time"),
                                        obj.getString("end_time")
                                );
                                fullScheduleList.add(item);
                            }

                            Collections.sort(fullScheduleList, Comparator.comparingInt(ScheduleItem::getPeriodNumber));

                            TextView mondayView = findViewById(R.id.day_mon);
                            mondayView.performClick();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void filterByDay(String selectedDay) {
        filteredList.clear();
        for (ScheduleItem item : fullScheduleList) {
            if (item.getDay().trim().equalsIgnoreCase(selectedDay.trim())) {
                filteredList.add(item);
            }
        }

        Collections.sort(filteredList, Comparator.comparingInt(ScheduleItem::getPeriodNumber));
        adapter.updateData(filteredList);
    }

    private void setupDayButtons() {
        setupDayTextView(findViewById(R.id.day_sat), "Saturday");
        setupDayTextView(findViewById(R.id.day_sun), "Sunday");
        setupDayTextView(findViewById(R.id.day_mon), "Monday");
        setupDayTextView(findViewById(R.id.day_tue), "Tuesday");
        setupDayTextView(findViewById(R.id.day_wed), "Wednesday");
        setupDayTextView(findViewById(R.id.day_thu), "Thursday");
        setupDayTextView(findViewById(R.id.day_fri), "Friday");
    }

    private void setupDayTextView(TextView dayView, String dayName) {
        dayView.setOnClickListener(v -> {
            filterByDay(dayName);

            if (selectedDayView != null) {
                selectedDayView.setBackgroundColor(Color.parseColor("#E5E7EB"));
                selectedDayView.setTextColor(Color.parseColor("#374151"));
            }

            dayView.setBackgroundColor(Color.parseColor("#008080"));
            dayView.setTextColor(Color.WHITE);

            selectedDayView = dayView;
        });
    }
}