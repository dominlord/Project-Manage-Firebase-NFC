package com.example.duan_nckh;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class ReadPostActivity extends AppCompatActivity {

    TextView titleTextView, contentTextView;
    RelativeLayout deleteButton  , backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_post);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        titleTextView = findViewById(R.id.titleTextView);
        contentTextView = findViewById(R.id.contentTextView);
        deleteButton = findViewById(R.id.deleteButton);
        backButton = findViewById(R.id.backButton);

        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        String postId = getIntent().getStringExtra("postId");
        boolean isFromManage = getIntent().getBooleanExtra("isFromManage", false);

        titleTextView.setText(title);
        contentTextView.setText(content);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

       


        // Chỉ hiện nút xoá nếu từ ManageActivity
        if (isFromManage && postId != null) {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(v -> {
                new AlertDialog.Builder(ReadPostActivity.this) // Sử dụng đúng context
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc muốn xoá bài viết này không?")
                        .setPositiveButton("Xoá", (dialog, which) -> {
                            FirebaseFirestore.getInstance()
                                    .collection("posts")
                                    .document(postId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(ReadPostActivity.this, "Đã xoá", Toast.LENGTH_SHORT).show();

                                        setResult(RESULT_OK);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Delete", "Xoá thất bại", e);
                                        Toast.makeText(ReadPostActivity.this, "Lỗi khi xoá", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .setNegativeButton("Huỷ", null)
                        .show();
            });
        } else {
            deleteButton.setVisibility(Button.GONE); // Ẩn nút nếu không từ ManageActivity
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Reload lại bài viết từ Firestore
            String postId = getIntent().getStringExtra("postId");
            FirebaseFirestore.getInstance().collection("posts").document(postId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            titleTextView.setText(documentSnapshot.getString("title"));
                            contentTextView.setText(documentSnapshot.getString("content"));
                            setResult(RESULT_OK);
                        }
                    });
        }
    }



}