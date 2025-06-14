package com.example.schoolhub;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
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

public class StudentMessagesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MessageAdapter adapter;
    List<Message> messageList = new ArrayList<>();

    private static final String TAG = "STUDENT_MESSAGES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_messages);

        ImageButton backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recycler_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);

        loadMessages();
    }

    private void loadMessages() {
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        int studentId = prefs.getInt("id", 0);

        Log.d(TAG, "DEBUG_USER_ID: " + studentId);

        if (studentId <= 0) {
            Toast.makeText(this, "Invalid student ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String URL = "http://10.0.2.2/api/get_student_messages.php?receiver_id=" + studentId;
        Log.d(TAG, "Request URL: " + URL);

        StringRequest request = new StringRequest(Request.Method.GET, URL,
                response -> {
                    Log.d(TAG, "RAW_RESPONSE: " + response);
                    try {
                        JSONObject json = new JSONObject(response);
                        String status = json.getString("status");

                        if (status.equals("success")) {
                            JSONArray array = json.getJSONArray("data");
                            messageList.clear();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                messageList.add(new Message(
                                        obj.getInt("id"),
                                        obj.getString("title"),
                                        obj.getString("content"),
                                        obj.getString("sender_type"),
                                        obj.getString("sent_at"),
                                        obj.getInt("sender_id")
                                ));
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No messages found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("PARSE_ERROR", "Exception while parsing: " + e.getMessage());
                        Toast.makeText(this, "Parse error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("NETWORK_ERROR", "Volley error: " + error.toString());
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }
}