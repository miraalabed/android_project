package com.example.schoolhub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class SurveyResponsesActivity extends AppCompatActivity {

    private ListView listView;
    private TextView textEmpty;
    private SearchView searchView;
    private SurveyStudentAdapter adapter;
    private List<SurveyStudent> originalList;

    int surveyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_responses);

        listView = findViewById(R.id.listViewStudents);
        textEmpty = findViewById(R.id.textEmpty);
        searchView = findViewById(R.id.searchViewStudents);
        ImageView goBack = findViewById(R.id.goback);

        goBack.setOnClickListener(v -> finish());

        surveyId = getIntent().getIntExtra("survey_id", -1);
        if (surveyId == -1) {
            Toast.makeText(this, "Invalid survey ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String url = "http://10.0.2.2/api/students.php?survey_id=" + surveyId;
        fetchSurveyResponses(url);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null && originalList != null) {
                    List<SurveyStudent> filtered = new ArrayList<>();
                    for (SurveyStudent student : originalList) {
                        if (student.getName().toLowerCase().contains(newText.toLowerCase())) {
                            filtered.add(student);
                        }
                    }
                    adapter = new SurveyStudentAdapter(SurveyResponsesActivity.this, filtered);
                    listView.setAdapter(adapter);
                }
                return true;
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            SurveyStudent selectedStudent = adapter.getItem(position);
            if (selectedStudent != null) {
                Intent intent = new Intent(SurveyResponsesActivity.this, SurveyAnswerDetailActivity.class);
                intent.putExtra("survey_id", surveyId);
                intent.putExtra("student_name", selectedStudent.getName());
                intent.putExtra("submitted_at", selectedStudent.getDate());
                startActivity(intent);
            }
        });
    }

    private void fetchSurveyResponses(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    List<SurveyStudent> list = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String name = obj.getString("full_name");
                            String date = obj.getString("submitted_at");
                            list.add(new SurveyStudent(name, date));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    originalList = list;
                    adapter = new SurveyStudentAdapter(this, list);
                    listView.setAdapter(adapter);
                    listView.setEmptyView(textEmpty);
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
                }
        );
        queue.add(jsonArrayRequest);
    }
}
