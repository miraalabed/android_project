package com.example.schoolhub;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class PublishMarksActivity extends AppCompatActivity {

    Spinner spinnerGrade, spinnerStudent, spinnerSubject;
    EditText editExamName, editMark;
    Button btnPublish;

    List<String> studentNames = new ArrayList<>();
    List<Integer> studentIds = new ArrayList<>();

    List<String> subjectNames = new ArrayList<>();
    List<Integer> subjectIds = new ArrayList<>();

    int teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_marks);

        spinnerGrade = findViewById(R.id.spinnerGrade);
        spinnerStudent = findViewById(R.id.spinnerStudent);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        editExamName = findViewById(R.id.editTextExamName);
        editMark = findViewById(R.id.editTextMark);
        btnPublish = findViewById(R.id.buttonPublish);

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        teacherId = prefs.getInt("id", 0);

        List<Integer> grades = new ArrayList<>();
        for (int i = 1; i <= 12; i++) grades.add(i);

        ArrayAdapter<Integer> gradeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grades);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGrade.setAdapter(gradeAdapter);

        spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                int selectedGrade = grades.get(pos);
                loadStudentsByGrade(selectedGrade);
                loadSubjectsByTeacher();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        btnPublish.setOnClickListener(v -> publishMark());
    }

    private void loadStudentsByGrade(int grade) {
        studentNames.clear();
        studentIds.clear();

        String url = "http://10.0.2.2/api/get_students_by_grade.php?class_grade=" + grade;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            studentNames.add(obj.getString("full_name"));
                            studentIds.add(obj.getInt("student_id"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, studentNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerStudent.setAdapter(adapter);

                    } catch (Exception e) {
                        Log.e("StudentParseError", "Error parsing student data: ", e);
                        Toast.makeText(this, "Error parsing student data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("StudentLoadError", "Volley error while loading students: " + error.toString());
                    Toast.makeText(this, "Failed to load students", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void loadSubjectsByTeacher() {
        subjectNames.clear();
        subjectIds.clear();

        String url = "http://10.0.2.2/api/get_subjects.php?teacher_id=" + teacherId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            subjectNames.add(obj.getString("subject_name"));
                            subjectIds.add(obj.getInt("subject_id"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerSubject.setAdapter(adapter);

                    } catch (Exception e) {
                        Log.e("SubjectParseError", "Error parsing subject data: ", e);
                        Toast.makeText(this, "Error parsing subject data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("SubjectLoadError", "Volley error while loading subjects: " + error.toString());
                    Toast.makeText(this, "Failed to load subjects", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void publishMark() {
        String examName = editExamName.getText().toString().trim();
        String mark = editMark.getText().toString().trim();

        if (examName.isEmpty() || mark.isEmpty() ||
                spinnerStudent.getSelectedItemPosition() == -1 ||
                spinnerSubject.getSelectedItemPosition() == -1) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int studentId = studentIds.get(spinnerStudent.getSelectedItemPosition());
        int subjectId = subjectIds.get(spinnerSubject.getSelectedItemPosition());

        String url = "http://10.0.2.2/api/publish_marks.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        Log.d("PublishResponse", "Server returned: " + response);
                        JSONObject obj = new JSONObject(response);
                        Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("PublishParseError", "Error parsing publish response: ", e);
                        Toast.makeText(this, "Failed to publish", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("PublishNetworkError", "Volley error: " + error.toString());
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("teacher_id", String.valueOf(teacherId));
                map.put("student_id", String.valueOf(studentId));
                map.put("subject_id", String.valueOf(subjectId));
                map.put("exam_name", examName);
                map.put("mark", mark);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
