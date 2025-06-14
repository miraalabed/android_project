package com.example.schoolhub;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class MarksActivity extends AppCompatActivity {

    TableLayout marksTable;
    TextView averageText;
    ImageView backButton;
    int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);

        marksTable = findViewById(R.id.marks_table);
        averageText = findViewById(R.id.average_text);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        // ✅ تعديل هنا: استخدم المفتاح الصحيح "id" بدلاً من "student_id"
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        studentId = prefs.getInt("id", 0);

        if (studentId == 0) {
            Toast.makeText(this, "Student ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        loadMarks();
    }

    private void loadMarks() {
        String url = "http://10.0.2.2/api/get_student_grades.php?student_id=" + studentId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (!json.getString("status").equals("success")) {
                            Toast.makeText(this, "Failed to load marks", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray array = json.getJSONArray("data");
                        double total = 0;

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String subject = obj.getString("subject_name");
                            String mark = obj.getString("mark");

                            TableRow row = new TableRow(this);

                            TextView subjectText = new TextView(this);
                            subjectText.setText(subject);
                            subjectText.setPadding(8, 8, 8, 8);
                            subjectText.setTextColor(Color.parseColor("#008080"));
                            subjectText.setGravity(Gravity.CENTER);

                            TextView markText = new TextView(this);
                            markText.setText(mark);
                            markText.setPadding(8, 8, 8, 8);
                            markText.setTextColor(Color.parseColor("#008080"));
                            markText.setGravity(Gravity.CENTER);

                            row.addView(subjectText);
                            row.addView(markText);
                            marksTable.addView(row);

                            total += Double.parseDouble(mark);
                        }

                        if (array.length() > 0) {
                            double avg = total / array.length();
                            averageText.setText("Average: " + String.format("%.2f", avg));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}
