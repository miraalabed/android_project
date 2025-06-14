package com.example.schoolhub;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import java.util.*;

public class SendSurveyActivity extends AppCompatActivity {

    EditText etTitle, etQuestion, etStudentId;
    TextView tvDate;
    Button btnSend;
    String url = "http://10.0.2.2/api/add_survey.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_survey);

        etTitle = findViewById(R.id.etSurveyTitle);
        etQuestion = findViewById(R.id.etSurveyQuestion);
        etStudentId = findViewById(R.id.etStudentId);
        tvDate = findViewById(R.id.tvSurveyDate);
        btnSend = findViewById(R.id.btnSendSurvey);

        tvDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                tvDate.setText(year + "-" + (month + 1) + "-" + day);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSend.setOnClickListener(v -> {
            String title = etTitle.getText().toString();
            String question = etQuestion.getText().toString();
            String studentId = etStudentId.getText().toString();
            String date = tvDate.getText().toString();

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Toast.makeText(this, "Survey sent successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    },
                    error -> Toast.makeText(this, "Failed to send", Toast.LENGTH_SHORT).show()) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> map = new HashMap<>();
                    map.put("title", title);
                    map.put("question", question);
                    map.put("student_id", studentId);
                    map.put("date", date);
                    map.put("status", "Not Answered");
                    return map;
                }
            };
            Volley.newRequestQueue(this).add(request);
        });
    }
}
