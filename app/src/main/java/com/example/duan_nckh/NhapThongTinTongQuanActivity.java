package com.example.duan_nckh;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NhapThongTinTongQuanActivity extends AppCompatActivity {
    private EditText kinangEditText, chungchiEditText, lienheEditText, kinhnghiemEditText;
    private RelativeLayout nhapTongQuanButton, trove;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nhap_thong_tin_tong_quan);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        kinangEditText = findViewById(R.id.kynangEditText);
        chungchiEditText = findViewById(R.id.chungchiEditText);
        lienheEditText = findViewById(R.id.lienheEditText);
        kinhnghiemEditText = findViewById(R.id.kinhnghiemEditText);
        trove = findViewById(R.id.trove3_btn);
        nhapTongQuanButton = findViewById(R.id.nhapthongtintongquan_btn);
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // Sử dụng email thay cho uid, thay đổi ký tự chấm thành dấu gạch dưới
            String email = currentUser.getEmail();
            String emailKey = email.replace(".", "_");

            db.collection("users").document(emailKey)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> data = (Map<String, Object>) documentSnapshot.get("tong_quan");
                            if (data != null) {
                                kinangEditText.setText(data.get("kinang") != null ? data.get("kinang").toString() : "");
                                chungchiEditText.setText(data.get("chungchi") != null ? data.get("chungchi").toString() : "");
                                lienheEditText.setText(data.get("lienhe") != null ? data.get("lienhe").toString() : "");
                                kinhnghiemEditText.setText(data.get("kinhnghiem") != null ? data.get("kinhnghiem").toString() : "");
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi tải dữ liệu", e));
        }

        trove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        nhapTongQuanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser != null) {
                    // Sử dụng email thay cho uid, chuyển ký tự chấm thành dấu gạch dưới
                    String email = currentUser.getEmail();
                    String emailKey = email.replace(".", "_");

                    String kinang = kinangEditText.getText().toString().trim();
                    String chungchi = chungchiEditText.getText().toString().trim();
                    String lienhe = lienheEditText.getText().toString().trim();
                    String kinhnghiem = kinhnghiemEditText.getText().toString().trim();
                    String key = generateRandomKey(); // Gọi hàm tạo số ngẫu nhiên

                    if (kinang.isEmpty() || chungchi.isEmpty() || lienhe.isEmpty() || kinhnghiem.isEmpty()) {
                        Toast.makeText(NhapThongTinTongQuanActivity.this,
                                "Vui lòng nhập đầy đủ thông tin",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> tongQuan = new HashMap<>();
                    tongQuan.put("kinang", kinang);
                    tongQuan.put("chungchi", chungchi);
                    tongQuan.put("lienhe", lienhe);
                    tongQuan.put("kinhnghiem", kinhnghiem);
                    tongQuan.put("key", key); // Thêm key vào dữ liệu

                    db.collection("users").document(emailKey)
                            .update("tong_quan", tongQuan)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(NhapThongTinTongQuanActivity.this,
                                        "Cập nhật thông tin tổng quan thành công!",
                                        Toast.LENGTH_SHORT).show();
                                Log.d("Firestore", "Thông tin tổng quan đã được cập nhật.");
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(NhapThongTinTongQuanActivity.this,
                                        "Cập nhật thông tin thất bại!",
                                        Toast.LENGTH_SHORT).show();
                                Log.e("Firestore", "Lỗi khi cập nhật thông tin tổng quan", e);
                            });
                } else {
                    Toast.makeText(NhapThongTinTongQuanActivity.this,
                            "Người dùng chưa đăng nhập!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Xử lý khoảng cách viền màn hình (Edge-to-Edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private String generateRandomKey() {
        Random random = new Random();
        StringBuilder keyBuilder = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            keyBuilder.append(random.nextInt(10)); // Thêm số ngẫu nhiên từ 0-9
            if (i % 4 == 3 && i != 11) {
                keyBuilder.append(" "); // Cứ 4 chữ số thì thêm dấu cách
            }
        }
        return keyBuilder.toString();
    }
}
