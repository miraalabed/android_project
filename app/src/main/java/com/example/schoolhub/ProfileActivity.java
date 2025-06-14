package com.example.schoolhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvFullName, tvNationalId, tvEmail, tvBirhdate , tvAddress, tvRole;
    private ImageView profileImage, btnHome, btnProfile, btnLogout;
    private int userId;
    private Uri imageUri;

    private final String baseUrl = "http://10.0.2.2/api/get_user_profile.php";

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUri = uri;
                    profileImage.setImageURI(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvFullName = findViewById(R.id.tvFullName);
        tvNationalId = findViewById(R.id.tvNationalId);
        tvEmail = findViewById(R.id.tvEmail);
        tvAddress = findViewById(R.id.tvAddress);
        tvBirhdate = findViewById(R.id.tvBirthdate);
        tvRole = findViewById(R.id.tvRole);
        profileImage = findViewById(R.id.profileImage);
        btnHome = findViewById(R.id.home);
        btnProfile = findViewById(R.id.profile);
        btnLogout = findViewById(R.id.logout);

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        userId = prefs.getInt("id", 0);

        if (userId != -1) {
            fetchUserProfile();
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
        }

        profileImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });

        btnProfile.setOnClickListener(v -> {
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this, "Logout success", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void fetchUserProfile() {
        String url = baseUrl + "?user_id=" + userId;

        StringRequest req = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        tvFullName.setText(obj.getString("full_name"));
                        tvEmail.setText("Email: " + obj.getString("email"));

                        String nationalId = obj.getString("national_id");
                        String role = obj.getString("role");
                        tvRole.setText("Role: " + role);
                        tvNationalId.setText("National ID: " + nationalId);
                        tvAddress.setText("Address: " + obj.getString("address"));
                        tvBirhdate.setText("Birth Date: " + obj.getString("birth_date"));



                    } catch (Exception e) {
                        Toast.makeText(this, "Parse error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(req);
    }

}
