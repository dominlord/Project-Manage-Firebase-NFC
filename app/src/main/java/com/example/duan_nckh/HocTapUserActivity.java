package com.example.duan_nckh;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HocTapUserActivity extends AppCompatActivity {

    TextView txtRenLuyen, txtLichSu, txtToan, txtHanhKiem;
    FirebaseFirestore db;
    FirebaseAuth auth;
    ImageView backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hoc_tap_user);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        txtRenLuyen = findViewById(R.id.txtRenLuyen);
        txtLichSu = findViewById(R.id.txtLichSu);
        txtToan = findViewById(R.id.txtToan);
        txtHanhKiem = findViewById(R.id.txtHanhKiem);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();

            db.collection("users")
                    .whereEqualTo("ca_nhan.email", email)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                            Map<String, Object> diemHocTap = (Map<String, Object>) documentSnapshot.get("diem_hoc_tap");

                            if (diemHocTap != null) {
                                Object renLuyen = diemHocTap.get("diemRenLuyen");
                                Object lichSu = diemHocTap.get("diemLichSu");
                                Object toan = diemHocTap.get("diemToan");
                                Object hanhKiem = diemHocTap.get("hanhKiem");

                                txtRenLuyen.setText("Rèn luyện: " + (renLuyen != null ? renLuyen.toString() : "Chưa có điểm"));
                                txtLichSu.setText("Lịch sử: " + (lichSu != null ? lichSu.toString() : "Chưa có điểm"));
                                txtToan.setText("Toán: " + (toan != null ? toan.toString() : "Chưa có điểm"));
                                txtHanhKiem.setText("Hạnh kiểm: " + (hanhKiem != null ? hanhKiem.toString() : "Chưa có điểm"));
                            } else {
                                showNoScore();
                            }
                        } else {
                            showNoScore();
                        }
                    })
                    .addOnFailureListener(e -> showNoScore());
        }

    }

    private void showNoScore() {
        txtRenLuyen.setText("Rèn luyện: Chưa có điểm");
        txtLichSu.setText("Lịch sử: Chưa có điểm");
        txtToan.setText("Toán: Chưa có điểm");
        txtHanhKiem.setText("Hạnh kiểm: Chưa có điểm");
    }
}