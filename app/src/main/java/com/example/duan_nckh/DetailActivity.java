package com.example.duan_nckh;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private String msv; // Biến lưu trữ msv
    private NfcAdapter nfcAdapter;
    private TextView tvResult;
    private Button btnWrite;
    private boolean isNfcSupported = true;
    private String dataToWrite = ""; // Dữ liệu sẽ lấy từ Firestore
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        TextView hoVaTenTextView = findViewById(R.id.textViewHoVaTen1);
        TextView sdtTextView = findViewById(R.id.textViewSDT);
        TextView diaChiTextView = findViewById(R.id.textViewDiaChi);
        TextView khoaTextView = findViewById(R.id.textViewKhoa);
        TextView lopTextView = findViewById(R.id.textViewLop);
        TextView msvTextView = findViewById(R.id.textViewMSV);
        TextView nganhTextView = findViewById(R.id.textViewNganh);

        TextView chungChiTextView = findViewById(R.id.textViewChungChi);
        TextView kyNangTextView = findViewById(R.id.textViewKyNang);
        TextView kinhNghiemTextView = findViewById(R.id.textViewKinhNghiem);
        TextView lienHeTextView = findViewById(R.id.textViewLienHe);
        TextView IDBHYTTextView = findViewById(R.id.textViewBHYT2);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        tvResult = findViewById(R.id.tvResult);
        btnWrite = findViewById(R.id.btnWrite);
        db = FirebaseFirestore.getInstance();

        // Nhận UID từ Intent
        String Email = getIntent().getStringExtra("email");
        String emailKey = Email.replace(".", "_");

        Button btnDownloadQR = findViewById(R.id.btnDownloadQR);
        ImageView BackButton21 = findViewById(R.id.backBtn);

        BackButton21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        if (nfcAdapter == null) {
            isNfcSupported = false;
            disableNfcFeatures();
        }

        btnWrite.setOnClickListener(v -> {
            if (!isNfcSupported) {
                Toast.makeText(this, "Thiết bị không hỗ trợ NFC!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tải dữ liệu từ Firestore và chỉ cho phép ghi sau khi hoàn tất
            fetchDataFromFirestore(emailKey);
            if (dataToWrite.isEmpty() || dataToWrite.equals("Không tìm thấy thông tin người dùng.")) {
                Toast.makeText(this, "Đang tải dữ liệu... Hãy thử lại!", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Hãy chạm thẻ NFC để ghi dữ liệu!", Toast.LENGTH_SHORT).show();
        });


        // Tải dữ liệu từ Firestore
        if (emailKey != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(emailKey).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Map<String, Object> caNhan = (Map<String, Object>) documentSnapshot.get("ca_nhan");
                        Map<String, Object> tongQuan = (Map<String, Object>) documentSnapshot.get("tong_quan");

                        if (caNhan != null) {
                            hoVaTenTextView.setText(String.valueOf(caNhan.get("ten")));
                            sdtTextView.setText(String.valueOf(caNhan.get("sdt")));
                            diaChiTextView.setText(String.valueOf(caNhan.get("diachi")));
                            khoaTextView.setText(String.valueOf(caNhan.get("khoa")));
                            lopTextView.setText(String.valueOf(caNhan.get("lop")));
                            nganhTextView.setText(String.valueOf(caNhan.get("nganh")));

                            // Lấy msv từ Firestore
                            msv = String.valueOf(caNhan.get("msv"));
                            msvTextView.setText(msv != null ? msv : "Không có dữ liệu");
                        } else {
                            Toast.makeText(this, "Không tìm thấy dữ liệu cá nhân!", Toast.LENGTH_SHORT).show();
                        }

                        // Cập nhật dữ liệu cho Thông tin tổng quan
                        if (tongQuan != null) {
                            chungChiTextView.setText(String.valueOf(tongQuan.get("chungchi")));
                            kyNangTextView.setText(String.valueOf(tongQuan.get("kinang")));
                            kinhNghiemTextView.setText(String.valueOf(tongQuan.get("kinhnghiem")));
                            lienHeTextView.setText(String.valueOf(tongQuan.get("lienhe")));
                            IDBHYTTextView.setText(String.valueOf(tongQuan.get("BHYT")));
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show());
        }

        // Sự kiện nhấn nút Download QR
        btnDownloadQR.setOnClickListener(view -> {
            if (Email!= null && !Email.isEmpty()) {
                generateAndDownloadQR(Email);
            } else {
                Toast.makeText(this, "Không có MSV để tạo mã QR!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateAndDownloadQR(String msv) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            // Tạo mã QR bitmap
            Bitmap bitmap = toBitmap(qrCodeWriter.encode(msv, BarcodeFormat.QR_CODE, 512, 512));

            // Lưu ảnh vào bộ nhớ thiết bị
            saveQRCodeToDevice(bitmap, msv);
        } catch (WriterException e) {
            Toast.makeText(this, "Lỗi tạo QR!", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap toBitmap(com.google.zxing.common.BitMatrix bitMatrix) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
            }
        }
        return bitmap;
    }

    private void saveQRCodeToDevice(Bitmap bitmap, String fileName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Lưu vào MediaStore cho Android 10+
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName + ".png");
            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QR_Codes");

            ContentResolver resolver = getContentResolver();
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                Toast.makeText(this, "Đã lưu mã QR vào thư viện ảnh!", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi khi lưu mã QR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Lưu vào bộ nhớ ngoài cho Android 9 trở xuống
            File directory = new File(Environment.getExternalStorageDirectory(), "Pictures/QR_Codes");
            if (!directory.exists() && !directory.mkdirs()) {
                Toast.makeText(this, "Không thể tạo thư mục để lưu QR Code!", Toast.LENGTH_SHORT).show();
                return;
            }

            File file = new File(directory, fileName + ".png");
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                Toast.makeText(this, "Đã lưu mã QR: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi khi lưu mã QR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchDataFromFirestore(String emailKey) {
        db.collection("users").document(emailKey)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String DataKey = documentSnapshot.getString("tong_quan.key");

                        // Chuỗi để ghi vào thẻ NFC
                        dataToWrite =  DataKey;
                    } else {
                        dataToWrite = "Không tìm thấy thông tin người dùng.";
                    }
                })
                .addOnFailureListener(e -> {
                    dataToWrite = "Lỗi khi lấy dữ liệu: " + e.getMessage();
                    e.printStackTrace();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNfcSupported) {
            Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
            IntentFilter[] intentFilters = new IntentFilter[]{};
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isNfcSupported) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (!isNfcSupported) {
            tvResult.setText("Thiết bị không hỗ trợ NFC!");
            return;
        }

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if (tag != null) {
            // Tạo bản ghi NDEF với nội dung từ Firestore
            NdefRecord recordToWrite = NdefRecord.createTextRecord("en", dataToWrite);
            writeNfcTag(tag, recordToWrite);
        }
    }

    private void writeNfcTag(Tag tag, NdefRecord newRecord) {
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    showToastAndLog("Thẻ NFC không cho phép ghi dữ liệu!", "Thẻ chỉ đọc, không thể ghi.");
                    ndef.close();
                    return;
                }

                NdefMessage existingMessage = ndef.getNdefMessage();
                List<NdefRecord> updatedRecords = new ArrayList<>();

                // Lấy dữ liệu hiện có hoặc khởi tạo danh sách mới
                if (existingMessage != null) {
                    updatedRecords.addAll(Arrays.asList(existingMessage.getRecords()));
                }

                // Thêm bản ghi mới
                updatedRecords.add(newRecord);

                // Kiểm tra kích thước dữ liệu
                NdefMessage newMessage = new NdefMessage(updatedRecords.toArray(new NdefRecord[0]));
                if (ndef.getMaxSize() < newMessage.toByteArray().length) {
                    showToastAndLog("Dữ liệu quá lớn để ghi vào thẻ!", "Dung lượng thẻ không đủ.");
                    ndef.close();
                    return;
                }

                // Ghi dữ liệu
                ndef.writeNdefMessage(newMessage);
                showToastAndLog("Ghi dữ liệu thành công!", "Dữ liệu đã được ghi vào thẻ.");
                ndef.close();
            } else {
                showToastAndLog("Thẻ không hỗ trợ NDEF!", "Thẻ không hỗ trợ NDEF.");
            }
        } catch (Exception e) {
            showToastAndLog("Lỗi khi ghi dữ liệu!", e.getMessage());
            e.printStackTrace();
        }
    }

    private void showToastAndLog(String toastMessage, String logMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
        tvResult.setText(logMessage);
    }

    private void disableNfcFeatures() {
        isNfcSupported = false;
        btnWrite.setEnabled(false);
        btnWrite.setText("NFC không khả dụng");
        Toast.makeText(DetailActivity.this, "Thiết bị hiện tại không hỗ trợ khả năng quét thẻ NFC\nMột số chức năng về sử dụng NFC sẽ bị khóa!", Toast.LENGTH_LONG).show();
    }


}