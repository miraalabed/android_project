package com.example.schoolhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class SurveyDetailsActivity extends AppCompatActivity {

    TextView surveyTitle, surveyQuestion;
    RadioGroup answerGroup;
    Button submitButton;
    ImageView backButton;

    int surveyId;
    int studentId;
    String selectedAnswer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_details);

        backButton = findViewById(R.id.back_button);
        surveyTitle = findViewById(R.id.survey_title);
        surveyQuestion = findViewById(R.id.survey_question);
        answerGroup = findViewById(R.id.answer_group);
        submitButton = findViewById(R.id.submit_button);

        surveyId = getIntent().getIntExtra("survey_id", -1);
        studentId = getIntent().getIntExtra("student_id", -1);
        String title = getIntent().getStringExtra("title");

        surveyTitle.setText(title);

        loadSurveyQuestion(surveyId);

        answerGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = findViewById(checkedId);
            selectedAnswer = selected.getText().toString();
        });

        backButton.setOnClickListener(v -> finish());

        submitButton.setOnClickListener(v -> {
            if (selectedAnswer.isEmpty()) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
                return;
            }

            submitAnswer();
        });
    }

    private void loadSurveyQuestion(int surveyId) {
        String url = "http://10.0.2.2/api/get_survey_question.php?survey_id=" + surveyId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("success")) {
                            String question = json.getString("question");
                            surveyQuestion.setText(question);
                        } else {
                            surveyQuestion.setText("Question not found.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        surveyQuestion.setText("Error loading question.");
                    }
                },
                error -> surveyQuestion.setText("Network error while loading question.")
        );

        Volley.newRequestQueue(this).add(request);
    }
    private void submitAnswer() {
        String url = "http://10.0.2.2/api/submit_survey_answer.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("success")) {
                            Toast.makeText(this, "Answer submitted successfully", Toast.LENGTH_SHORT).show();

                            setResult(RESULT_OK);
                            finish();

                        } else {
                            Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("student_id", String.valueOf(studentId));
                params.put("survey_id", String.valueOf(surveyId));
                params.put("answer", selectedAnswer);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
