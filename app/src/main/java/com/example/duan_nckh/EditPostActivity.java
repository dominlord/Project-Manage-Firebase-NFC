package com.example.duan_nckh;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditPostActivity extends AppCompatActivity {
    private EditText titleEditText, contentEditText;
    private RelativeLayout saveButton;
    private String postId;
    private FirebaseFirestore db;
    private ImageView Backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        Backbtn = findViewById(R.id.backBtn);
        saveButton = findViewById(R.id.saveButton);

        db = FirebaseFirestore.getInstance();
        postId = getIntent().getStringExtra("postId");

        Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // Load dữ liệu bài viết
        db.collection("posts").document(postId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        titleEditText.setText(document.getString("title"));
                        contentEditText.setText(document.getString("content"));
                    }
                });

        saveButton.setOnClickListener(v -> {
            String newTitle = titleEditText.getText().toString();
            String newContent = contentEditText.getText().toString();

            db.collection("posts").document(postId)
                    .update("title", newTitle, "content", newContent)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish(); // Quay về
                    });
        });
    }
}
