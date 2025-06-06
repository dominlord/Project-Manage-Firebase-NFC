package com.example.duan_nckh;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailPhanHoiActivity extends AppCompatActivity {

    private TextView textViewUid, textViewPhanHoi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_phan_hoi);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // Tham chiếu đến TextView
        textViewUid = findViewById(R.id.textViewUid);
        textViewPhanHoi = findViewById(R.id.textViewPhanHoi);
        ImageView troveBtn = findViewById(R.id.backBtn);

        troveBtn.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Nhận dữ liệu từ Intent
        String email = getIntent().getStringExtra("email");
        String phanHoi = getIntent().getStringExtra("phanhoi");

        // Hiển thị dữ liệu lên giao diện
        if (email != null) {
            textViewUid.setText(email);
        } else {
            textViewUid.setText("Không có Email được truyền!");
        }

        if (phanHoi != null && !phanHoi.isEmpty()) {
            textViewPhanHoi.setText(phanHoi);
        } else {
            textViewPhanHoi.setText("Chưa có phản hồi.");
        }
    }
}
