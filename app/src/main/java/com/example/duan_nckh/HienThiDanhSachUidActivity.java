package com.example.duan_nckh;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HienThiDanhSachUidActivity extends AppCompatActivity {

    private SearchView uidSearchView; // Khai báo SearchView
    private ListView uidListView;
    private ArrayAdapter<String> adapter;
    private List<String> uidList = new ArrayList<>();
    private List<String> filteredUidList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hien_thi_danh_sach_uid);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        uidSearchView = findViewById(R.id.uidSearchView2);
        uidListView = findViewById(R.id.uidListView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredUidList);
        uidListView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        ImageView backButton1 = findViewById(R.id.backBtn);

        backButton1.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        //change color for search view
        int hintTextid = uidSearchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = uidSearchView.findViewById(hintTextid);
        textView.setHintTextColor(ContextCompat.getColor(this, R.color.white));
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));

        // Tải UIDs từ Firestore
        db.collection("users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        uidList.add(document.getId());
                    }
                    filteredUidList.addAll(uidList); // Ban đầu hiển thị toàn bộ danh sách
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load UIDs", Toast.LENGTH_SHORT).show());

        uidListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUid = filteredUidList.get(position);
            Intent intent = new Intent(HienThiDanhSachUidActivity.this, ChinhSuaUserTuAdminActivity.class);
            intent.putExtra("uid", selectedUid);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Xử lý tìm kiếm
        uidSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUidList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUidList(newText);
                return true;
            }
        });
    }

    private void filterUidList(String query) {
        filteredUidList.clear();
        if (query.isEmpty()) {
            filteredUidList.addAll(uidList); // Hiển thị tất cả nếu không có từ khóa
        } else {
            for (String uid : uidList) {
                if (uid.toLowerCase().contains(query.toLowerCase())) {
                    filteredUidList.add(uid);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}