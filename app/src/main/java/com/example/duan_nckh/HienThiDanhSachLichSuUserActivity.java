package com.example.duan_nckh;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class HienThiDanhSachLichSuUserActivity extends AppCompatActivity {

    private ListView historyListView;
    private ArrayAdapter<String> historyAdapter;
    private ArrayList<String> historyList;
    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hien_thi_danh_sach_lich_su_user);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        historyListView = findViewById(R.id.historyListView3);
        historyList = new ArrayList<>();
        historyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        historyListView.setAdapter(historyAdapter);

        db = FirebaseFirestore.getInstance();

        // Lấy UID từ Intent
        uid = getIntent().getStringExtra("UID");

        ImageView backbutton4 = findViewById(R.id.backBtn);

        backbutton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        if (uid != null) {
            loadHistoryData();
        }
    }

    private void loadHistoryData() {
        db.collection("users").document(uid).collection("historyUser")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot historyDoc : querySnapshot.getDocuments()) {
                        String field = historyDoc.getString("field");
                        String oldValue = historyDoc.getString("oldValue");
                        String newValue = historyDoc.getString("newValue");
                        Timestamp timestamp = historyDoc.getTimestamp("timestamp");
                        
                        String log = String.format("Đã sửa %s từ '%s' thành '%s' vào ngày %s",
                                field != null ? field : "Không xác định",
                                oldValue != null ? oldValue : "Chưa có",
                                newValue != null ? newValue : "Chưa có",
                                timestamp != null ? timestamp.toDate().toString() : "Không xác định");

                        historyList.add(log);
                    }
                    historyAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi lấy lịch sử thay đổi!", e));
    }
}
