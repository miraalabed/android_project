package com.example.schoolhub;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolhub.R;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<TaskItem> tasks = new ArrayList<>();
    TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        recyclerView = findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        ImageView btnProfile = findViewById(R.id.profile);
        ImageView btnLogout = findViewById(R.id.logout);

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);

        });
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
        tasks.add(new TaskItem("Add New Student", R.drawable.addstudent));
        tasks.add(new TaskItem("Add New Teacher", R.drawable.addteacher));
        tasks.add(new TaskItem("Add New Subject", R.drawable.addsubject));
        tasks.add(new TaskItem("View Users", R.drawable.user));
        tasks.add(new TaskItem("View Subjects", R.drawable.sub));
        tasks.add(new TaskItem("Build Class Schedule", R.drawable.schedule));
        tasks.add(new TaskItem("Build Teacher Schedule", R.drawable.schedule1));
        tasks.add(new TaskItem("Assign Exam", R.drawable.assignexam));
        tasks.add(new TaskItem("View Survey Results", R.drawable.survey));
        tasks.add(new TaskItem("Send Survey to Student", R.drawable.survey));



        adapter = new TaskAdapter(this , tasks);
        recyclerView.setAdapter(adapter);
    }
}
