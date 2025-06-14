package com.example.schoolhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TeacherDashboardActivity extends AppCompatActivity {

    private TextView textViewWelcome;
    private Button buttonPublishMarks;
    private Button buttonViewReplies;
    private Button buttonresources ;

    private Button buttonSendAssignment;
    private Button buttonSendMessages;
    private Button buttonAddPreparation;
    private Button buttonViewSchedule;
    private Button buttonViewAssignment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        ImageView btnProfile = findViewById(R.id.profile);
        ImageView btnLogout = findViewById(R.id.logout);
        buttonresources = findViewById(R.id.resourceslerning);

        buttonViewReplies = findViewById(R.id.buttonViewReplies);
        textViewWelcome = findViewById(R.id.textViewWelcome);
        buttonPublishMarks = findViewById(R.id.buttonPublishMarks);
        buttonSendAssignment = findViewById(R.id.buttonSendAssignment);
        buttonViewSchedule = findViewById(R.id.buttonViewSchedule);
        buttonAddPreparation = findViewById(R.id.buttonAddPreparation);
        buttonSendMessages = findViewById(R.id.buttonSendMessages);
        buttonViewAssignment = findViewById(R.id.buttonViewAssignment);

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String teacherName = prefs.getString("name", "Teacher");
        textViewWelcome.setText("Welcome, " + teacherName + "!");

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
        buttonViewReplies.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, ViewRepliesMessageActivity.class);
            startActivity(intent);
        });
        buttonresources.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, UploadResourceActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this, "Logout success", Toast.LENGTH_LONG).show();
            finish();
        });

        buttonPublishMarks.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, PublishMarksActivity.class);
            startActivity(intent);
        });

        buttonSendAssignment.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, SendAssignmentActivity.class);
            startActivity(intent);
        });

        buttonViewSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, TeacherScheduleActivity.class);
            startActivity(intent);
        });

        buttonSendMessages.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, SendMessageActivity.class);
            startActivity(intent);
        });

        buttonAddPreparation.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, AddPreparationActivity.class);
            startActivity(intent);
        });

        buttonViewAssignment.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, TeacherAssignmentsActivity.class);
            startActivity(intent);
        });
    }
}
