package com.example.schoolhub;

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

public class AssignmentRepliesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AssignmentReplyAdapter adapter;
    List<AssignmentReply> replyList = new ArrayList<>();
    int assignmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_replies);

        recyclerView = findViewById(R.id.recyclerReplies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        assignmentId = getIntent().getIntExtra("assignmentId", -1);
        if (assignmentId == -1) {
            Toast.makeText(this, "Missing assignment ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadReplies();
    }

    private void loadReplies() {
        String url = "http://10.0.2.2/api/get_assignment_replies.php?assignment_id=" + assignmentId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("success")) {
                            JSONArray array = json.getJSONArray("data");
                            replyList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                AssignmentReply reply = new AssignmentReply(
                                        obj.getString("student_name"),
                                        obj.getString("reply_text"),
                                        obj.getString("reply_link")
                                );
                                replyList.add(reply);
                            }
                            adapter = new AssignmentReplyAdapter(this, replyList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(this, "No replies found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("RepliesParse", "Error parsing", e);
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}
