package com.example.schoolhub;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResourcesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ResourceAdapter adapter;
    List<Resource> resourceList = new ArrayList<>();
    ProgressBar progressBar;
    TextView emptyText;
    String classGrade = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_resources);

        recyclerView = findViewById(R.id.recycler_resources);
        progressBar = findViewById(R.id.progress_bar);
        emptyText = findViewById(R.id.empty_text);
        ImageView backButton = findViewById(R.id.back_button);

        adapter = new ResourceAdapter(this, resourceList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(v -> onBackPressed());

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        int grade = prefs.getInt("class_grade", 0);
        classGrade = String.valueOf(grade);

        loadResources();
    }

    private void loadResources() {
        progressBar.setVisibility(View.VISIBLE);

        String url = "http://10.0.2.2/api/get_resources.php?class_grade=" + classGrade;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray array = response.getJSONArray("data");
                            resourceList.clear();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                Resource res = new Resource(
                                        obj.getString("title"),
                                        obj.getString("description"),
                                        obj.getString("file_link"),
                                        obj.getString("created_at")
                                );
                                resourceList.add(res);
                            }

                            adapter.notifyDataSetChanged();
                            if (resourceList.isEmpty()) emptyText.setVisibility(View.VISIBLE);
                            else emptyText.setVisibility(View.GONE);
                        } else {
                            emptyText.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }
}