package com.example.schoolhub;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class SendAssignmentActivity extends AppCompatActivity {

    private EditText editTextAssignmentTitle, editTextAssignmentDescription;
    private TextView textViewSelectedDueDate;
    private Button buttonSelectDueDate, buttonAttachFile, buttonSend;
    private Spinner spinnerGrade, spinnerTargetSubject;

    private Calendar selectedDueDateCalendar;
    private List<String> subjectNames = new ArrayList<>();
    private List<Integer> subjectIds = new ArrayList<>();

    private int teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_assignment);

        editTextAssignmentTitle = findViewById(R.id.editTextAssignmentTitle);
        editTextAssignmentDescription = findViewById(R.id.editTextAssignmentDescription);
        textViewSelectedDueDate = findViewById(R.id.textViewSelectedDueDate);
        buttonSelectDueDate = findViewById(R.id.buttonSelectDueDate);
        buttonAttachFile = findViewById(R.id.buttonAttachFile);
        buttonSend = findViewById(R.id.buttonSend);
        spinnerGrade = findViewById(R.id.spinnerGrade);
        spinnerTargetSubject = findViewById(R.id.spinnerTargetSubject);

        selectedDueDateCalendar = Calendar.getInstance();

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        teacherId = prefs.getInt("id", -1);

        List<Integer> grades = new ArrayList<>();
        for (int i = 1; i <= 12; i++) grades.add(i);
        ArrayAdapter<Integer> gradeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grades);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGrade.setAdapter(gradeAdapter);

        loadSubjectsByTeacher();

        buttonSelectDueDate.setOnClickListener(v -> showDatePickerDialog());

        buttonAttachFile.setOnClickListener(v -> {
            Toast.makeText(this, "File attachment coming soon", Toast.LENGTH_SHORT).show();
        });

        buttonSend.setOnClickListener(v -> sendAssignment());

        findViewById(R.id.home).setOnClickListener(v -> {
            startActivity(new Intent(this, TeacherDashboardActivity.class));
            finish();
        });

        findViewById(R.id.profile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            Toast.makeText(this, "Profile feature coming soon", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.logout).setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
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
                        spinnerTargetSubject.setAdapter(adapter);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing subject data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Failed to load subjects", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void showDatePickerDialog() {
        new DatePickerDialog(this, (view, year, month, day) -> {
            selectedDueDateCalendar.set(Calendar.YEAR, year);
            selectedDueDateCalendar.set(Calendar.MONTH, month);
            selectedDueDateCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateDueDateLabel();
        }, selectedDueDateCalendar.get(Calendar.YEAR),
                selectedDueDateCalendar.get(Calendar.MONTH),
                selectedDueDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDueDateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        textViewSelectedDueDate.setText(sdf.format(selectedDueDateCalendar.getTime()));
        textViewSelectedDueDate.setTextColor(getResources().getColor(android.R.color.black));
    }

    private void sendAssignment() {
        String title = editTextAssignmentTitle.getText().toString().trim();
        String description = editTextAssignmentDescription.getText().toString().trim();
        String dueDate = textViewSelectedDueDate.getText().toString();

        if (title.isEmpty() || description.isEmpty() || dueDate.equals("Not Set")) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerGrade.getSelectedItemPosition() == -1 || spinnerTargetSubject.getSelectedItemPosition() == -1) {
            Toast.makeText(this, "Please select grade and subject", Toast.LENGTH_SHORT).show();
            return;
        }

        int grade = (int) spinnerGrade.getSelectedItem();
        int subjectId = subjectIds.get(spinnerTargetSubject.getSelectedItemPosition());

        String url = "http://10.0.2.2/api/send_assignment.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        editTextAssignmentTitle.setText("");
                        editTextAssignmentDescription.setText("");
                        textViewSelectedDueDate.setText("Not Set");
                        spinnerGrade.setSelection(0);
                        spinnerTargetSubject.setSelection(0);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("teacher_id", String.valueOf(teacherId));
                map.put("subject_id", String.valueOf(subjectId));
                map.put("class_grade", String.valueOf(grade));
                map.put("title", title);
                map.put("description", description);
                map.put("due_date", dueDate);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
