package com.example.duan_nckh;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuanLyTinTucActivity extends AppCompatActivity {
    RelativeLayout btnCreate, btnManage ;
    ImageView troveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quan_ly_tin_tuc);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        btnCreate = findViewById(R.id.btnCreate);
        btnManage = findViewById(R.id.btnManage);
        troveBtn = findViewById(R.id.backBtn);

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

        troveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnCreate.setOnClickListener(v -> {
            startActivity(new Intent(QuanLyTinTucActivity.this, CreatePostActivity.class));
        });

        btnManage.setOnClickListener(v -> {
            startActivity(new Intent(QuanLyTinTucActivity.this, ManagePostActivity.class));
        });


    }
}