package com.example.schoolhub;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    EditText editFullName, editEmail, editAddress;
    Button btnSave;
    String updateUrl = "http://10.0.2.2/api/update_user_profile.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editFullName = findViewById(R.id.editFullName);
        editEmail = findViewById(R.id.editEmail);
        editAddress = findViewById(R.id.editAddress);
        btnSave = findViewById(R.id.btnSaveChanges);
        Button btnBack = findViewById(R.id.btnBack);

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("id", 0);
        String fullName = prefs.getString("fullName", "");
        String email = prefs.getString("email", "");
        String address = prefs.getString("address", "");

        editFullName.setText(fullName);
        editEmail.setText(email);
        editAddress.setText(address);

        btnSave.setOnClickListener(v -> updateUserProfile(userId));
        btnBack.setOnClickListener(v -> finish());
    }


    private void updateUserProfile(int userId) {
        String fullName = editFullName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String address = editAddress.getText().toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, updateUrl,
                response -> {
                    Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();

                    SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("fullName", fullName);
                    editor.putString("email", email);
                    editor.putString("address", address);
                    editor.apply();

                    finish();
                },
                error -> Toast.makeText(EditProfileActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(userId));
                params.put("full_name", fullName);
                params.put("email", email);
                params.put("address", address);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
