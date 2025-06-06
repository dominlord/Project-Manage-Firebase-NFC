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
import com.google.firebase.auth.FirebaseAuth;

public class HistoryAdminActivity extends AppCompatActivity {

    RelativeLayout lichsuUser , lichsuAdmin , diemdanh , logoutButton;
    SharedPreferences sharedPreferences;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_admin);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        lichsuUser = findViewById(R.id.lichsuUser_btn);
        lichsuAdmin = findViewById(R.id.lichsuAdmin_btn);
        diemdanh = findViewById(R.id.diemdanh_btn);
        logoutButton = findViewById(R.id.logoutButton);

        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        ViewFlipper viewFlipperLichSuUser = findViewById(R.id.viewFlipperLSND);
        ViewFlipper viewFlipperLichSuAdmin = findViewById(R.id.viewFlipperLSNPT);
        ViewFlipper viewFlipperDiemDanh = findViewById(R.id.viewFlipperDiemDanh);
        ViewFlipper viewFlipperDangXuat = findViewById(R.id.viewFlipperDangXuat);

        viewFlipperLichSuUser.setInAnimation(this, android.R.anim.slide_in_left);
        viewFlipperLichSuAdmin.setInAnimation(this, android.R.anim.slide_in_left);
        viewFlipperDiemDanh.setInAnimation(this, android.R.anim.slide_in_left);
        viewFlipperDangXuat.setInAnimation(this, android.R.anim.slide_in_left);


        viewFlipperLichSuUser.showNext();
        viewFlipperLichSuAdmin.showNext();
        viewFlipperDiemDanh.showNext();
        viewFlipperDangXuat.showNext();

        new Handler().postDelayed(() -> {
            viewFlipperLichSuUser.stopFlipping();
            viewFlipperLichSuAdmin.stopFlipping();
            viewFlipperDiemDanh.stopFlipping();
            viewFlipperDangXuat.stopFlipping();

        }, 3000);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                clearSavedCredentials();
                Toast.makeText(HistoryAdminActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HistoryAdminActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        lichsuUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent9 = new Intent(HistoryAdminActivity.this, LichSuThayDoiUserActivity.class);
                startActivity(intent9);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        lichsuAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent10 = new Intent(HistoryAdminActivity.this, HienThiDanhSachUidActivity.class);
                startActivity(intent10);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        diemdanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent11 = new Intent(HistoryAdminActivity.this, DiemDanhActivity.class);
                startActivity(intent11);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_history);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home2) {
                startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_history) {
                return true;
            } else if (itemId == R.id.bottom_database) {
                startActivity(new Intent(getApplicationContext(), DataBaseAdminActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_helper) {
                startActivity(new Intent(getApplicationContext(), HelperAdminActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
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