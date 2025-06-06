package com.example.duan_nckh;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LichSuSuaUidAdminActivity extends AppCompatActivity {

    private ListView historyListView;
    private ArrayAdapter<String> adapter;
    private List<String> historyList = new ArrayList<>();
    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lich_su_sua_uid_admin);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        historyListView = findViewById(R.id.historyListView1);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        historyListView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        uid = getIntent().getStringExtra("uid");

        ImageView backButton2 = findViewById(R.id.backBtn);

        backButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // Kiểm tra sự tồn tại của collection history
        DocumentReference userDocRef = db.collection("users").document(uid);
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Toast.makeText(this, "Người dùng không tồn tại!", Toast.LENGTH_SHORT).show();
                return;
            }

            userDocRef.collection("history")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.isEmpty()) {
                            createSampleData();
                        } else {
                            loadHistory();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi kiểm tra lịch sử", Toast.LENGTH_SHORT).show());
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateHistory(String field, String oldValue, String newValue) {
        Map<String, Object> historyData = new HashMap<>();
        historyData.put("field", field);
        historyData.put("oldValue", oldValue);
        historyData.put("newValue", newValue);
        historyData.put("timestamp", FieldValue.serverTimestamp());

        // Thêm vào collection historyUser của người dùng
        db.collection("users").document(uid).collection("historyUser")
                .add(historyData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Lịch sử thay đổi được cập nhật thành công.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi lưu lịch sử thay đổi", e);
                });
    }



    private void createSampleData() {
        Map<String, Object> defaultData = new HashMap<>();
        defaultData.put("field", "N/A");
        defaultData.put("oldValue", "N/A");
        defaultData.put("newValue", "N/A");
        defaultData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users").document(uid).collection("history")
                .add(defaultData)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Tạo dữ liệu mẫu thành công!", Toast.LENGTH_SHORT).show();
                    loadHistory();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi tạo dữ liệu mẫu!", Toast.LENGTH_SHORT).show());
    }

    private void loadHistory() {
        db.collection("users").document(uid).collection("history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    historyList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String field = document.getString("field");
                        String oldValue = document.getString("oldValue");
                        String newValue = document.getString("newValue");
                        String timestamp = document.getDate("timestamp") != null
                                ? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(document.getDate("timestamp"))
                                : "Không xác định";

                        historyList.add("Admin đã sửa " + field + " từ \"" + oldValue + "\" sang \"" + newValue + "\" vào " + timestamp + ".");
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(LichSuSuaUidAdminActivity.this, "Lỗi khi tải lịch sử", Toast.LENGTH_SHORT).show());
    }
}
