package com.example.duan_nckh;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DiemDanhActivity extends AppCompatActivity {

    Spinner spinnerLop;
    ListView listViewMSV;
    RelativeLayout btnScanQR, btnGhiDanhSach;
    FirebaseFirestore db;
    FirebaseAuth auth;
    private ImageView troveBtn ;

    ArrayList<String> lopList = new ArrayList<>();
    ArrayList<String> msvList = new ArrayList<>();
    ArrayAdapter<String> lopAdapter;
    ArrayAdapter<String> msvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diem_danh);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // Khởi tạo UI và Firebase
        spinnerLop   = findViewById(R.id.spinnerLop);
        listViewMSV  = findViewById(R.id.listViewMSV);
        troveBtn = findViewById(R.id.backBtn);
        btnScanQR    = findViewById(R.id.btnScanQR);
        db           = FirebaseFirestore.getInstance();
        auth         = FirebaseAuth.getInstance();

        CollectionReference usersRef = db.collection("users");

        troveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // 1) Ghi danh sách ra collection mới
        btnGhiDanhSach = findViewById(R.id.btnGhiDanhSach);
        btnGhiDanhSach.setOnClickListener(v -> {
            String selectedLop = spinnerLop.getSelectedItem().toString();
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(new Date());
            DocumentReference docRef = db.collection("danh_sach_diem_danh")
                    .document(currentDate);

            // 1) Lấy trạng thái checked từ UI
            SparseBooleanArray checked = listViewMSV.getCheckedItemPositions();

            // 2) Đọc document hôm nay (nếu có) để merge
            docRef.get().addOnSuccessListener(docSnap -> {
                        List<Map<String, Object>> danhSach;
                        if (docSnap.exists() && docSnap.get("danh_sach") != null) {
                            danhSach = (List<Map<String, Object>>) docSnap.get("danh_sach");
                        } else {
                            danhSach = new ArrayList<>();
                        }

                        // 3) Mở map để dễ cập nhật theo MSV
                        Map<String, Map<String, Object>> mapByMsv = new HashMap<>();
                        for (Map<String, Object> item : danhSach) {
                            mapByMsv.put((String)item.get("msv"), item);
                        }

                        // 4) Duyệt toàn bộ MSV trên UI
                        for (int i = 0; i < msvList.size(); i++) {
                            String msv = msvList.get(i);
                            boolean isChecked = checked.get(i, false);

                            if (mapByMsv.containsKey(msv)) {
                                // Cập nhật checkin
                                mapByMsv.get(msv).put("checkin", isChecked);
                            } else {
                                // Thêm mới
                                Map<String, Object> newItem = new HashMap<>();
                                newItem.put("msv", msv);
                                newItem.put("lop", selectedLop);
                                newItem.put("checkin", isChecked);
                                mapByMsv.put(msv, newItem);
                            }
                        }

                        // 5) Xây lại danh_sach
                        List<Map<String, Object>> finalList = new ArrayList<>(mapByMsv.values());

                        // 6) Chuẩn bị data
                        Map<String, Object> data = new HashMap<>();
                        data.put("lop", selectedLop);
                        data.put("ngay", currentDate);
                        data.put("danh_sach", finalList);

                        // 7) Ghi đè document (tạo mới nếu chưa có)
                        docRef.set(data)
                                .addOnSuccessListener(aVoid -> {
                                    String msg = docSnap.exists()
                                            ? "Đã cập nhật danh sách điểm danh!"
                                            : "Đã tạo danh sách điểm danh!";
                                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this,
                                                "Lỗi khi ghi danh sách: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show()
                                );
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this,
                                    "Không thể đọc danh sách cũ: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show()
                    );
        });






        // 2) Lấy danh sách lớp
        usersRef.get().addOnSuccessListener(qds -> {
            HashSet<String> lopSet = new HashSet<>();
            for (DocumentSnapshot doc : qds) {
                Object caNhanObj = doc.get("ca_nhan");
                if (caNhanObj instanceof Map) {
                    Map<String, Object> caNhan = (Map<String, Object>) caNhanObj;
                    String lop = (String) caNhan.get("lop");
                    if (lop != null) lopSet.add(lop);
                }
            }
            lopList.addAll(lopSet);
            lopAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lopList);
            lopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLop.setAdapter(lopAdapter);
        });

        // 3) Khi chọn lớp, load MSV
        spinnerLop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selectedLop = lopList.get(pos);
                msvList.clear();
                usersRef.get().addOnSuccessListener(qds -> {
                    for (DocumentSnapshot doc : qds) {
                        Object caNhanObj = doc.get("ca_nhan");
                        if (caNhanObj instanceof Map) {
                            Map<String, Object> caNhan = (Map<String, Object>) caNhanObj;
                            String lop = (String) caNhan.get("lop");
                            String msv = (String) caNhan.get("msv");
                            if (lop != null && lop.equals(selectedLop) && msv != null) {
                                msvList.add(msv);
                            }
                        }
                    }
                    msvAdapter = new ArrayAdapter<>(DiemDanhActivity.this,
                            android.R.layout.simple_list_item_multiple_choice,
                            msvList);
                    listViewMSV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    listViewMSV.setAdapter(msvAdapter);
                });
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 4) Quét QR thủ công (nếu cần)
        btnScanQR.setOnClickListener(v -> {
            ScanOptions opts = new ScanOptions();
            opts.setPrompt("Quét mã QR của sinh viên");
            opts.setBeepEnabled(true);
            opts.setOrientationLocked(true);
            opts.setCaptureActivity(PortraitCaptureActivity.class);
            barcodeLauncher.launch(opts);
        });

        // 5) Lắng nghe realtime các lượt quét camera Wi‑Fi đã đẩy vào Firestore
        String currentUserEmail = auth.getCurrentUser() != null
                ? auth.getCurrentUser().getEmail()
                : null;

        if (currentUserEmail != null) {
            db.collection("danh_sach_diem_danh")
                    .whereEqualTo("identifier", currentUserEmail)
                    .addSnapshotListener((snaps, err) -> {
                        if (err != null) {
                            Log.w("DiemDanh", "Listen error", err);
                            return;
                        }
                        for (DocumentChange dc : snaps.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                String time   = dc.getDocument().getString("time");
                                Toast.makeText(this,
                                        "Tự động điểm danh lúc " + time,
                                        Toast.LENGTH_SHORT).show();
                                // TODO: nếu muốn đánh dấu ListView, tìm index MSV tương ứng rồi setItemChecked()
                            }
                        }
                    });
        }
    }

    // Xử lý kết quả quét QR trong app
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() == null) return;
                String scannedEmail = result.getContents();
                String selectedLop  = spinnerLop.getSelectedItem().toString();

                CollectionReference usersRef = db.collection("users");
                usersRef.get().addOnSuccessListener(qds -> {
                    boolean found = false;
                    for (DocumentSnapshot doc : qds) {
                        Object caNhanObj = doc.get("ca_nhan");
                        if (caNhanObj instanceof Map) {
                            Map<String, Object> caNhan = (Map<String, Object>) caNhanObj;
                            String lop   = (String) caNhan.get("lop");
                            String email = (String) caNhan.get("email");
                            if (!doc.contains("checkin")) {
                                doc.getReference().update("checkin", "fail");
                            }
                            if (email != null
                                    && email.equalsIgnoreCase(scannedEmail)
                                    && lop != null
                                    && lop.equals(selectedLop)) {

                                doc.getReference().update("checkin", true);

                                String msv = (String) caNhan.get("msv");
                                int idx = msvList.indexOf(msv);
                                if (idx != -1) listViewMSV.setItemChecked(idx, true);

                                Toast.makeText(this,
                                        "Điểm danh thành công: " + email,
                                        Toast.LENGTH_SHORT).show();
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        Toast.makeText(this,
                                "Email " + scannedEmail + " không thuộc lớp!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            });

}
