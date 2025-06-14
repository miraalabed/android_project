package com.example.schoolhub;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;

import java.util.HashMap;
import java.util.Map;

public class ReplyMessageActivity extends AppCompatActivity {

    TextView replyTitle;
    EditText replyInput;
    Button sendReplyButton;
    ImageButton backBtn;

    int studentId, teacherId, messageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_message);

        replyTitle = findViewById(R.id.reply_title);
        replyInput = findViewById(R.id.reply_content);
        sendReplyButton = findViewById(R.id.btn_send_reply);
        backBtn = findViewById(R.id.btn_back);

        String title = getIntent().getStringExtra("title");
        teacherId = getIntent().getIntExtra("sender_id", 0);
        messageId = getIntent().getIntExtra("message_id", 0);
        replyTitle.setText("RE: " + title);

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        studentId = prefs.getInt("id", 0);

        backBtn.setOnClickListener(v -> finish());

        sendReplyButton.setOnClickListener(v -> sendReply());
    }

    private void sendReply() {
        String replyText = replyInput.getText().toString().trim();

        if (replyText.isEmpty()) {
            Toast.makeText(this, "Please enter a reply", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/api/send_reply.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Reply sent successfully", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to send reply", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("message_id", String.valueOf(messageId));
                params.put("reply_content", replyText);
                params.put("reply_sender_id", String.valueOf(studentId));
                params.put("reply_sender_type", "Student");
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}