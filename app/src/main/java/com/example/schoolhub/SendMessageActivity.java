package com.example.schoolhub;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.*;

import java.util.*;

public class SendMessageActivity extends AppCompatActivity {

    private Spinner spinnerStudents;
    private EditText editTitle, editContent;
    private Button buttonSend;
    private List<String> studentNames = new ArrayList<>();
    private List<String> studentIds = new ArrayList<>();

    private static final String URL_GET_STUDENTS = "http://10.0.2.2/api/get_student.php";
    private static final String URL_SEND_MESSAGE = "http://10.0.2.2/api/send_message.php";
    private static final String TAG = "SendMessageDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        spinnerStudents = findViewById(R.id.spinnerStudents);
        editTitle = findViewById(R.id.editTextTitle);
        editContent = findViewById(R.id.editTextContent);
        buttonSend = findViewById(R.id.buttonSend);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, studentNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStudents.setAdapter(adapter);

        loadStudents(adapter);

        buttonSend.setOnClickListener(v -> sendMessage());
    }

    private void loadStudents(ArrayAdapter<String> adapter) {
        StringRequest request = new StringRequest(Request.Method.GET, URL_GET_STUDENTS,
                response -> {
                    try {
                        Log.d(TAG, "Students loaded: " + response);
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            JSONArray data = jsonResponse.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                studentNames.add(obj.getString("full_name"));
                                studentIds.add(obj.getString("id"));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e(TAG, "No students found in response");
                            Toast.makeText(this, "No students found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parse error in loadStudents", e);
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Network error in loadStudents", error);
                    Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
                }
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void sendMessage() {
        int selectedPosition = spinnerStudents.getSelectedItemPosition();
        if (selectedPosition == -1) {
            Toast.makeText(this, "Please select a student", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No student selected");
            return;
        }

        String receiverId = studentIds.get(selectedPosition);
        String title = editTitle.getText().toString().trim();
        String content = editContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Empty title or content");
            return;
        }

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        int senderId = prefs.getInt("id", -1);
        if (senderId == -1) {
            Log.e(TAG, "Invalid sender ID from SharedPreferences");
            Toast.makeText(this, "Invalid sender ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String senderIdStr = String.valueOf(senderId);
        String senderType = "Teacher";
        String receiverType = "Student";

        Map<String, String> data = new HashMap<>();
        data.put("sender_id", senderIdStr);
        data.put("sender_type", senderType);
        data.put("receiver_id", receiverId);
        data.put("receiver_type", receiverType);
        data.put("title", title);
        data.put("content", content);

        Log.d(TAG, "Sending data: " + data);

        StringRequest request = new StringRequest(Request.Method.POST, URL_SEND_MESSAGE,
                response -> {
                    Log.d(TAG, "Response from server: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Log.e(TAG, "Server returned error: " + jsonResponse.getString("message"));
                            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing server response", e);
                        Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error on sending message", error);
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return data;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
