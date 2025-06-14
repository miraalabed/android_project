package com.example.schoolhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ImageView studentImage;
    TextView studentName, studentId;

    Button btnSurveys, btnAgenda, btnNews, btnAnnouncements,
            btnPlan, btnResources, btnMessages, btnSchedules,
            btnHomework, btnExams, btnMarks, btnSettings;

    int studentID;
    String name, nationalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        studentImage = findViewById(R.id.student_image);
        studentName = findViewById(R.id.student_name);
        studentId = findViewById(R.id.student_id);

        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        studentID = sharedPreferences.getInt("student_id", 0);
        name = sharedPreferences.getString("name", "No Name");
        nationalId = sharedPreferences.getString("national_id", "N/A");


        studentName.setText(name);
        studentId.setText("National ID: " + nationalId);

        btnSurveys = findViewById(R.id.btn_surveys);
        btnAgenda = findViewById(R.id.btn_agenda);
        btnNews = findViewById(R.id.btn_news);
        btnAnnouncements = findViewById(R.id.btn_announcements);
        btnPlan = findViewById(R.id.btn_plan);
        btnResources = findViewById(R.id.btn_resources);
        btnMessages = findViewById(R.id.btn_messages);
        btnSchedules = findViewById(R.id.btn_schedules);
        btnHomework = findViewById(R.id.btn_homework);
        btnExams = findViewById(R.id.btn_exams);
        btnMarks = findViewById(R.id.btn_mark);
        btnSettings = findViewById(R.id.btn_settings);


        btnMarks.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MarksActivity.class);
            intent.putExtra("student_id", studentID);
            startActivity(intent);
        });

        btnExams.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExamsActivity.class);
            startActivity(intent);
        });

        btnHomework.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AssignmentListActivity.class);
            startActivity(intent);
        });
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AccountSettingsActivity.class);
            startActivity(intent);
        });


        btnSchedules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });
        btnMessages.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StudentMessagesActivity.class);
            intent.putExtra("id", studentID);
            startActivity(intent);
        });

        btnPlan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PreparationActivity.class);
            intent.putExtra("class_grade", "9A");
            startActivity(intent);
        });

        btnResources.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ResourcesActivity.class);
            startActivity(intent);
        });
        btnSurveys.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SurveysActivity.class);
            intent.putExtra("id", studentID);
            intent.putExtra("name", name);
            intent.putExtra("national_id", nationalId);
            startActivity(intent);
        });
        btnNews.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NewsActivity.class);
            startActivity(intent);
        });
        btnResources.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ResourcesActivity.class);
            startActivity(intent);
        });
        btnAgenda.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AgendaActivity.class);
            startActivity(intent);
        });
        btnAnnouncements.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AnnouncementsActivity.class);
            startActivity(intent);
        });



    }
}
