package com.example.schoolhub;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class ExamsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView emptyText;
    ImageView backBtn;

    ArrayList<Exam> examList = new ArrayList<>();
    ExamsAdapter adapter;

    String API_URL = "http://10.0.2.2/api/get_exams.php?grade=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams);

        recyclerView = findViewById(R.id.examsRecyclerView);
        progressBar = findViewById(R.id.progress_bar);
        emptyText = findViewById(R.id.text_empty);
        backBtn = findViewById(R.id.back_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExamsAdapter(this, examList);
        recyclerView.setAdapter(adapter);

        backBtn.setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        int classGrade = prefs.getInt("class_grade", 1);

        loadExams(String.valueOf(classGrade));
    }

    private void loadExams(String grade) {
        progressBar.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);
        examList.clear();

        StringRequest request = new StringRequest(Request.Method.GET, API_URL + grade,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    Log.d("EXAMS_RESPONSE", response);

                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("success")) {
                            JSONArray exams = json.getJSONArray("exams");

                            for (int i = 0; i < exams.length(); i++) {
                                JSONObject obj = exams.getJSONObject(i);
                                examList.add(new Exam(
                                        obj.getString("subject_name"),
                                        obj.getString("exam_type"),
                                        obj.getString("exam_date"),
                                        obj.getString("description")
                                ));
                            }

                            if (examList.size() > 0) {
                                emptyText.setVisibility(View.GONE);
                            } else {
                                emptyText.setVisibility(View.VISIBLE);
                            }

                            adapter.notifyDataSetChanged();

                        } else {
                            emptyText.setText("Failed to load exams.");
                            emptyText.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                        emptyText.setText("Parsing error");
                        emptyText.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    error.printStackTrace();
                    Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    emptyText.setText("Network error");
                    emptyText.setVisibility(View.VISIBLE);
                });

        Volley.newRequestQueue(this).add(request);
    }
}
