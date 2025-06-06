package com.example.duan_nckh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        RelativeLayout thongtincanhan = findViewById(R.id.thongtincanhan_btn);
        RelativeLayout thongtintongquan = findViewById(R.id.thongtintongquan_btn);
        RelativeLayout buttonGuiYeuCau = findViewById(R.id.guiyeucau_btn);
        RelativeLayout logoutButton = findViewById(R.id.logoutButton);

        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        ViewFlipper viewFlipperProfile = findViewById(R.id.viewFlipperProfile);
        ViewFlipper viewFlipperTongQuan = findViewById(R.id.viewFlipperTongQuan);
        ViewFlipper viewFlipperGuiThongTin = findViewById(R.id.viewFlipperGuiThongTin);
        ViewFlipper viewFlipperDangXuat = findViewById(R.id.viewFlipperDangXuat);


        viewFlipperProfile.setInAnimation(this, android.R.anim.slide_in_left);
        viewFlipperTongQuan.setInAnimation(this, android.R.anim.slide_in_left);
        viewFlipperGuiThongTin.setInAnimation(this, android.R.anim.slide_in_left);
        viewFlipperDangXuat.setInAnimation(this, android.R.anim.slide_in_left);

        viewFlipperProfile.showNext();
        viewFlipperTongQuan.showNext();
        viewFlipperGuiThongTin.showNext();
        viewFlipperDangXuat.showNext();

        new Handler().postDelayed(() -> {
            viewFlipperProfile.stopFlipping();
            viewFlipperTongQuan.stopFlipping();
            viewFlipperGuiThongTin.stopFlipping();
            viewFlipperDangXuat.stopFlipping();
        }, 3000);


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                clearSavedCredentials();
                Toast.makeText(ProfileActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        //xử lí gửi yêu cầu
        buttonGuiYeuCau.setOnClickListener(view -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String email = currentUser.getEmail();
                db.collection("requests").document(email).get()
                        .addOnSuccessListener(documentSnapshot -> {

                            /*
                            if (documentSnapshot.exists()) {
                                Timestamp lastRequest = documentSnapshot.getTimestamp("lastRequest");
                                if (lastRequest != null) {
                                    long now = System.currentTimeMillis();
                                    long lastTime = lastRequest.toDate().getTime();

                                    if (now - lastTime < 24 * 60 * 60 * 1000) {
                                        Toast.makeText(this, "Bạn chỉ được gửi yêu cầu 1 lần trong 24h.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            } */

                            Map<String, Object> request = new HashMap<>();
                            request.put("email", email);
                            request.put("status", "pending");
                            request.put("lastRequest", FieldValue.serverTimestamp());

                            db.collection("requests").document(email).set(request)
                                    .addOnSuccessListener(unused -> Toast.makeText(this, "Thông tin đã được tiếp nhận, vui lòng chờ 24h sau Admin phản hồi.", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi gửi yêu cầu!", Toast.LENGTH_SHORT).show());
                        });
            }
        });

        //xử lí Intent
        thongtincanhan.setOnClickListener(view -> {
            Intent intent2 = new Intent(ProfileActivity.this, ThongTinCaNhanActivity.class);
            startActivity(intent2);
        });

        thongtintongquan.setOnClickListener(view -> {
            Intent intent1 = new Intent(ProfileActivity.this, ThongTinTongQuanActivity.class);
            startActivity(intent1);
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (itemId == R.id.bottom_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (itemId == R.id.bottom_supports) {
                startActivity(new Intent(getApplicationContext(), SupportActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (itemId == R.id.bottom_profile) {
                return true;
            }
            return false;
        });


    }

    private void clearSavedCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}