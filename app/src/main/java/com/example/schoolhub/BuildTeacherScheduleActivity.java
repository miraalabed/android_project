package com.example.schoolhub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class BuildTeacherScheduleActivity extends AppCompatActivity {

    Spinner teacherSpinner, subjectSpinner, daySpinner, classGradeSpinner, periodSpinner;
    TextView startTimeInput, endTimeInput;
    Button addPeriodButton;
    HashMap<String, String> teacherMap = new HashMap<>();
    HashMap<String, String> subjectMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_teacher_schedule);
        ImageView btnHome = findViewById(R.id.home);
        ImageView btnProfile = findViewById(R.id.profile);
        ImageView btnLogout = findViewById(R.id.logout);

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this, "Logout success", Toast.LENGTH_LONG).show();

            finish();
        });
        teacherSpinner = findViewById(R.id.teacherSpinner);
        subjectSpinner = findViewById(R.id.subjectSpinner);
        daySpinner = findViewById(R.id.daySpinner);
        classGradeSpinner = findViewById(R.id.classGradeSpinner);
        periodSpinner = findViewById(R.id.periodSpinner);
        startTimeInput = findViewById(R.id.startTimeInput);
        endTimeInput = findViewById(R.id.endTimeInput);
        addPeriodButton = findViewById(R.id.addPeriodButton);

        String[] days = {"Saturday", "Monday", "Tuesday", "Wednesday", "Thursday"};
        daySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, days));

        List<String> grades = new ArrayList<>();
        for (int i = 1; i <= 12; i++) grades.add(String.valueOf(i));
        classGradeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, grades));

        List<String> periods = new ArrayList<>();
        for (int i = 1; i <= 5; i++) periods.add(String.valueOf(i));
        periodSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, periods));

        periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[][] fixedPeriods = {
                        {"08:00", "08:50"},
                        {"09:00", "09:50"},
                        {"10:00", "10:50"},
                        {"11:00", "11:50"},
                        {"12:00", "12:50"}
                };
                if (position >= 0 && position < fixedPeriods.length) {
                    startTimeInput.setText(fixedPeriods[position][0]);
                    endTimeInput.setText(fixedPeriods[position][1]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                startTimeInput.setText("");
                endTimeInput.setText("");
            }
        });

        teacherSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String teacherName = teacherSpinner.getSelectedItem().toString();
                String teacherId = teacherMap.get(teacherName);
                loadSubjects(teacherId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        loadTeachers();

        addPeriodButton.setOnClickListener(v -> {
            String teacherName = teacherSpinner.getSelectedItem().toString();
            String teacherId = teacherMap.get(teacherName);

            String subjectName = subjectSpinner.getSelectedItem().toString();
            String subjectId = subjectMap.get(subjectName);

            String day = daySpinner.getSelectedItem().toString();
            String classGrade = classGradeSpinner.getSelectedItem().toString();
            String periodNumStr = periodSpinner.getSelectedItem().toString();

            String startTime = startTimeInput.getText().toString();
            String endTime = endTimeInput.getText().toString();

            addTeacherSchedule(teacherId, subjectId, classGrade, day, periodNumStr, startTime, endTime);
        });

    }

    private void loadTeachers() {
        String url = "http://10.0.2.2/api/get_users.php?role=Teacher";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    List<String> names = new ArrayList<>();
                    try {
                        org.json.JSONArray array = new org.json.JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String name = obj.getString("name");
                            String id = obj.getString("id");
                            teacherMap.put(name, id);
                            names.add(name);
                        }
                        if (!names.isEmpty()) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(BuildTeacherScheduleActivity.this, android.R.layout.simple_spinner_item, names);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            teacherSpinner.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(BuildTeacherScheduleActivity.this, "Error parsing teacher data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(BuildTeacherScheduleActivity.this, "Error fetching teachers", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }


    private void loadSubjects(String teacherId) {
        String url = "http://10.0.2.2/api/get_subjects.php?teacher_id=" + teacherId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    List<String> names = new ArrayList<>();
                    try {
                        org.json.JSONArray array = new org.json.JSONArray(response);
                        subjectMap.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String name = obj.getString("subject_name");
                            String id = obj.has("subject_id") ? obj.getString("subject_id") : "0";
                            subjectMap.put(name, id);
                            names.add(name);
                        }
                        if (!names.isEmpty()) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(BuildTeacherScheduleActivity.this, android.R.layout.simple_spinner_item, names);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            subjectSpinner.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(BuildTeacherScheduleActivity.this, "Error parsing subject data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(BuildTeacherScheduleActivity.this, "Error fetching subjects", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void addTeacherSchedule(String teacherId, String subjectId, String classGrade, String day, String periodNumber, String startTime, String endTime) {
        String url = "http://10.0.2.2/api/add_teacher_schedule.php";

        ProgressDialog dialog = ProgressDialog.show(this, "Saving", "Please wait...", true);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    dialog.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if (status.equals("success")) {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Invalid response format", Toast.LENGTH_SHORT).show();
                        Log.e("ScheduleError", "Parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("ScheduleError", "Network error: ", error);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("teacher_id", teacherId);
                params.put("subject_id", subjectId);
                params.put("class_grade", classGrade);
                params.put("day_of_week", day);
                params.put("period_number", periodNumber);
                params.put("start_time", startTime);
                params.put("end_time", endTime);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

}
