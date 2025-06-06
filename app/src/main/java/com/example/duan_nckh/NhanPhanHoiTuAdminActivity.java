package com.example.duan_nckh;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NhanPhanHoiTuAdminActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<PhanHoi> adapter;
    private List<PhanHoi> phanhoiList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhan_phan_hoi_tu_admin);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        listView = findViewById(R.id.listPhanHoi);
        phanhoiList = new ArrayList<>();
        ImageView troveBtn1 = findViewById(R.id.backBtn);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, phanhoiList);
        listView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        troveBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentEmail = currentUser.getEmail();

            db.collection("phanhoiToUser")
                    .whereEqualTo("to", currentEmail)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(this, "Chưa có phản hồi nào.", Toast.LENGTH_SHORT).show();
                        } else {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                String from = doc.getString("from");
                                String noidung = doc.getString("phanhoi");
                                Timestamp ts = doc.getTimestamp("timestamp");
                                Date date = ts != null ? ts.toDate() : new Date();

                                phanhoiList.add(new PhanHoi(from, noidung, date));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi tải phản hồi:\n" + e.getMessage(), Toast.LENGTH_LONG).show());
        }

        // Hiển thị nội dung phản hồi khi người dùng nhấn vào mục
        listView.setOnItemClickListener((parent, view, position, id) -> {
            PhanHoi ph = phanhoiList.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Nội dung phản hồi")
                    .setMessage(ph.noidung)
                    .setPositiveButton("Đóng", null)
                    .show();
        });
    }
}
