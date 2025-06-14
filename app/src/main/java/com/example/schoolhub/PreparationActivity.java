package com.example.schoolhub;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.*;

import java.util.*;

public class PreparationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PreparationAdapter adapter;
    List<PreparationPlan> planList = new ArrayList<>();
    ProgressBar progressBar;
    TextView emptyText;

    private static final String TAG = "PreparationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparation);

        recyclerView = findViewById(R.id.recycler_preparation);
        progressBar = findViewById(R.id.progress_bar);
        emptyText = findViewById(R.id.empty_text);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PreparationAdapter(planList);
        recyclerView.setAdapter(adapter);

        ImageView backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(v -> onBackPressed());

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        int classGrade = prefs.getInt("class_grade", 0);

        Log.d(TAG, "Loaded classGrade from SharedPreferences: " + classGrade);

        if (classGrade <= 0) {
            Toast.makeText(this, "Class grade not found", Toast.LENGTH_LONG).show();
            emptyText.setText("No class grade available");
            emptyText.setVisibility(View.VISIBLE);
            return;
        }

        fetchPreparationPlans(String.valueOf(classGrade));
    }

    private void fetchPreparationPlans(String classGrade) {
        progressBar.setVisibility(View.VISIBLE);

        Uri.Builder builder = Uri.parse("http://10.0.2.2/api/get_preparation.php").buildUpon();
        builder.appendQueryParameter("class_grade", classGrade);
        String url = builder.build().toString();

        Log.d(TAG, "Requesting plans from URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray array = response.getJSONArray("data");
                            planList.clear();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                PreparationPlan plan = new PreparationPlan(
                                        obj.getString("subject_name"),
                                        obj.getString("title"),
                                        obj.getString("content"),
                                        obj.getString("date")
                                );
                                planList.add(plan);
                            }

                            adapter.notifyDataSetChanged();
                            emptyText.setVisibility(planList.isEmpty() ? View.VISIBLE : View.GONE);

                        } else {
                            emptyText.setVisibility(View.VISIBLE);
                            emptyText.setText("No plans found for your class.");
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "JSON parsing error: ", e);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Volley error: ", error);
                });

        Volley.newRequestQueue(this).add(request);
    }
}