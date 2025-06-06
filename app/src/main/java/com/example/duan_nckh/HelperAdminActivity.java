package com.example.duan_nckh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HelperAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_admin);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        ListView listViewRequests = findViewById(R.id.listViewRequests1);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        LinearLayout emptyView = findViewById(R.id.emptyView1);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RelativeLayout xemPhanhoiBtn = findViewById(R.id.xemPhanhoiBtn);
        RelativeLayout PhanhoiUserBtn = findViewById(R.id.PhanhoiUserBtn);

        xemPhanhoiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HelperAdminActivity.this,HienThiPhanHoiAdminActivity.class);
                startActivity(intent);
            }
        });

        PhanhoiUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(HelperAdminActivity.this,PhanHoiDenUserActivity.class);
                startActivity(intent2);
            }
        });

        // Lấy danh sách yêu cầu từ "requestPhanHoi"
        db.collection("requestPhanHoi").whereEqualTo("status", "pending")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Lỗi khi tải yêu cầu", e);
                        return;
                    }

                    List<String> requests = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String email = doc.getString("email");
                        String phanhoi = doc.getString("phanhoi"); // Lấy phản hồi từ Firestore
                        if (email != null && phanhoi != null) {
                            requests.add("Email: " + email);
                        }
                    }

                    if (requests.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                        listViewRequests.setVisibility(View.GONE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        listViewRequests.setVisibility(View.VISIBLE);

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, requests);
                        listViewRequests.setAdapter(adapter);
                    }
                });

        // Xử lý khi chọn yêu cầu từ danh sách
        listViewRequests.setOnItemClickListener((adapterView, view, position, id) -> {
            String selectedEmail = ((String) adapterView.getItemAtPosition(position)).replace("Email: ", "").trim();


            new AlertDialog.Builder(this)
                    .setTitle("Phản hồi yêu cầu")
                    .setMessage("Bạn có muốn chấp nhận yêu cầu từ Email " + selectedEmail + "?")
                    .setPositiveButton("Chấp nhận", (dialog, which) -> {
                        // Cập nhật trạng thái "accepted"
                        db.collection("requestPhanHoi").document(selectedEmail)
                                .update("status", "accepted")
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Thông tin đã được chấp nhận.", Toast.LENGTH_SHORT).show();
                                    db.collection("acceptedEmailReport").document(selectedEmail)
                                            .set(Collections.singletonMap("email", selectedEmail));
                                })
                                .addOnFailureListener(err -> Log.e("Firestore", "Lỗi khi chấp nhận yêu cầu", err));
                    })
                    .setNegativeButton("Từ chối", (dialog, which) -> {
                        db.collection("requestPhanHoi").document(selectedEmail)
                                .delete()
                                .addOnSuccessListener(unused -> Toast.makeText(this, "Thông tin đã bị từ chối.", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(err -> Log.e("Firestore", "Lỗi khi từ chối yêu cầu", err));
                    })
                    .show();
        });

        // Xử lý bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.bottom_helper);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home2) {
                startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
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
                return true;
            }
            return false;
        });
    }
}
