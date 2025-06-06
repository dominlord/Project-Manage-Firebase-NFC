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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HienThiDataBaseAdminActivity extends AppCompatActivity {

    private ListView listViewAcceptedEmails; // ListView hiển thị danh sách email
    private FirebaseFirestore db; // Firestore Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hien_thi_data_base_admin);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        db = FirebaseFirestore.getInstance();

        listViewAcceptedEmails = findViewById(R.id.listViewAcceptedUids); // dùng lại ID cũ
        ImageView BackButton10 = findViewById(R.id.backBtn);

        BackButton10.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Lắng nghe thay đổi từ collection "acceptedUids"
        db.collection("acceptedEmails").addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Lỗi khi tải danh sách email", e);
                return;
            }

            List<String> acceptedEmails = new ArrayList<>();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                String email = doc.getString("email"); // đổi từ "uid" sang "email"
                if (email != null) {
                    acceptedEmails.add(email);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, acceptedEmails);
            listViewAcceptedEmails.setAdapter(adapter);
        });

        // Xử lý khi chọn một email trong danh sách
        listViewAcceptedEmails.setOnItemClickListener((adapterView, view, position, id) -> {
            String selectedEmail = (String) adapterView.getItemAtPosition(position);

            if (selectedEmail != null) {
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra("email", selectedEmail); // truyền email thay vì uid
                startActivity(intent);
            } else {
                Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
