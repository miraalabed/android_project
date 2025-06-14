package com.example.schoolhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SurveyListActivity extends AppCompatActivity {

    ListView listView;
    SearchView searchView;
    SurveyAdapter1 adapter;
    ArrayList<SurveyModel> surveyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_list);

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

        listView = findViewById(R.id.listViewSurveys);
        searchView = findViewById(R.id.searchViewSurveys);

        adapter = new SurveyAdapter1(this, surveyList);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        fetchSurveys();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            SurveyModel selected = adapter.getItem(position);
            Intent i = new Intent(this, SurveyResponsesActivity.class);

            i.putExtra("survey_id", Integer.parseInt(selected.getId()));
            i.putExtra("survey_title", selected.getTitle());
            i.putExtra("created_at", selected.getCreatedAt());
            startActivity(i);
        });
    }

    private void fetchSurveys() {
        StringRequest request = new StringRequest(Request.Method.GET, "http://10.0.2.2/api/get_surveys.php",
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        surveyList.clear();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String id = obj.getString("survey_id");
                            String title = obj.getString("survey_title");
                            String createdAt = obj.getString("created_at");
                            surveyList.add(new SurveyModel(id, title, createdAt));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error loading surveys", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }
}
