package com.example.schoolhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;

import java.util.HashMap;
import java.util.Map;

public class AddPreparationActivity extends AppCompatActivity {

    EditText editClass, editSubject, editTitle, editContent;
    Button btnSubmit;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_preparation);

        editClass = findViewById(R.id.edit_class);
        editSubject = findViewById(R.id.edit_subject);
        editTitle = findViewById(R.id.edit_title);
        editContent = findViewById(R.id.edit_content);
        btnSubmit = findViewById(R.id.btn_submit);
        progressBar = findViewById(R.id.progress_bar);

        findViewById(R.id.home).setOnClickListener(v -> {
            Intent intent = new Intent(AddPreparationActivity.this, TeacherDashboardActivity.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.profile).setOnClickListener(v -> {
            // Navigate to Profile Activity (Student 2's module)
            Intent profileIntent = new Intent(AddPreparationActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
            Toast.makeText(this, "Profile feature coming soon", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.logout).setOnClickListener(v -> {
            // Handle Logout
            SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        btnSubmit.setOnClickListener(v -> {
            String classGrade = editClass.getText().toString().trim();
            String subject = editSubject.getText().toString().trim();
            String title = editTitle.getText().toString().trim();
            String content = editContent.getText().toString().trim();

            if (classGrade.isEmpty() || subject.isEmpty() || title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            sendPlanToServer(classGrade, subject, title, content);
        });
    }

    private void sendPlanToServer(String classGrade, String subject, String title, String content) {
        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        String url = "http://10.0.2.2/api/add_preparation.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);
                    Toast.makeText(this, "Plan submitted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);
                    Toast.makeText(this, "Error submitting plan", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("class_grade", classGrade);
                params.put("subject_name", subject);
                params.put("title", title);
                params.put("content", content);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
