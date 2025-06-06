package com.example.duan_nckh;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingDiemActivity extends AppCompatActivity {

    private Spinner spinnerEmail;
    private EditText diemRenLuyen, diemLichSu, diemToan, hanhKiem;
    private RelativeLayout btnLuu ;
    ImageView btnBack;


    private ArrayList<String> emailList = new ArrayList<>();
    private ArrayAdapter<String> emailAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_diem);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }


        spinnerEmail = findViewById(R.id.spinnerEmail);
        diemRenLuyen = findViewById(R.id.diemRenLuyen);
        diemLichSu = findViewById(R.id.diemLichSu);
        diemToan = findViewById(R.id.diemToan);
        hanhKiem = findViewById(R.id.hanhKiem);
        btnBack = findViewById(R.id.backBtn);
        btnLuu = findViewById(R.id.btnLuu);

        emailAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, emailList);
        emailAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEmail.setAdapter(emailAdapter);

        spinnerEmail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedEmail = emailList.get(position);

                db.collection("users")
                        .whereEqualTo("ca_nhan.email", selectedEmail)
                        .get()
                        .addOnSuccessListener(query -> {
                            if (!query.isEmpty()) {
                                DocumentSnapshot doc = query.getDocuments().get(0);
                                Map<String, Object> diemHocTap = (Map<String, Object>) doc.get("diem_hoc_tap");
                                if (diemHocTap != null) {
                                    diemRenLuyen.setText(String.valueOf(diemHocTap.get("diemRenLuyen")));
                                    diemLichSu.setText(String.valueOf(diemHocTap.get("diemLichSu")));
                                    diemToan.setText(String.valueOf(diemHocTap.get("diemToan")));
                                    hanhKiem.setText(String.valueOf(diemHocTap.get("hanhKiem")));
                                } else {
                                    diemRenLuyen.setText("");
                                    diemLichSu.setText("");
                                    diemToan.setText("");
                                    hanhKiem.setText("");
                                }
                            }
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(SettingDiemActivity.this, "Không tải được dữ liệu điểm!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Có thể để trống
            }
        });



        // Load dữ liệu email từ Firestore
        loadEmails();


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });









        btnLuu.setOnClickListener(v -> {
            String selectedEmail = spinnerEmail.getSelectedItem().toString();
            String renLuyen = diemRenLuyen.getText().toString();
            String lichSu = diemLichSu.getText().toString();
            String toan = diemToan.getText().toString();
            String hanh = hanhKiem.getText().toString();


            Map<String, Object> diemHocTap = new HashMap<>();
            diemHocTap.put("diemRenLuyen", Double.parseDouble(renLuyen));
            diemHocTap.put("diemLichSu", Double.parseDouble(lichSu));
            diemHocTap.put("diemToan", Double.parseDouble(toan));
            diemHocTap.put("hanhKiem", hanh);

            db.collection("users")
                    .whereEqualTo("ca_nhan.email", selectedEmail)
                    .get()
                    .addOnSuccessListener(query -> {
                        for (DocumentSnapshot doc : query) {
                            db.collection("users")
                                    .document(doc.getId())
                                    .update("diem_hoc_tap", diemHocTap);
                        }
                        Toast.makeText(this, "Lưu điểm thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

    }

    private void loadEmails() {
        db.collection("users")
                .get()
                .addOnSuccessListener(query -> {
                    emailList.clear();
                    for (DocumentSnapshot doc : query) {
                        // Lấy field con ca_nhan.email
                        String email = doc.getString("ca_nhan.email");
                        if (email != null) {
                            emailList.add(email);
                        }
                    }
                    emailAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Không tải được email: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );

}
}
