package com.example.schoolhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class SurveyAnswerDetailActivity extends AppCompatActivity {

    TextView textStudentName, textSubmittedAt;
    LinearLayout layoutAnswersContainer;
    String studentName, submittedAt;
    int surveyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_answer_detail);

        ImageView btnHome = findViewById(R.id.home);
        ImageView btnProfile = findViewById(R.id.profile);
        ImageView btnLogout = findViewById(R.id.logout);
        ImageView btnBack = findViewById(R.id.goback);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
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

        textStudentName = findViewById(R.id.textStudentName);
        textSubmittedAt = findViewById(R.id.textSubmittedAt);
        layoutAnswersContainer = findViewById(R.id.layoutAnswersContainer);

        surveyId = getIntent().getIntExtra("survey_id", -1);
        studentName = getIntent().getStringExtra("student_name");
        submittedAt = getIntent().getStringExtra("submitted_at");

        if (surveyId == -1 || studentName == null || submittedAt == null) {
            Toast.makeText(this, "Missing data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textStudentName.setText("Student: " + studentName);
        textSubmittedAt.setText("Submitted at: " + submittedAt);

        loadAnswers();
    }

    private void loadAnswers() {
        String url = "http://10.0.2.2/api/get_survey_answer.php?survey_id=" + surveyId + "&student_name=" + Uri.encode(studentName);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        layoutAnswersContainer.removeAllViews();

                        if (array.length() == 0) {
                            TextView empty = new TextView(this);
                            empty.setText("No answers found.");
                            layoutAnswersContainer.addView(empty);
                            return;
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String question = obj.getString("question");
                            String answer = obj.getString("answer");

                            TextView questionView = new TextView(this);
                            questionView.setText("Question: " + question);
                            questionView.setTextColor(getResources().getColor(R.color.teal_primary));
                            questionView.setTextSize(16f);
                            questionView.setTypeface(null, android.graphics.Typeface.BOLD);
                            questionView.setPadding(0, 16, 0, 4);

                            TextView answerView = new TextView(this);
                            answerView.setText("Answer: " + answer);
                            answerView.setTextColor(getResources().getColor(android.R.color.black));
                            answerView.setTextSize(16f);
                            answerView.setPadding(0, 0, 0, 12);

                            layoutAnswersContainer.addView(questionView);
                            layoutAnswersContainer.addView(answerView);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parse error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }
}
