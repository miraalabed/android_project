package com.example.schoolhub;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONObject;
import org.json.JSONException;

public class AccountSettingsActivity extends AppCompatActivity {

    EditText etName, etEmail, etPhone, etAddress;
    Button btnEdit;
    ImageView backButton;
    int studentId;
    boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnEdit = findViewById(R.id.btnEdit);
        backButton = findViewById(R.id.back_button);

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        studentId = prefs.getInt("id", 0);

        if (studentId == 0) {
            Toast.makeText(this, "Student ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        backButton.setOnClickListener(v -> finish());

        loadStudentInfo();

        btnEdit.setOnClickListener(v -> {
            if (isEditing) {
                saveStudentInfo();
            } else {
                toggleFields(true);
            }
        });
    }

    private void toggleFields(boolean editable) {
        etName.setEnabled(editable);
        etEmail.setEnabled(editable);
        etPhone.setEnabled(editable);
        etAddress.setEnabled(editable);
        btnEdit.setText(editable ? "Save" : "Edit");
        isEditing = editable;
    }

    private void loadStudentInfo() {
        String url = "http://10.0.2.2/api/get_student_info.php?student_id=" + studentId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            JSONObject data = jsonResponse.getJSONObject("data");
                            String name = data.getString("name");
                            String email = data.getString("email");
                            String phone = data.getString("phone");
                            String address = data.getString("address");

                            etName.setText(name);
                            etEmail.setText(email);
                            etPhone.setText(phone);
                            etAddress.setText(address);
                        } else {
                            Toast.makeText(this, "Student not found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void saveStudentInfo() {
        String url = "http://10.0.2.2/api/update_student_info.php";
        JSONObject data = new JSONObject();
        try {
            data.put("id", studentId);
            data.put("name", etName.getText().toString().trim());
            data.put("email", etEmail.getText().toString().trim());
            data.put("phone", etPhone.getText().toString().trim());
            data.put("address", etAddress.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data,
                response -> {
                    toggleFields(false);
                    Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
                },
                error -> Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }
}
