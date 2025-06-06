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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Map;

public class ThongTinCaNhanActivity extends AppCompatActivity {

    private TextView hovatenText, diachiText, sdtText, lopText, msvText, khoaText, nganhText;
    private RelativeLayout troveBtn, nhapthongtinBtn;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thong_tin_ca_nhan);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        hovatenText = findViewById(R.id.hovatenText);
        diachiText = findViewById(R.id.diachiText);
        sdtText = findViewById(R.id.sdtText);
        lopText = findViewById(R.id.lopText);
        msvText = findViewById(R.id.msvText);
        khoaText = findViewById(R.id.khoaText);
        nganhText = findViewById(R.id.nganhText);
        troveBtn = findViewById(R.id.trovethongtintongquan_btn);
        nhapthongtinBtn = findViewById(R.id.nhapthongtintongquan_btn);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        troveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        nhapthongtinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ThongTinCaNhanActivity.this, NhapThongTinCaNhanActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        FirebaseUser currentUser = auth.getCurrentUser();
        String email = currentUser.getEmail();
        if (email != null) {
            String emailKey = email.replace(".", "_");
            fetchUserData(emailKey);
        }
        else {
            Toast.makeText(this, "Không tìm thấy người dùng, vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //scan data from firebase
    private void fetchUserData(String emailKey) {
        DocumentReference userDocRef = db.collection("users").document(emailKey);

        listenerRegistration = userDocRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Lỗi khi lắng nghe dữ liệu: ", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Map<String, Object> caNhanData = (Map<String, Object>) snapshot.get("ca_nhan");

                if (caNhanData != null) {
                    hovatenText.setText((String) caNhanData.get("ten"));
                    diachiText.setText((String) caNhanData.get("diachi"));
                    sdtText.setText((String) caNhanData.get("sdt"));
                    lopText.setText((String) caNhanData.get("lop"));
                    msvText.setText((String) caNhanData.get("msv"));
                    khoaText.setText((String) caNhanData.get("khoa"));
                    nganhText.setText((String) caNhanData.get("nganh"));
                } else {
                    Log.d("Firestore", "Dữ liệu ca_nhan không tồn tại!");
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
