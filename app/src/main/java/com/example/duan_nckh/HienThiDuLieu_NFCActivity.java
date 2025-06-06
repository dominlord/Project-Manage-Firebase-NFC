package com.example.duan_nckh;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class HienThiDuLieu_NFCActivity extends AppCompatActivity {

    private TextView tvData;
    private FloatingActionButton btnClick;
    private FirebaseFirestore db;
    private String nfcData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hien_thi_du_lieu_nfcactivity);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        tvData = findViewById(R.id.tvData12);
        btnClick = findViewById(R.id.btnClick);
        db = FirebaseFirestore.getInstance();

        nfcData = getIntent().getStringExtra("nfcData");
        tvData.setText(nfcData != null ? nfcData : "Không có dữ liệu từ thẻ NFC.");

        FloatingActionButton BackButton201 = findViewById(R.id.BackButton201);

        BackButton201.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnClick.setOnClickListener(v -> checkData(nfcData));
    }

    private void checkData(String nfcData) {
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean found = false;

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> tongQuan = (Map<String, Object>) document.get("tong_quan");
                        Map<String, Object> caNhan = (Map<String, Object>) document.get("ca_nhan");

                        if (tongQuan != null) {
                            String keyFromDB = (String) tongQuan.get("key");

                            // Kiểm tra key có trùng khớp không
                            if (nfcData != null && keyFromDB != null && keyFromDB.equals(nfcData)) {
                                found = true;

                                // 📌 Lấy thông tin từ "ca_nhan"
                                if (caNhan != null) {
                                    setTextViewText(R.id.textViewHoVaTen11, (String) caNhan.get("ten"));
                                    setTextViewText(R.id.textViewDiaChi11, (String) caNhan.get("diachi"));
                                    setTextViewText(R.id.textViewSDT11, (String) caNhan.get("sdt"));
                                    setTextViewText(R.id.textViewKhoa11, (String) caNhan.get("khoa"));
                                    setTextViewText(R.id.textViewLop11, (String) caNhan.get("lop"));
                                    setTextViewText(R.id.textViewNganh11, (String) caNhan.get("nganh"));
                                }

                                // 📌 Lấy thông tin từ "tong_quan"
                                setTextViewText(R.id.textViewChungChi11, (String) tongQuan.get("chungchi"));
                                setTextViewText(R.id.textViewLienHe11, (String) tongQuan.get("lienhe"));
                                setTextViewText(R.id.textViewKyNang11, (String) tongQuan.get("kinang"));
                                setTextViewText(R.id.textViewKinhNghiem11, (String) tongQuan.get("kinhnghiem"));

                                break;
                            }
                        }
                    }

                    if (!found) {
                        Toast.makeText(HienThiDuLieu_NFCActivity.this, "Không tìm thấy dữ liệu trùng khớp!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HienThiDuLieu_NFCActivity.this, "Lỗi khi truy vấn dữ liệu!", Toast.LENGTH_SHORT).show();
                });
    }

    private void setTextViewText(int textViewId, String value) {
        TextView textView = findViewById(textViewId);
        if (textView != null) {
            textView.setText(value != null ? value : "Chưa có dữ liệu");
        }
    }
}