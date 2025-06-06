package com.example.duan_nckh;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class LichSuThayDoiUserActivity extends AppCompatActivity {

    private ListView listView;
    private SearchView searchView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> emailList, filteredList;
    private FirebaseFirestore db;
    private HashMap<String, String> emailToUidMap; // Lưu email -> UID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lich_su_thay_doi_user);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        listView = findViewById(R.id.uidListView1);
        searchView = findViewById(R.id.uidSearchView);
        emailList = new ArrayList<>();
        filteredList = new ArrayList<>();
        emailToUidMap = new HashMap<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredList);
        listView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        ImageView backbutton = findViewById(R.id.backBtn);


        //change color for search view
        int hintTextid = searchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchView.findViewById(hintTextid);
        textView.setHintTextColor(ContextCompat.getColor(this, R.color.white));
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));

        backbutton.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Lấy danh sách từ Firestore
        db.collection("users").get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot userDoc : querySnapshot.getDocuments()) {
                        String uid = userDoc.getId();
                        String email = userDoc.getString("email");

                        if (email != null && !email.isEmpty()) {
                            emailToUidMap.put(email, uid);
                            emailList.add(email); // Hiển thị email
                        } else {
                            emailToUidMap.put(uid, uid);
                            emailList.add(uid); // Nếu không có email, hiển thị UID
                        }
                    }
                    filteredList.addAll(emailList);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi lấy danh sách người dùng!", e));

        // Xử lý tìm kiếm theo email hoặc UID
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterResults(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterResults(newText);
                return true;
            }
        });

        // Xử lý chọn mục trong danh sách
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = filteredList.get(position);
            String selectedUid = emailToUidMap.getOrDefault(selectedItem, selectedItem); // Nếu là email, lấy UID

            Intent intent = new Intent(LichSuThayDoiUserActivity.this, HienThiDanhSachLichSuUserActivity.class);
            intent.putExtra("UID", selectedUid);
            startActivity(intent);
        });
    }

    // Phương thức tìm kiếm theo cả email và UID
    private void filterResults(String keyword) {
        filteredList.clear();
        if (keyword.isEmpty()) {
            filteredList.addAll(emailList);
        } else {
            for (String item : emailList) {
                if (item.toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
