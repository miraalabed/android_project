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
import java.util.List;

public class SurveysActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SurveyAdapter adapter;
    List<Survey> surveyList;
    ProgressBar progressBar;
    TextView emptyText;
    ImageView backButton;
    int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surveys);

        recyclerView = findViewById(R.id.recycler_surveys);
        progressBar = findViewById(R.id.progress_bar);
        emptyText = findViewById(R.id.text_empty);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        studentId = prefs.getInt("id", 0);

        surveyList = new ArrayList<>();
        adapter = new SurveyAdapter(this, surveyList, studentId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadSurveys();
    }

    private void loadSurveys() {
        progressBar.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);
        surveyList.clear();
        adapter.notifyDataSetChanged();

        String url = "http://10.0.2.2/api/get_surveys1.php?student_id=" + studentId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    Log.d("SERVER_RESPONSE", "Response: " + response);
                    try {
                        JSONObject object = new JSONObject(response);

                        if (object.getString("status").equals("success")) {
                            JSONArray array = object.getJSONArray("surveys");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                Survey s = new Survey(
                                        obj.getInt("id"),
                                        obj.getString("title"),
                                        obj.getString("status"),
                                        obj.getString("date")
                                );
                                surveyList.add(s);
                            }

                            adapter.notifyDataSetChanged();

                            if (surveyList.isEmpty()) {
                                emptyText.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(this, "No surveys found", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadSurveys();
        }
    }
}