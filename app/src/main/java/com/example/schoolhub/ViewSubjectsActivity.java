package com.example.schoolhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewSubjectsActivity extends AppCompatActivity {

    SearchView searchView;
    ListView listView;
    ArrayAdapter<Subject> adapter;
    List<Subject> subjectList = new ArrayList<>();

    RequestQueue requestQueue;
    String serverUrl = "http://10.0.2.2/api/getAllSubjects.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_subjects);

        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listViewSubjects);

        requestQueue = Volley.newRequestQueue(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetchSubjects(newText);
                return true;
            }
        });

        fetchSubjects("");

        findViewById(R.id.home).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });

        findViewById(R.id.profile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        findViewById(R.id.logout).setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void fetchSubjects(String keyword) {
        String url = serverUrl + "?search=" + Uri.encode(keyword);

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                Log.d("SUBJECTS_RESPONSE", response);

                JSONArray array = new JSONArray(response);
                subjectList.clear();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String name = obj.getString("subject_name");
                    String desc = obj.getString("description");
                    String teacher = obj.getString("teacher_name");

                    subjectList.add(new Subject(name, desc, teacher));
                }

                SubjectAdapter adapter = new SubjectAdapter(this, subjectList);
                listView.setAdapter(adapter);

            } catch (JSONException e) {
                Toast.makeText(this, "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, error -> {
            Toast.makeText(this, "Network Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        });

        requestQueue.add(request);
    }

}
