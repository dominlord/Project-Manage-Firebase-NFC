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
import android.widget.Button;
import android.widget.ImageView;
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

public class DataBaseAdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base_admin);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // Tham chiếu các view
        ListView listViewRequests = findViewById(R.id.listViewRequests);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        LinearLayout emptyView = findViewById(R.id.emptyView);
        RelativeLayout button, NFCbutton;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        button = findViewById(R.id.button);
        NFCbutton = findViewById(R.id.NFCbutton);

        NFCbutton.setOnClickListener(view -> {
            Intent intent50 = new Intent(DataBaseAdminActivity.this, NFCActivity.class);
            startActivity(intent50);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        button.setOnClickListener(view -> {
            Intent intent = new Intent(DataBaseAdminActivity.this, HienThiDataBaseAdminActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Danh sách hiển thị email và lưu ID tương ứng
        List<String> requests = new ArrayList<>();
        List<String> documentIds = new ArrayList<>();

        // Hiển thị danh sách yêu cầu từ Firestore
        db.collection("requests").whereEqualTo("status", "pending")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Lỗi khi tải yêu cầu", e);
                        return;
                    }

                    requests.clear();
                    documentIds.clear();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String email = doc.getString("email");
                        if (email != null) {
                            requests.add(email);
                            documentIds.add(doc.getId());
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

        // Xử lý chọn yêu cầu
        listViewRequests.setOnItemClickListener((adapterView, view, position, id) -> {
            String selectedEmail = (String) adapterView.getItemAtPosition(position);
            String documentId = documentIds.get(position);

            new AlertDialog.Builder(this)
                    .setTitle("Phản hồi yêu cầu")
                    .setMessage("Bạn có muốn chấp nhận yêu cầu từ Email " + selectedEmail + "?")
                    .setPositiveButton("Chấp nhận", (dialog, which) -> {
                        db.collection("requests").document(documentId)
                                .update("status", "accepted")
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Thông tin đã được chấp nhận.", Toast.LENGTH_SHORT).show();
                                    db.collection("acceptedEmails").document(selectedEmail)
                                            .set(Collections.singletonMap("email", selectedEmail));
                                })
                                .addOnFailureListener(err -> Log.e("Firestore", "Lỗi khi chấp nhận yêu cầu", err));
                    })
                    .setNegativeButton("Từ chối", (dialog, which) -> {
                        db.collection("requests").document(documentId)
                                .delete()
                                .addOnSuccessListener(unused -> Toast.makeText(this, "Thông tin không đủ yêu cầu để chấp nhận.", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(err -> Log.e("Firestore", "Lỗi khi từ chối yêu cầu", err));
                    })
                    .show();
        });

        // Xử lý điều hướng
        bottomNavigationView.setSelectedItemId(R.id.bottom_database);
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
                return true;
            } else if (itemId == R.id.bottom_helper) {
                startActivity(new Intent(getApplicationContext(), HelperAdminActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });
    }
}
