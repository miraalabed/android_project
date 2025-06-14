package com.example.schoolhub;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddStudentActivity extends AppCompatActivity {

    EditText editFullName, editNationalId, editAddress, editBirthDate, editEmail, editPassword;
    Button btnAddStudent;
    String insertUrl = "http://10.0.2.2/api/add_student.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        ImageView btnHome = findViewById(R.id.home);
        ImageView btnProfile = findViewById(R.id.profile);
        ImageView btnLogout = findViewById(R.id.logout);

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
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

        editFullName = findViewById(R.id.editFullName);
        editNationalId = findViewById(R.id.editNationalId);
        editAddress = findViewById(R.id.editAddress);
        editBirthDate = findViewById(R.id.editBirthDate);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnAddStudent = findViewById(R.id.btnAddStudent);

        editBirthDate.setOnClickListener(view -> showDatePicker());
        setupValidation();

        btnAddStudent.setOnClickListener(view -> {
            if (validateAllFields()) {
                addStudentToDatabase();
            }
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            String formattedDate = String.format("%04d-%02d-%02d", y, m + 1, d);
            editBirthDate.setText(formattedDate);
        }, year, month, day);
        dialog.show();
    }

    private boolean isAgeValid(String birthDateStr) {
        try {
            String[] parts = birthDateStr.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            Calendar birthDate = Calendar.getInstance();
            birthDate.set(year, month - 1, day);
            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return age >= 6 && age <= 18;
        } catch (Exception e) {
            return false;
        }
    }

    private int getGradeFromBirthDate(String birthDateStr) {
        try {
            String[] parts = birthDateStr.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            Calendar birthDate = Calendar.getInstance();
            birthDate.set(year, month - 1, day);
            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            int grade = age - 5;
            return Math.max(1, Math.min(grade, 12));
        } catch (Exception e) {
            return 1;
        }
    }

    private void setupValidation() {
        editFullName.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
            public void onTextChanged(CharSequence s,int start,int before,int count) {
                editFullName.setError(s.length() > 50 ? "Max 50 characters allowed" : null);
            }
            public void afterTextChanged(Editable s) {}
        });

        editNationalId.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
            public void onTextChanged(CharSequence s,int start,int before,int count) {
                String input = s.toString();
                if (!input.matches("\\d{9}")) {
                    editNationalId.setError("National ID must be exactly 9 digits");
                } else {
                    editNationalId.setError(null);
                }
            }
            public void afterTextChanged(Editable s) {}
        });

        editEmail.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
            public void onTextChanged(CharSequence s,int start,int before,int count) {
                editEmail.setError(s.toString().endsWith("@school.ps") ? null : "Email must end with @school.ps");
            }
            public void afterTextChanged(Editable s) {}
        });

        editPassword.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
            public void onTextChanged(CharSequence s,int start,int before,int count) {
                editPassword.setError(s.toString().matches("^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z\\d]{1,15}$") ? null : "Password must contain letters and numbers, max 15 chars");
            }
            public void afterTextChanged(Editable s) {}
        });
    }

    private boolean validateAllFields() {
        boolean valid = true;

        String fullName = editFullName.getText().toString().trim();
        String nationalId = editNationalId.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String birthDate = editBirthDate.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString();

        if (fullName.isEmpty() || fullName.length() > 50) {
            editFullName.setError("Full name is required and max 50 characters");
            valid = false;
        }
        if (!nationalId.matches("\\d{9}")) {
            editNationalId.setError("National ID must be exactly 9 digits");
            valid = false;
        }
        if (address.isEmpty()) {
            editAddress.setError("Address is required");
            valid = false;
        }
        if (birthDate.isEmpty()) {
            editBirthDate.setError("Birth date is required");
            valid = false;
        } else if (!isAgeValid(birthDate)) {
            editBirthDate.setError("Age must be between 6 and 18 years");
            valid = false;
        }
        if (!email.endsWith("@school.ps")) {
            editEmail.setError("Email must end with @school.ps");
            valid = false;
        }
        if (!password.matches("^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z\\d]{1,15}$")) {
            editPassword.setError("Password must contain letters and numbers, max 15 chars");
            valid = false;
        }

        if (!valid) {
            Toast.makeText(this, "Please fix all errors before submitting", Toast.LENGTH_LONG).show();
        }
        return valid;
    }

    private void addStudentToDatabase() {
        String fullName = editFullName.getText().toString().trim();
        String nationalId = editNationalId.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String birthDate = editBirthDate.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString();
        int classGrade = getGradeFromBirthDate(birthDate);

        StringRequest request = new StringRequest(Request.Method.POST, insertUrl,
                response -> {
                    Toast.makeText(AddStudentActivity.this, "Student Added Successfully", Toast.LENGTH_LONG).show();
                    finish();
                },
                error -> Toast.makeText(AddStudentActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("full_name", fullName);
                params.put("national_id", nationalId);
                params.put("address", address);
                params.put("birth_date", birthDate);
                params.put("email", email);
                params.put("pass", password);
                params.put("role", "Student");
                params.put("class_grade", String.valueOf(classGrade));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
