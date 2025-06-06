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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import com.example.duan_nckh.NhapThongTinTongQuanActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Map;


public class ThongTinTongQuanActivity extends AppCompatActivity {
    private TextView tvKyNang, tvChungChi, tvLienHe, tvKinhNghiem;
    private RelativeLayout troveBtn, nhapthongtinBtn;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private ListenerRegistration listenerRegistration;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_thong_tin_tong_quan);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        tvKyNang = findViewById(R.id.kynangText);
        tvChungChi = findViewById(R.id.chungchiText);
        tvLienHe = findViewById(R.id.lienheEditText);
        tvKinhNghiem = findViewById(R.id.kinhnghiemText);
        troveBtn = findViewById(R.id.trovethongtintongquan_btn);
        nhapthongtinBtn = findViewById(R.id.nhapthongtintongquan_btn);

        // Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        String email = currentUser.getEmail();
        if (email != null) {
            String emailKey = email.replace(".", "_");
            fetchUserData(emailKey);
        } else {
            Toast.makeText(this, "Không tìm thấy người dùng, vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
        }

        troveBtn.setOnClickListener(v -> finish());

        nhapthongtinBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, NhapThongTinTongQuanActivity.class));
        });
    }

    private void fetchUserData(String emailKey) {
        DocumentReference userDocRef = db.collection("users").document(emailKey);

        listenerRegistration = userDocRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Lỗi khi lắng nghe dữ liệu: ", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Map<String, Object> tongQuanData = (Map<String, Object>) snapshot.get("tong_quan");

                if (tongQuanData != null) {
                    tvKyNang.setText((String) tongQuanData.get("kinang"));
                    tvChungChi.setText((String) tongQuanData.get("chungchi"));
                    tvLienHe.setText((String) tongQuanData.get("lienhe"));
                    tvKinhNghiem.setText((String) tongQuanData.get("kinhnghiem"));
                } else {
                    Log.d("Firestore", "Dữ liệu tong_quan không tồn tại!");
                    Toast.makeText(this, "Không có thông tin cá nhân!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("Firestore", "Tài liệu không tồn tại!");
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
