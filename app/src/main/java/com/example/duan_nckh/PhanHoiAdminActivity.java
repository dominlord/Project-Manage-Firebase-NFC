package com.example.duan_nckh;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PhanHoiAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phan_hoi_admin);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        ImageView troveBtn1 = findViewById(R.id.backBtn);
        EditText phanhoiAdmin = findViewById(R.id.phanhoiAdmin);
        Button sendButton = findViewById(R.id.SendBtn);
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();


        sendButton.setOnClickListener(view -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String email = currentUser.getEmail();
                String phanhoi = phanhoiAdmin.getText().toString().trim(); // Lấy nội dung từ EditText

                if (phanhoi.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập phản hồi trước khi gửi!", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.collection("requestPhanHoi").document(email).get()
                        .addOnSuccessListener(documentSnapshot -> {

                            /*
                            if (documentSnapshot.exists()) {
                                Timestamp lastRequest = documentSnapshot.getTimestamp("lastRequest");
                                if (lastRequest != null) {
                                    long now = System.currentTimeMillis();
                                    long lastTime = lastRequest.toDate().getTime();

                                    if (now - lastTime < 24 * 60 * 60 * 1000) {
                                        Toast.makeText(this, "Bạn chỉ được gửi phản hồi 1 lần trong 24h.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            } */

                            // Thêm phản hồi vào Firestore
                            Map<String, Object> request = new HashMap<>();
                            request.put("email", email);
                            request.put("status", "pending");
                            request.put("lastRequest", FieldValue.serverTimestamp());
                            request.put("phanhoi", phanhoi); // Lưu nội dung phản hồi

                            db.collection("requestPhanHoi").document(email).set(request)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Phản hồi đã được gửi, vui lòng chờ phản hồi từ Admin trong 24h.", Toast.LENGTH_SHORT).show();
                                        phanhoiAdmin.setText(""); // Xóa nội dung sau khi gửi
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi gửi phản hồi!", Toast.LENGTH_SHORT).show());
                        });
            }
        });


        troveBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}