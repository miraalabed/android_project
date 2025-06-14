package com.example.schoolhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AddSubjectActivity extends AppCompatActivity {

    Spinner teacherSpinner;
    ArrayList<String> teacherNames = new ArrayList<>();
    ArrayList<String> teacherIds = new ArrayList<>();
    EditText editSubjectName, editDescription;
    Button btnAddSubject;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        teacherSpinner = findViewById(R.id.teacherSpinner);
        editSubjectName = findViewById(R.id.editSubjectName);
        editDescription = findViewById(R.id.editDescription);
        btnAddSubject = findViewById(R.id.btnAddSubject);
        ImageView btnHome = findViewById(R.id.home);
        ImageView btnProfile = findViewById(R.id.profile);
        ImageView btnLogout = findViewById(R.id.logout);

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this,ProfileActivity.class);
            startActivity(intent);
            finish();});

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
        requestQueue = Volley.newRequestQueue(this);

        loadTeachers();

        btnAddSubject.setOnClickListener(v -> {
            String subject = editSubjectName.getText().toString().trim();
            String desc = editDescription.getText().toString().trim();
            int selectedPosition = teacherSpinner.getSelectedItemPosition();

            if (subject.isEmpty() || desc.isEmpty() || selectedPosition == -1) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String teacherId = teacherIds.get(selectedPosition);
            addSubjectToDatabase(subject, desc, teacherId);
        });
    }

    private void loadTeachers() {
        String url = "http://10.0.2.2/api/get_users.php?search=&role=Teacher";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject teacher = response.getJSONObject(i);
                            teacherNames.add(teacher.getString("name"));
                            teacherIds.add(teacher.getString("id"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item, teacherNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        teacherSpinner.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to load teachers", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

    private void addSubjectToDatabase(String subject, String desc, String teacherId) {
        String url = "http://10.0.2.2/api/add_subject.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        String status = json.getString("status");
                        if (status.equals("success")) {
                            Toast.makeText(this, "Subject added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String msg = json.getString("message");
                            Toast.makeText(this, "Error: " + msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Invalid response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error saving subject", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("subject_name", subject);
                params.put("description", desc);
                params.put("teacher_id", teacherId);
                return params;
            }
        };

        requestQueue.add(request);
    }
}
