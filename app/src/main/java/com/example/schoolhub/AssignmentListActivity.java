package com.example.schoolhub;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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
import java.util.List;

public class AssignmentListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AssignmentAdapter1 adapter;
    List<Assignment> assignmentList = new ArrayList<>();
    int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assignment_list);

        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        studentId = sharedPreferences.getInt("id", 0);

        if (studentId == 0) {
            Toast.makeText(this, "Student ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerAssignments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        loadAssignments();
    }

    private void loadAssignments() {
        String url = "http://10.0.2.2/api/get_student_assignments.php?student_id=" + studentId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Log.d("ASSIGNMENT_RESPONSE", response);

                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("success")) {
                            JSONArray array = json.getJSONArray("data");
                            assignmentList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                Assignment assignment = new Assignment(
                                        obj.getInt("assignment_id"),
                                        obj.getString("title"),
                                        obj.getString("description"),
                                        obj.getString("due_date")
                                );
                                assignmentList.add(assignment);
                            }
                            adapter = new AssignmentAdapter1(this, assignmentList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(this, "No assignments found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}