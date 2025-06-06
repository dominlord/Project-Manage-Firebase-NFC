package com.example.duan_nckh;

import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HienThiPhanHoiAdminActivity extends AppCompatActivity {

    private ListView listViewAcceptedUids;
    private FirebaseFirestore db;
    private Map<String, String> uidPhanhoiMap = new HashMap<>();
    private List<String> phanhoiList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hien_thi_phan_hoi_admin);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        db = FirebaseFirestore.getInstance();

        listViewAcceptedUids = findViewById(R.id.listViewAcceptedUids1);
        ImageView BackButton1001 = findViewById(R.id.backBtn);

        BackButton1001.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });


        db.collection("acceptedEmailReport").addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Lỗi khi tải danh sách UID", e);
                return;
            }

            List<String> acceptedUids = new ArrayList<>();
            phanhoiList.clear();
            uidPhanhoiMap.clear();

            for (QueryDocumentSnapshot doc : querySnapshot) {
                String email = doc.getString("email");
                String phanhoi = doc.getString("phanhoi");

                if (email != null) {
                    acceptedUids.add(email);
                    uidPhanhoiMap.put(email, phanhoi);
                    phanhoiList.add(phanhoi != null ? phanhoi : "Không có phản hồi.");
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, acceptedUids);
            listViewAcceptedUids.setAdapter(adapter);
        });

        listViewAcceptedUids.setOnItemClickListener((adapterView, view, position, id) -> {
            String selectedEmail = (String) adapterView.getItemAtPosition(position);

            db.collection("requestPhanHoi").document(selectedEmail).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists() && documentSnapshot.contains("phanhoi")) {
                            String phanhoi = documentSnapshot.getString("phanhoi");
                            Intent intent = new Intent(this, DetailPhanHoiActivity.class);
                            intent.putExtra("email", selectedEmail);
                            intent.putExtra("phanhoi", phanhoi);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Không tìm thấy phản hồi!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi lấy phản hồi", e));
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
