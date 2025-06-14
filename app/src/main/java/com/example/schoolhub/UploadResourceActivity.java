package com.example.schoolhub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class UploadResourceActivity extends AppCompatActivity {

    EditText editClass, editTitle, editDescription;
    Button btnUpload, btnChooseFile;
    ProgressBar progressBar;
    ImageView backButton;

    String URL = "http://10.0.2.2/api/upload_resource.php";
    private static final int PICK_FILE_REQUEST = 1;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_resource);

        editClass = findViewById(R.id.edit_class);
        editTitle = findViewById(R.id.edit_title);
        editDescription = findViewById(R.id.edit_description);
        btnUpload = findViewById(R.id.btn_upload_resource);
        btnChooseFile = findViewById(R.id.btnChooseFile);
        progressBar = findViewById(R.id.progress_bar);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> onBackPressed());

        btnChooseFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST);
        });

        btnUpload.setOnClickListener(v -> {
            String classGrade = editClass.getText().toString().trim();
            String title = editTitle.getText().toString().trim();
            String desc = editDescription.getText().toString().trim();

            if (classGrade.isEmpty() || title.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            uploadResource(classGrade, title, desc);
        });
    }

    private void uploadResource(String classGrade, String title, String desc) {
        progressBar.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.POST, URL,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Resource uploaded successfully", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("class_grade", classGrade);
                data.put("title", title);
                data.put("description", desc);
                data.put("file_link", fileUri != null ? fileUri.toString() : "");
                return data;
            }

        };

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            Toast.makeText(this, "File selected: " + fileUri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
        }
    }
}
