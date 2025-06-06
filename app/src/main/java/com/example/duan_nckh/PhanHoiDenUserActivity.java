package com.example.duan_nckh;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhanHoiDenUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phan_hoi_den_user);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        ImageView troveBtn1 = findViewById(R.id.troveBtn100);
        EditText phanhoiAdmin = findViewById(R.id.phanhoiAdmin);
        Button sendButton = findViewById(R.id.SendBtn);

        FirebaseFirestore db ;
        List<String> emailList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, emailList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.emailSpinner);
        spinner.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        db.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                // Lấy đối tượng con "ca_nhan"
                Map<String, Object> caNhan = (Map<String, Object>) document.get("ca_nhan");
                if (caNhan != null && caNhan.get("email") != null) {
                    String email = caNhan.get("email").toString();
                    emailList.add(email);
                }
            }
            adapter.notifyDataSetChanged(); // cập nhật Spinner
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi khi lấy email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });


        sendButton.setOnClickListener(view -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String selectedEmail = spinner.getSelectedItem().toString();
                String phanhoi = phanhoiAdmin.getText().toString().trim();

                if (phanhoi.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập phản hồi trước khi gửi!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> request = new HashMap<>();
                request.put("from", currentUser.getEmail()); // Người gửi
                request.put("to", selectedEmail); // Người nhận
                request.put("phanhoi", phanhoi);
                request.put("status", "pending");
                request.put("timestamp", FieldValue.serverTimestamp());

                db.collection("phanhoiToUser").add(request)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Phản hồi đã gửi đến " + selectedEmail, Toast.LENGTH_SHORT).show();
                            phanhoiAdmin.setText("");
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi gửi phản hồi!", Toast.LENGTH_SHORT).show());
            }
        });



        troveBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}