package com.example.schoolhub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class AssignmentDetailsActivity extends AppCompatActivity {

    TextView tvAssignmentTitle, tvAssignmentDescription, tvAssignmentDueDate;
    EditText etReplyText;
    Button btnSubmitReply, btnChooseFile;
    ImageView backButton;

    int assignmentId, studentId;
    String title, description, dueDate;
    Uri selectedFileUri = null;

    private static final String SUBMIT_URL = "http://10.0.2.2/api/submit_assignment_reply.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assignment_details);

        // ربط العناصر
        tvAssignmentTitle = findViewById(R.id.tvAssignmentTitle);
        tvAssignmentDescription = findViewById(R.id.tvAssignmentDescription);
        tvAssignmentDueDate = findViewById(R.id.tvAssignmentDueDate);
        etReplyText = findViewById(R.id.etReplyText);
        btnSubmitReply = findViewById(R.id.btnSubmitReply);
        btnChooseFile = findViewById(R.id.btnChooseFile);
        backButton = findViewById(R.id.back_button);

        // جلب بيانات من intent
        Intent intent = getIntent();
        assignmentId = intent.getIntExtra("assignmentId", -1);
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");
        dueDate = intent.getStringExtra("dueDate");

        // جلب student_id من shared preferences
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        studentId = prefs.getInt("id", 0);

        // عرض البيانات
        tvAssignmentTitle.setText(title);
        tvAssignmentDescription.setText(description);
        tvAssignmentDueDate.setText("Due: " + dueDate);

        // رجوع
        backButton.setOnClickListener(v -> finish());

        // زر اختيار ملف
        btnChooseFile.setOnClickListener(v -> chooseFile());

        // إرسال الرد
        btnSubmitReply.setOnClickListener(v -> {
            String replyText = etReplyText.getText().toString().trim();
            if (replyText.isEmpty() && selectedFileUri == null) {
                Toast.makeText(this, "Please write an answer or choose a file", Toast.LENGTH_SHORT).show();
                return;
            }

            // حاليًا نرسل رابط الملف فقط كنص
            submitReply(replyText, selectedFileUri != null ? selectedFileUri.toString() : "");
        });
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(Intent.createChooser(intent, "Select File"));
    }

    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedFileUri = result.getData().getData();
                    if (selectedFileUri != null) {
                        String fileName = getFileName(selectedFileUri);
                        btnChooseFile.setText("File: " + fileName);
                    }
                }
            });

    private String getFileName(Uri uri) {
        String result = "Selected file";
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0) result = cursor.getString(nameIndex);
            }
        } catch (Exception e) {
            Log.e("FilePicker", "Error getting file name", e);
        }
        return result;
    }

    private void submitReply(String text, String link) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Submitting...");
        dialog.setCancelable(false);
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, SUBMIT_URL,
                response -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Reply submitted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Error submitting reply", Toast.LENGTH_SHORT).show();
                    Log.e("AssignmentSubmit", "Volley Error: " + error.getMessage());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("student_id", String.valueOf(studentId));
                map.put("assignment_id", String.valueOf(assignmentId));
                map.put("reply_content", text);
                map.put("reply_link", link);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
