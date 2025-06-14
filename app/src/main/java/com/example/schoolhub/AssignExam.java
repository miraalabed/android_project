package com.example.schoolhub;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class AssignExam extends AppCompatActivity {
    Spinner spinnerGrade, spinnerSubject, spinnerExamType;
    EditText editExamDate, editDescription;
    Button btnAssignExam;

    HashMap<String, String> subjectMap = new HashMap<>();

    Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_exam);

        spinnerGrade = findViewById(R.id.spinnerGrade);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        spinnerExamType = findViewById(R.id.spinnerExamType);
        editExamDate = findViewById(R.id.editExamDate);
        editDescription = findViewById(R.id.editDescription);
        btnAssignExam = findViewById(R.id.btnAssignExam);

        ImageView btnHome = findViewById(R.id.home);
        ImageView btnProfile = findViewById(R.id.profile);
        ImageView btnLogout = findViewById(R.id.logout);

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        //btnProfile.setOnClickListener(v -> finish());

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

        List<String> grades = new ArrayList<>();
        for (int i = 1; i <= 12; i++) grades.add("Grade " + i);
        spinnerGrade.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, grades));

        String[] examTypes = {"First Exam", "Midterm Exam", "Second Exam", "Final Exam"};
        spinnerExamType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, examTypes));

        loadSubjects();

        editExamDate.setOnClickListener(v -> {
            Calendar today = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);

                int dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK); // 1 = Sunday, 6 = Friday
                if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SUNDAY) {
                    Toast.makeText(this, "You cannot select Friday or Sunday for exams", Toast.LENGTH_LONG).show();
                    editExamDate.setText("");
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                editExamDate.setText(sdf.format(selectedDate.getTime()));
            }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));

            dialog.getDatePicker().setMinDate(today.getTimeInMillis());
            dialog.show();
        });


        btnAssignExam.setOnClickListener(v -> assignExam());
    }

    private void loadSubjects() {
        new Thread(() -> {
            List<String> subjects = new ArrayList<>();
            try {
                URL url = new URL("http://10.0.2.2/api/getAllSubjects.php");
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                JSONArray array = new JSONArray(sb.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String name = obj.getString("subject_name");
                    String id = obj.getString("subject_id");
                    subjectMap.put(name, id);
                    subjects.add(name);
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AssignExam.this, android.R.layout.simple_spinner_item, subjects);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSubject.setAdapter(adapter);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void assignExam() {
        String grade = spinnerGrade.getSelectedItem().toString().replace("Grade ", "").trim();
        String subjectName = spinnerSubject.getSelectedItem().toString();
        String subjectId = subjectMap.get(subjectName);
        String examType = spinnerExamType.getSelectedItem().toString();
        String examDate = editExamDate.getText().toString();
        String description = editDescription.getText().toString();

        if (examDate.isEmpty()) {
            Toast.makeText(this, "Please select exam date", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog dialog = ProgressDialog.show(this, "Assigning", "Please wait...", true);

        StringRequest request = new StringRequest(Request.Method.POST, "http://10.0.2.2/api/assignexam.php",
                response -> {
                    dialog.dismiss();
                    Toast.makeText(AssignExam.this, response, Toast.LENGTH_LONG).show();
                    finish();
                },
                error -> {
                    dialog.dismiss();
                    Toast.makeText(AssignExam.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("subject_id", subjectId);
                params.put("grade", grade);
                params.put("exam_type", examType);
                params.put("exam_date", examDate);
                params.put("description", description);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
