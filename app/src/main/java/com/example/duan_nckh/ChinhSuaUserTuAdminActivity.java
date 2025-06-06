package com.example.duan_nckh;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChinhSuaUserTuAdminActivity extends AppCompatActivity {

    private EditText editTen, editDiaChi, editSDT, editLop, editKhoa, editNganh, editMSV , editBHYT;
    private RelativeLayout btnSave, btnViewHistory;
    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chinh_sua_user_tu_admin);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        editTen = findViewById(R.id.editTen);
        editDiaChi = findViewById(R.id.editDiaChi);
        editSDT = findViewById(R.id.editSDT);
        editLop = findViewById(R.id.editLop);
        editKhoa = findViewById(R.id.editKhoa);
        editNganh = findViewById(R.id.editNganh);
        editMSV = findViewById(R.id.editMSV);
        editBHYT = findViewById(R.id.editBHYT);
        btnSave = findViewById(R.id.btnSave);
        btnViewHistory = findViewById(R.id.btnViewHistory);

        db = FirebaseFirestore.getInstance();

        uid = getIntent().getStringExtra("uid");

        ImageView backButton5 = findViewById(R.id.backBtn);

        backButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // Load user data
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> caNhan = (Map<String, Object>) documentSnapshot.get("ca_nhan");
                        if (caNhan != null) {
                            editTen.setText((String) caNhan.get("ten"));
                            editDiaChi.setText((String) caNhan.get("diachi"));
                            editSDT.setText((String) caNhan.get("sdt"));
                            editLop.setText((String) caNhan.get("lop"));
                            editKhoa.setText((String) caNhan.get("khoa"));
                            editNganh.setText((String) caNhan.get("nganh"));
                            editMSV.setText((String) caNhan.get("msv"));
                        }
                        Map<String, Object> tongQuan = (Map<String, Object>) documentSnapshot.get("tong_quan");
                        if (tongQuan != null) {
                            String bhyt = (String) tongQuan.get("BHYT");
                            editBHYT.setText(bhyt != null ? bhyt : ""); // Nếu không có BHYT, gán giá trị rỗng
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChinhSuaUserTuAdminActivity.this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
                });


        btnViewHistory.setOnClickListener(view -> {
            Intent intent = new Intent(ChinhSuaUserTuAdminActivity.this, LichSuSuaUidAdminActivity.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        });

        btnSave.setOnClickListener(view -> {
            // Lấy dữ liệu hiện tại trước khi cập nhật
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Lấy dữ liệu hiện tại của ca_nhan
                            Map<String, Object> currentData = (Map<String, Object>) documentSnapshot.get("ca_nhan");
                            if (currentData == null) {
                                currentData = new HashMap<>();
                            }

                            // Dữ liệu cập nhật cho ca_nhan
                            Map<String, Object> updatedCaNhan = new HashMap<>();
                            updatedCaNhan.put("ten", editTen.getText().toString().trim());
                            updatedCaNhan.put("diachi", editDiaChi.getText().toString().trim());
                            updatedCaNhan.put("sdt", editSDT.getText().toString().trim());
                            updatedCaNhan.put("lop", editLop.getText().toString().trim());
                            updatedCaNhan.put("khoa", editKhoa.getText().toString().trim());
                            updatedCaNhan.put("nganh", editNganh.getText().toString().trim());
                            updatedCaNhan.put("msv", editMSV.getText().toString().trim());

                            // Dữ liệu BHYT
                            String bhyt = editBHYT.getText().toString().trim();

                            // Lấy dữ liệu hiện tại của tong_quan
                            Map<String, Object> currentTongQuan = (Map<String, Object>) documentSnapshot.get("tong_quan");
                            if (currentTongQuan == null) {
                                currentTongQuan = new HashMap<>();
                            }

                            // Cập nhật BHYT
                            currentTongQuan.put("BHYT", bhyt);

                            // Lưu lịch sử thay đổi
                            saveHistoryLog(currentData, updatedCaNhan); // Lưu thay đổi của ca_nhan
                            saveHistoryLog(currentTongQuan, Collections.singletonMap("BHYT", bhyt)); // Lưu thay đổi của BHYT

                            // Cập nhật Firestore
                            Map<String, Object> finalCurrentTongQuan = currentTongQuan;
                            db.collection("users").document(uid)
                                    .set(Collections.singletonMap("ca_nhan", updatedCaNhan), SetOptions.merge())
                                    .addOnSuccessListener(unused -> {
                                        // Sau khi cập nhật ca_nhan thành công, cập nhật tong_quan
                                        db.collection("users").document(uid)
                                                .set(Collections.singletonMap("tong_quan", finalCurrentTongQuan), SetOptions.merge())
                                                .addOnSuccessListener(unused1 -> {
                                                    Toast.makeText(ChinhSuaUserTuAdminActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(ChinhSuaUserTuAdminActivity.this, "Lỗi khi cập nhật BHYT!", Toast.LENGTH_SHORT).show();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(ChinhSuaUserTuAdminActivity.this, "Lỗi khi cập nhật thông tin cá nhân!", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    });
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void saveHistoryLog(Map<String, Object> currentData, Map<String, Object> updatedData) {
        for (Map.Entry<String, Object> entry : updatedData.entrySet()) {
            String field = entry.getKey();
            String newValue = entry.getValue().toString();

            // Lấy giá trị cũ từ currentData
            String oldValue = "Chưa có"; // Giá trị mặc định nếu không tồn tại
            if (currentData.containsKey(field)) {
                Object currentFieldValue = currentData.get(field);
                oldValue = currentFieldValue != null ? currentFieldValue.toString() : "Chưa có";
            }

            // Chỉ lưu log nếu giá trị thay đổi
            if (!newValue.equals(oldValue)) {
                // Tạo bản ghi lịch sử
                Map<String, Object> historyLog = new HashMap<>();
                historyLog.put("field", field);
                historyLog.put("oldValue", oldValue);
                historyLog.put("newValue", newValue);
                historyLog.put("timestamp", FieldValue.serverTimestamp());

                // Lưu bản ghi lịch sử vào Firestore
                db.collection("users").document(uid).collection("history").add(historyLog)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("HistoryLog", "Lưu lịch sử thành công: " + field);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("HistoryLog", "Lỗi khi lưu lịch sử: " + e.getMessage());
                        });
            }
        }
    }
}
