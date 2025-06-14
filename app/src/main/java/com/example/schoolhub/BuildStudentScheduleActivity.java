package com.example.schoolhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class BuildStudentScheduleActivity extends AppCompatActivity {
    private static final String TAG = "BuildStudentSchedule";
    private Button buttonSave;
    private LinearLayout scheduleContainer;
    private Spinner spinnerGrade;
    private ArrayList<String> subjects = new ArrayList<>();
    private HashMap<String, Integer> subjectMap = new HashMap<>();
    private HashMap<String, Integer> subjectToTeacherMap = new HashMap<>();
    private ArrayAdapter<String> subjectAdapter;
    private ArrayList<Spinner> subjectSpinners = new ArrayList<>();
    private RequestQueue requestQueue;

    private final String[] gradeNames = {"Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5",
            "Grade 6", "Grade 7", "Grade 8", "Grade 9", "Grade 10",
            "Grade 11", "Grade 12"};
    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Saturday"};
    private final String[] periods = {"8:00 - 8:50", "9:00 - 9:50", "10:00 - 10:50",
            "11:00 - 11:50", "12:00 - 12:50"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_student_schedule);
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
        initializeViews();
        setupAdapters();
        setupRequestQueue();
        setupButtonListeners();
        fetchSubjectsWithTeachers();
    }

    private void initializeViews() {
        spinnerGrade = findViewById(R.id.spinnerGrade);
        scheduleContainer = findViewById(R.id.scheduleContainer);
        buttonSave = findViewById(R.id.btnSave);
    }

    private void setupAdapters() {
        subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, gradeNames);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGrade.setAdapter(gradeAdapter);
    }

    private void setupRequestQueue() {
        requestQueue = Volley.newRequestQueue(this);
    }

    private void setupButtonListeners() {
        buttonSave.setOnClickListener(v -> saveScheduleToDatabase());
    }

    private void fetchSubjectsWithTeachers() {
        String url = "http://10.0.2.2/api/getAllSubjects.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        subjects.clear();
                        subjectMap.clear();
                        subjectToTeacherMap.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject subject = response.getJSONObject(i);
                            String name = subject.getString("subject_name");
                            int subjectId = subject.getInt("subject_id");

                            int teacherId = 0;
                            if (!subject.isNull("teacher_id")) {
                                teacherId = subject.getInt("teacher_id");
                            }

                            subjects.add(name);
                            subjectMap.put(name, subjectId);
                            subjectToTeacherMap.put(name, teacherId);
                        }
                        subjectAdapter.notifyDataSetChanged();
                        buildScheduleTable();
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        Toast.makeText(this, "Error parsing subjects data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error: " + error.toString());
                    Toast.makeText(this, "Error fetching subjects: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

    private void buildScheduleTable() {
        scheduleContainer.removeAllViews();
        subjectSpinners.clear();

        for (String day : days) {
            TextView dayTitle = new TextView(this);
            dayTitle.setText(day);
            dayTitle.setTextSize(18f);
            dayTitle.setPadding(0, 16, 0, 8);
            scheduleContainer.addView(dayTitle);

            for (String period : periods) {
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(0, 8, 0, 8);

                TextView time = new TextView(this);
                time.setText(period);
                time.setWidth(dpToPx(120));
                time.setTextSize(14f);

                Spinner subjectSpinner = new Spinner(this);
                subjectSpinner.setAdapter(subjectAdapter);
                subjectSpinner.setMinimumWidth(dpToPx(200));

                row.addView(time);
                row.addView(subjectSpinner);
                scheduleContainer.addView(row);
                subjectSpinners.add(subjectSpinner);
            }
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void saveScheduleToDatabase() {
        int classGrade = spinnerGrade.getSelectedItemPosition() + 1;
        JSONArray scheduleArray = new JSONArray();

        try {
            int spinnerIndex = 0;
            for (String day : days) {
                for (int p = 0; p < periods.length; p++) {
                    if (spinnerIndex >= subjectSpinners.size()) {
                        Log.e(TAG, "Spinner index out of bounds: " + spinnerIndex);
                        continue;
                    }

                    Spinner spinner = subjectSpinners.get(spinnerIndex++);
                    if (spinner.getSelectedItem() == null) {
                        Log.e(TAG, "No subject selected at index " + spinnerIndex);
                        continue;
                    }

                    String subjectName = spinner.getSelectedItem().toString();
                    if (!subjectMap.containsKey(subjectName)) {
                        Log.e(TAG, "Invalid subject name: " + subjectName);
                        continue;
                    }

                    int subjectId = subjectMap.get(subjectName);
                    Integer teacherId = subjectToTeacherMap.get(subjectName);
                    if (teacherId == null) {
                        Log.e(TAG, "Missing teacher for subject: " + subjectName);
                        continue;
                    }

                    String[] timeParts = periods[p].split("-");
                    if (timeParts.length != 2) {
                        Log.e(TAG, "Invalid time format: " + periods[p]);
                        continue;
                    }

                    JSONObject scheduleItem = new JSONObject();
                    scheduleItem.put("class_grade", classGrade);
                    scheduleItem.put("day", day);
                    scheduleItem.put("period_number", p + 1);
                    scheduleItem.put("subject_id", subjectId);
                    scheduleItem.put("teacher_id", teacherId);
                    scheduleItem.put("start_time", timeParts[0].trim() + ":00");
                    scheduleItem.put("end_time", timeParts[1].trim() + ":00");

                    scheduleArray.put(scheduleItem);
                    Log.d(TAG, "Added item: " + scheduleItem.toString());
                }
            }

            JSONObject requestBody = new JSONObject();
            requestBody.put("schedule", scheduleArray);
            sendScheduleToServer(requestBody);

        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON schedule: " + e.getMessage());
            Toast.makeText(this, "Error building schedule JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendScheduleToServer(JSONObject requestBody) {
        String url = "http://10.0.2.2/api/add_student_schedule.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        Log.d(TAG, "Server response: " + response.toString());
                        String status = response.getString("status");
                        String message = response.getString("message");

                        if ("success".equals(status)) {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Response parsing error: " + e.getMessage());
                        Toast.makeText(this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Schedule save error: " + error.toString());
                    if (error.networkResponse != null) {
                        try {
                            String errorResponse = new String(error.networkResponse.data, "UTF-8");
                            Log.e(TAG, "Server error response body: " + errorResponse);
                            Toast.makeText(this, "Server error: " + errorResponse, Toast.LENGTH_LONG).show();
                        } catch (UnsupportedEncodingException e) {
                            Log.e(TAG, "Unsupported encoding: " + e.getMessage());
                        }
                    } else {
                        Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_LONG).show();
                    }
                });

        requestQueue.add(request);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}
