package com.example.duan_nckh;

import android.content.Intent;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NhapThongTinCaNhanActivity extends AppCompatActivity {

    private EditText hovatenEditText, diachiEditText, sdtEditText, lopEditText,
            msvEditText, khoaEditText, nganhEditText;
    FirebaseFirestore db;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nhap_thong_tin_ca_nhan);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // Khởi tạo view từ layout
        hovatenEditText = findViewById(R.id.hovatenEditText);
        diachiEditText = findViewById(R.id.diachiEditText);
        sdtEditText = findViewById(R.id.sdtEditText);
        lopEditText = findViewById(R.id.lopEditText);
        msvEditText = findViewById(R.id.msvEditText);
        khoaEditText = findViewById(R.id.khoaEditText);
        nganhEditText = findViewById(R.id.nganhEditText);

        db = FirebaseFirestore.getInstance();

        RelativeLayout trove1 = findViewById(R.id.trove3_btn);
        RelativeLayout nhapthongtincanhanh = findViewById(R.id.nhapthongtintongquan_btn);

        // Nút trở về: chỉ cần finish Activity
        trove1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        if (currentUser != null) {
            // Sử dụng email làm khóa, chuyển đổi ký tự chấm thành dấu gạch dưới để tránh lỗi
            String email = currentUser.getEmail();
            String emailKey = email.replace(".", "_");

            // Nếu đã đăng nhập, đọc dữ liệu từ Firestore dựa trên email
            db.collection("users").document(emailKey).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Nếu có, lấy thông tin cá nhân
                            Map<String, Object> caNhan = (Map<String, Object>) documentSnapshot.get("ca_nhan");
                            if (caNhan != null) {
                                hovatenEditText.setText((String) caNhan.get("ten"));
                                diachiEditText.setText((String) caNhan.get("diachi"));
                                sdtEditText.setText((String) caNhan.get("sdt"));
                                lopEditText.setText((String) caNhan.get("lop"));
                                khoaEditText.setText((String) caNhan.get("khoa"));
                                nganhEditText.setText((String) caNhan.get("nganh"));
                                msvEditText.setText((String) caNhan.get("msv"));

                                // Nếu MSV đã tồn tại thì không cho chỉnh sửa thêm
                                if (caNhan.containsKey("msv") && caNhan.get("msv") != null) {
                                    msvEditText.setEnabled(false);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(NhapThongTinCaNhanActivity.this,
                                "Lỗi khi tải dữ liệu!",
                                Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Lỗi khi lấy dữ liệu cá nhân", e);
                    });
        }

        // Xử lý sự kiện khi bấm nút “Nhập thông tin cá nhân”
        nhapthongtincanhanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    // Sử dụng email làm khóa, chuyển đổi ký tự chấm thành dấu gạch dưới
                    String email = currentUser.getEmail();
                    String emailKey = email.replace(".", "_");

                    db.collection("users").document(emailKey).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                Map<String, Object> caNhan = null;
                                String existingMsv = null;

                                if (documentSnapshot.exists()) {
                                    caNhan = (Map<String, Object>) documentSnapshot.get("ca_nhan");
                                    if (caNhan != null) {
                                        existingMsv = (String) caNhan.get("msv");
                                    }
                                }

                                // Lấy thông tin từ giao diện
                                String ten = hovatenEditText.getText().toString().trim();
                                String diachi = diachiEditText.getText().toString().trim();
                                String sdt = sdtEditText.getText().toString().trim();
                                String lop = lopEditText.getText().toString().trim();
                                String khoa = khoaEditText.getText().toString().trim();
                                String nganh = nganhEditText.getText().toString().trim();
                                String msv = msvEditText.getText().toString().trim();

                                // Kiểm tra logic của MSV: nếu đã có và thay đổi thì báo lỗi
                                if (existingMsv != null && !existingMsv.isEmpty()) {
                                    if (!existingMsv.equals(msv)) {
                                        Toast.makeText(NhapThongTinCaNhanActivity.this, "MSV đã tồn tại và không thể thay đổi!", Toast.LENGTH_SHORT).show();
                                        msvEditText.setText(existingMsv);
                                        msvEditText.setEnabled(false);
                                        return;
                                    }
                                }

                                // Chuẩn bị dữ liệu cập nhật, lưu thêm email vào dữ liệu (dùng cho việc hiển thị sau này)
                                Map<String, Object> updatedCaNhan = new HashMap<>();
                                updatedCaNhan.put("ten", ten);
                                updatedCaNhan.put("diachi", diachi);
                                updatedCaNhan.put("sdt", sdt);
                                updatedCaNhan.put("lop", lop);
                                updatedCaNhan.put("khoa", khoa);
                                updatedCaNhan.put("nganh", nganh);
                                updatedCaNhan.put("msv", existingMsv == null || existingMsv.isEmpty() ? msv : existingMsv);
                                updatedCaNhan.put("email", email);

                                // Ghi lại lịch sử các thay đổi (chỉ ghi khi trường thay đổi)
                                if (caNhan != null) {
                                    for (String key : updatedCaNhan.keySet()) {
                                        String oldValue = caNhan.containsKey(key) ? (String) caNhan.get(key) : null;
                                        String newValue = (String) updatedCaNhan.get(key);

                                        if (oldValue != null && !oldValue.equals(newValue)) {
                                            Map<String, Object> historyLog = new HashMap<>();
                                            historyLog.put("field", key);
                                            historyLog.put("oldValue", oldValue);
                                            historyLog.put("newValue", newValue);
                                            historyLog.put("timestamp", FieldValue.serverTimestamp());

                                            // Ghi lịch sử sửa vào subcollection historyUser
                                            db.collection("users").document(emailKey)
                                                    .collection("historyUser")
                                                    .add(historyLog)
                                                    .addOnSuccessListener(unused -> Log.d("Firestore", "Lịch sử sửa đã được lưu cho field: " + key))
                                                    .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi lưu lịch sử sửa cho field: " + key, e));
                                        }
                                    }
                                }

                                // Lưu thông tin cá nhân vào document (sử dụng email làm khóa)
                                db.collection("users").document(emailKey)
                                        .set(Collections.singletonMap("ca_nhan", updatedCaNhan), SetOptions.merge())
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(NhapThongTinCaNhanActivity.this,
                                                    "Cập nhật thông tin thành công!",
                                                    Toast.LENGTH_SHORT).show();
                                            Log.d("Firestore", "Thông tin cá nhân đã được lưu.");
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(NhapThongTinCaNhanActivity.this,
                                                    "Cập nhật thông tin thất bại!",
                                                    Toast.LENGTH_SHORT).show();
                                            Log.e("Firestore", "Lỗi khi lưu thông tin cá nhân", e);
                                        });

                                // Nếu document không tồn tại trước đó, tạo mới
                                if (!documentSnapshot.exists()) {
                                    Toast.makeText(NhapThongTinCaNhanActivity.this,
                                            "Dữ liệu chưa tồn tại! Tạo mới dữ liệu.",
                                            Toast.LENGTH_SHORT).show();

                                    Map<String, Object> newCaNhan = new HashMap<>();
                                    newCaNhan.put("ten", ten);
                                    newCaNhan.put("diachi", diachi);
                                    newCaNhan.put("sdt", sdt);
                                    newCaNhan.put("lop", lop);
                                    newCaNhan.put("khoa", khoa);
                                    newCaNhan.put("nganh", nganh);
                                    newCaNhan.put("msv", msv);
                                    newCaNhan.put("email", email);

                                    db.collection("users").document(emailKey)
                                            .set(Collections.singletonMap("ca_nhan", newCaNhan))
                                            .addOnSuccessListener(unused -> Toast.makeText(NhapThongTinCaNhanActivity.this,
                                                    "Tạo mới thông tin thành công!",
                                                    Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi tạo mới dữ liệu", e));
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(NhapThongTinCaNhanActivity.this,
                                        "Lỗi khi lấy dữ liệu người dùng!",
                                        Toast.LENGTH_SHORT).show();
                                Log.e("Firestore", "Lỗi khi lấy dữ liệu người dùng", e);
                            });
                } else {
                    Toast.makeText(NhapThongTinCaNhanActivity.this,
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
}
