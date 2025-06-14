package com.example.schoolhub;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AnnouncementDetailsActivity extends AppCompatActivity {

    TextView titleView, dateView, contentView;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_details);

        titleView = findViewById(R.id.detailTitle);
        dateView = findViewById(R.id.detailDate);
        contentView = findViewById(R.id.detailContent);
        backButton = findViewById(R.id.back_button);

        String title = getIntent().getStringExtra("title");
        String date = getIntent().getStringExtra("date");
        String content = getIntent().getStringExtra("content");

        titleView.setText(title);
        dateView.setText(date);
        contentView.setText(content);

        backButton.setOnClickListener(v -> finish());
    }
}
