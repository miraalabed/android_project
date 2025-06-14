package com.example.schoolhub;

import android.os.Bundle;
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

public class AnnouncementsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView emptyText;
    ImageView backButton;

    List<Announcement> announcementList = new ArrayList<>();
    AnnouncementAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);

        recyclerView = findViewById(R.id.recycler_announcements);
        progressBar = findViewById(R.id.progress_bar);
        emptyText = findViewById(R.id.text_empty);
        backButton = findViewById(R.id.back_button);

        adapter = new AnnouncementAdapter(announcementList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(v -> finish());

        loadAnnouncements();
    }

    private void loadAnnouncements() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);

        String url = "http://10.0.2.2/api/get_announcements.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("success")) {
                            JSONArray array = json.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                Announcement a = new Announcement(
                                        obj.getString("title"),
                                        obj.getString("content"),
                                        obj.getString("date")
                                );
                                announcementList.add(a);
                            }

                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            if (announcementList.isEmpty()) {
                                emptyText.setVisibility(View.VISIBLE);
                            }
                        } else {
                            showEmpty();
                        }
                    } catch (Exception e) {
                        showEmpty();
                    }
                },
                error -> showEmpty());

        Volley.newRequestQueue(this).add(request);
    }

    private void showEmpty() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyText.setVisibility(View.VISIBLE);
    }
}
