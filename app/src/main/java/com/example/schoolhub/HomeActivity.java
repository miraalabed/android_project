package com.example.schoolhub;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class HomeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "LanguagePrefs";
    private static final String LANGUAGE_KEY = "Language";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(e -> {
            Intent intent = new Intent(HomeActivity.this, loginActivity.class);
            startActivity(intent);
            finish();
        });

    }
}
