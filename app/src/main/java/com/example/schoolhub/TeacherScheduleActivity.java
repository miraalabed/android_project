package com.example.schoolhub;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

public class TeacherScheduleActivity extends AppCompatActivity {
    private Spinner spinnerDays;
    private RecyclerView recyclerViewSchedule;
    private TextView textViewNoSchedule;
    private Button buttonBack;

    private String URL_TEACHER_SCHEDULE = "http://10.0.2.2/api/teacher_schedule.php";
    private String teacherEmail;
    private JSONObject scheduleData;
    private List<String> daysList = new ArrayList<>();
    private ScheduleAdapter1 scheduleAdapter;
    private List<ScheduleItem> scheduleItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_schedule);

        spinnerDays = findViewById(R.id.spinnerDays);
        recyclerViewSchedule = findViewById(R.id.recyclerViewSchedule);
        textViewNoSchedule = findViewById(R.id.textViewNoSchedule);
        buttonBack = findViewById(R.id.buttonBack);

        recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(this));
        scheduleAdapter = new ScheduleAdapter1(scheduleItems);
        recyclerViewSchedule.setAdapter(scheduleAdapter);

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        teacherEmail = prefs.getString("email", "");

        buttonBack.setOnClickListener(v -> finish());

        spinnerDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < daysList.size()) {
                    loadScheduleForDay(daysList.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        loadTeacherSchedule();
    }

    private void loadTeacherSchedule() {
        if (teacherEmail == null || teacherEmail.isEmpty()) {
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = URL_TEACHER_SCHEDULE + "?email=" + teacherEmail;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Log.d("TeacherSchedule", "Response: " + response);

                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            scheduleData = jsonResponse.getJSONObject("schedule");
                            daysList.clear();
                            Iterator<String> keys = scheduleData.keys();
                            while (keys.hasNext()) {
                                String day = keys.next();
                                if (!day.equalsIgnoreCase("Friday") && !day.equalsIgnoreCase("Sunday")) {
                                    daysList.add(day);
                                }
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerDays.setAdapter(adapter);
                            if (!daysList.isEmpty()) {
                                spinnerDays.setSelection(0);
                                loadScheduleForDay(daysList.get(0));
                            }
                        } else {
                            textViewNoSchedule.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        textViewNoSchedule.setVisibility(View.VISIBLE);
                    }
                },
                error -> textViewNoSchedule.setVisibility(View.VISIBLE)
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void loadScheduleForDay(String day) {
        try {
            JSONArray array = scheduleData.getJSONArray(day);
            scheduleItems.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                scheduleItems.add(new ScheduleItem(
                        item.getString("subject_name"),
                        item.getString("class_name"),
                        item.getString("start_time"),
                        item.getString("end_time"),
                        item.getString("room_name")
                ));
            }
            scheduleAdapter.notifyDataSetChanged();
            recyclerViewSchedule.setVisibility(View.VISIBLE);
            textViewNoSchedule.setVisibility(View.GONE);
        } catch (JSONException e) {
            recyclerViewSchedule.setVisibility(View.GONE);
            textViewNoSchedule.setVisibility(View.VISIBLE);
        }
    }

    public static class ScheduleItem {
        private final String subject, className, startTime, endTime, room;

        public ScheduleItem(String subject, String className, String startTime, String endTime, String room) {
            this.subject = subject;
            this.className = className;
            this.startTime = startTime;
            this.endTime = endTime;
            this.room = room;
        }

        public String getSubject() { return subject; }
        public String getClassName() { return className; }
        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
        public String getRoom() { return room; }
    }
}
