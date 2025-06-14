package com.example.schoolhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class loginActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    TextView errorMessage;
    Button loginButton;
    CheckBox rememberCheckBox;
    SharedPreferences sharedPreferences;

    String URL = "http://10.0.2.2/api/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        errorMessage = findViewById(R.id.error_message);
        loginButton = findViewById(R.id.login_button);
        rememberCheckBox = findViewById(R.id.checkbox_remember);

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        String savedEmail = sharedPreferences.getString("email", null);
        String savedPass = sharedPreferences.getString("pass", null);

        if (savedEmail != null && savedPass != null) {
            usernameEditText.setText(savedEmail);
            passwordEditText.setText(savedPass);
            rememberCheckBox.setChecked(true);
        }

        loginButton.setOnClickListener(v -> {
            final String email = usernameEditText.getText().toString().trim();
            final String pass = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            StringRequest request = new StringRequest(Request.Method.POST, URL,
                    response -> {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("status").equals("success")) {

                                String role = json.getString("role");

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email", email);
                                editor.putString("pass", pass);
                                editor.putString("role", role);
                                editor.putString("name", json.getString("name"));
                                editor.putInt("id", json.getInt("id"));

                                if (role.equals("Student")) {
                                    editor.putString("national_id", json.getString("national_id"));
                                    editor.putInt("class_grade", json.getInt("class_grade"));
                                }

                                editor.apply();

                                Intent intent = null;
                                switch (role) {
                                    case "Student":
                                        intent = new Intent(this, MainActivity.class);
                                        break;
                                    case "Teacher":
                                        intent = new Intent(this, TeacherDashboardActivity.class);
                                        break;
                                    case "Registrar":
                                        intent = new Intent(this, RegisterActivity.class);
                                        break;
                                }

                                if (intent != null) {
                                    startActivity(intent);
                                    finish();
                                }

                            } else {
                                errorMessage.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("pass", pass);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        });
    }
}