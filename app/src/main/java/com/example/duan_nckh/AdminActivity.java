package com.example.duan_nckh;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class AdminActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @SuppressLint({"MissingInflatedId", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        db = FirebaseFirestore.getInstance();

        ViewFlipper viewFlipperQR = findViewById(R.id.viewFlipperQR);
        ViewFlipper viewFlipperQuanLyTT = findViewById(R.id.viewFlipperQuanLyTT);
        ViewFlipper viewFlipperQuanLyTT1 = findViewById(R.id.viewFlipperQuanLyTT1);

        viewFlipperQR.setInAnimation(this, android.R.anim.slide_in_left);
        viewFlipperQuanLyTT.setInAnimation(this, android.R.anim.slide_in_left);
        viewFlipperQuanLyTT1.setInAnimation(this, android.R.anim.slide_in_left);

        viewFlipperQR.showNext();
        viewFlipperQuanLyTT.showNext();
        viewFlipperQuanLyTT1.showNext();

        new Handler().postDelayed(() -> {
            viewFlipperQR.stopFlipping();
            viewFlipperQuanLyTT.stopFlipping();
            viewFlipperQuanLyTT1.stopFlipping();

        }, 3000);


        findViewById(R.id.btn_quanlytintuc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, QuanLyTinTucActivity.class));
            }
        });

        findViewById(R.id.btnDiem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, SettingDiemActivity.class));
            }
        });


        // QR Code Button
        findViewById(R.id.btnScanQR).setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setPrompt("Scan QR Code");
            integrator.setOrientationLocked(true);
            integrator.setCaptureActivity(PortraitCaptureActivity.class);
            integrator.initiateScan();
        });

        // Bottom Navigation Setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home2);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home2) {
                return true;
            } else if (itemId == R.id.bottom_history) {
                startActivity(new Intent(getApplicationContext(), HistoryAdminActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String email = result.getContents();

                // Navigate to HienThiThongTinQRActivity and pass msv
                Intent intent = new Intent(this, HienThiThongTinQRActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Không tìm thấy nội dung QR!", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
