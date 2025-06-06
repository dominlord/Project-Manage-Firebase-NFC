package com.example.duan_nckh;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class HienThiThongTinQR_UserActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hien_thi_thong_tin_qr_user);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        ImageView BackButton20 = findViewById(R.id.backBtn);

        BackButton20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        // Lấy `msv` từ Intent
        String email = getIntent().getStringExtra("email");

        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Không có Email được truyền!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tham chiếu đến Firestore
        db = FirebaseFirestore.getInstance();

        // Truy vấn thông tin dựa trên `msv`
        db.collection("users")
                .whereEqualTo("ca_nhan.email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        Map<String, Object> caNhan = (Map<String, Object>) document.get("ca_nhan");
                        Map<String, Object> tongQuan = (Map<String, Object>) document.get("tong_quan");

                        if (caNhan != null) {
                            String ten = (String) caNhan.get("ten");
                            String diachi = (String) caNhan.get("diachi");
                            String sdt = (String) caNhan.get("sdt");
                            String khoa = (String) caNhan.get("khoa");
                            String lop = (String) caNhan.get("lop");
                            String nganh = (String) caNhan.get("nganh");

                            // Hiển thị thông tin từ ca_nhan
                            setTextViewText(R.id.textViewHoVaTen12, ten);
                            setTextViewText(R.id.textViewDiaChi12, diachi);
                            setTextViewText(R.id.textViewSDT12, sdt);
                            setTextViewText(R.id.textViewKhoa12, khoa);
                            setTextViewText(R.id.textViewLop12, lop);
                            setTextViewText(R.id.textViewNganh12, nganh);
                        }

                        if (tongQuan != null) {
                            String chungchi = (String) tongQuan.get("chungchi");
                            String lienhe = (String) tongQuan.get("lienhe");
                            String kynang = (String) tongQuan.get("kinang");
                            String kinhnghiem = (String) tongQuan.get("kinhnghiem");

                            // Hiển thị thông tin từ tong_quan
                            setTextViewText(R.id.textViewChungChi12, chungchi);
                            setTextViewText(R.id.textViewLienHe12, lienhe);
                            setTextViewText(R.id.textViewKyNang12, kynang);
                            setTextViewText(R.id.textViewKinhNghiem12, kinhnghiem);
                        }
                    } else {
                        Toast.makeText(this, "Không tìm thấy thông tin với MSV!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreError", "Lỗi: ", e);
                });
    }

    private void setTextViewText(int textViewId, String text) {
        TextView textView = findViewById(textViewId);
        if (textView != null) {
            textView.setText(text != null ? text : "Không có dữ liệu");
        } else {
            Log.e("TextViewError", "TextView ID " + textViewId + " không tìm thấy!");
        }
    }
}