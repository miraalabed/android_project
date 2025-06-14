package com.example.schoolhub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);


        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity1.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }, 4*1000);
    }
}