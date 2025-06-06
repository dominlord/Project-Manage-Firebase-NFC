package com.example.duan_nckh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SupportActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        RelativeLayout PhanHoiNPT = findViewById(R.id.phanhoinpt_btn);
        RelativeLayout TinNhanPhanHoi = findViewById(R.id.tinhan_btn);

        ViewFlipper viewFlipperPhanHoiNPT = findViewById(R.id.viewFlipperPhanHoiNPT);
        ViewFlipper viewFlipperTinhan = findViewById(R.id.viewFlipperTinhan);

        viewFlipperPhanHoiNPT.setInAnimation(this, android.R.anim.slide_in_left);
        viewFlipperTinhan.setInAnimation(this, android.R.anim.slide_in_left);

        viewFlipperPhanHoiNPT.showNext();
        viewFlipperTinhan.showNext();

        new Handler().postDelayed(() -> {
            viewFlipperPhanHoiNPT.stopFlipping();
            viewFlipperTinhan.stopFlipping();

        }, 3000);

        PhanHoiNPT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SupportActivity.this, PhanHoiAdminActivity.class);
                startActivity(intent);
            }
        });

        TinNhanPhanHoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SupportActivity.this, NhanPhanHoiTuAdminActivity.class);
                startActivity(intent1);
            }
        });















        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_supports);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_supports) {
                return true;
            } else if (itemId == R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });

    }
}