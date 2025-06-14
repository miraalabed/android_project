package com.example.schoolhub;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.*;

import java.util.*;

public class ViewRepliesMessageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RepliesAdapter adapter;
    private List<ReplyModel> replies = new ArrayList<>();
    private static final String URL = "http://10.0.2.2/api/get_replies.php";
    private static final String TAG = "RepliesDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_replies);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewReplies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RepliesAdapter(replies, this);
        recyclerView.setAdapter(adapter);

        loadReplies();
    }

    private void loadReplies() {
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        int teacherId = prefs.getInt("id", -1);

        if (teacherId == -1) {
            Toast.makeText(this, "Invalid teacher ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String finalUrl = URL + "?teacher_id=" + teacherId;

        StringRequest request = new StringRequest(Request.Method.GET, finalUrl,
                response -> {
                    Log.d(TAG, "Response: " + response);
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("success")) {
                            JSONArray data = json.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject item = data.getJSONObject(i);
                                replies.add(new ReplyModel(
                                        item.getString("student_name"),
                                        item.getString("reply_content"),
                                        item.optString("reply_link", ""),
                                        item.getString("message_title"),
                                        item.getString("reply_date")
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No replies found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON error", e);
                        Toast.makeText(this, "Error parsing replies", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error", error);
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }
}
