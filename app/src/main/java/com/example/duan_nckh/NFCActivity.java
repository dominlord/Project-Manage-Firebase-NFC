package com.example.duan_nckh;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;

public class NFCActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private TextView tvResult, tvNfcStatus;
    private RelativeLayout btnDelete , btnRead;
    private boolean isNfcSupported = true;
    private boolean isReadMode = false;
    private ImageView troveBtn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nfcactivity);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        tvResult = findViewById(R.id.tvResult1);
        troveBtn = findViewById(R.id.backBtn);
        tvNfcStatus = findViewById(R.id.tvNfcStatus1);
        btnDelete = findViewById(R.id.btnDelete);
        btnRead = findViewById(R.id.btnReadNFC);

        ViewFlipper viewFlipperPhanHoiNPT = findViewById(R.id.viewFlipperPhanHoiNPT);
        ViewFlipper viewFlipperTinhan = findViewById(R.id.viewFlipperTinhan);

        viewFlipperPhanHoiNPT.setInAnimation(this, android.R.anim.slide_in_left);
        viewFlipperTinhan.setInAnimation(this, android.R.anim.slide_in_left);

        viewFlipperPhanHoiNPT.showNext();
        viewFlipperTinhan.showNext();

        new Handler().postDelayed(() -> {
            viewFlipperPhanHoiNPT.stopFlipping();
            viewFlipperTinhan.stopFlipping();

        }, 3000);

        troveBtn.setOnClickListener(new View.OnClickListener() {
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

        btnRead.setOnClickListener(v -> {
            isReadMode = true;
            if (!isNfcSupported) {
                Toast.makeText(this, "Thiết bị không hỗ trợ NFC!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Hãy chạm thẻ NFC để đọc dữ liệu!", Toast.LENGTH_SHORT).show();
        });

        btnDelete.setOnClickListener(v -> {
            if (!isNfcSupported) {
                Toast.makeText(this, "Thiết bị không hỗ trợ NFC!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Hãy chạm thẻ NFC để xóa dữ liệu!", Toast.LENGTH_SHORT).show();
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
            if (isReadMode) {
                // Chế độ đọc NFC
                readNfcTag(tag);
            } else {
                // Chế độ xóa NFC
                NdefRecord emptyRecord = NdefRecord.createTextRecord("en", "");
                clearNfcTag(tag, emptyRecord);
            }
        }
    }

    private void readNfcTag(Tag tag) {
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                NdefMessage ndefMessage = ndef.getNdefMessage();

                if (ndefMessage != null) {
                    StringBuilder nfcData = new StringBuilder();
                    for (NdefRecord record : ndefMessage.getRecords()) {
                        if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN &&
                                Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                            String text = readTextFromNdefRecord(record);
                            nfcData.append(text).append("\n");
                        }
                    }

                    // Chuyển sang HienThiDuLieu_NFCActivity
                    Intent newActivityIntent = new Intent(this, HienThiDuLieu_NFCActivity.class);
                    newActivityIntent.putExtra("nfcData", nfcData.toString().trim());
                    startActivity(newActivityIntent);

                } else {
                    showToastAndLog("Thẻ không chứa dữ liệu!", "Thẻ rỗng.");
                }
                ndef.close();
            } else {
                showToastAndLog("Thẻ không hỗ trợ NDEF!", "Thẻ không hỗ trợ NDEF.");
            }
        } catch (Exception e) {
            showToastAndLog("Lỗi khi đọc thẻ!", e.getMessage());
            e.printStackTrace();
        }
    }



    private void clearNfcTag(Tag tag, NdefRecord emptyRecord) {
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    showToastAndLog("Thẻ NFC không cho phép ghi dữ liệu!", "Thẻ chỉ đọc, không thể xóa.");
                    ndef.close();
                    return;
                }

                NdefMessage emptyMessage = new NdefMessage(new NdefRecord[]{emptyRecord});
                ndef.writeNdefMessage(emptyMessage);
                showToastAndLog("Xóa dữ liệu thành công!", "Thẻ đã được xóa sạch.");
                ndef.close();
            } else {
                showToastAndLog("Thẻ không hỗ trợ NDEF!", "Thẻ không hỗ trợ NDEF.");
            }
        } catch (Exception e) {
            showToastAndLog("Lỗi khi xóa dữ liệu!", e.getMessage());
            e.printStackTrace();
        }
    }

    private String readTextFromNdefRecord(NdefRecord record) {
        try {
            byte[] payload = record.getPayload();
            // Bỏ qua byte đầu tiên (status byte) và mã ngôn ngữ
            int languageCodeLength = payload[0] & 0x3F; // Độ dài của mã ngôn ngữ
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1); // Đọc dữ liệu chính
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khi đọc thẻ NFC!";
        }
    }




    private void showToastAndLog(String toastMessage, String logMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
        tvResult.setText(logMessage);
    }

    private void disableNfcFeatures() {
        isNfcSupported = false;
        btnDelete.setEnabled(false);
        tvNfcStatus.setText("Thiết bị không hỗ trợ NFC. Một số tính năng bị vô hiệu hóa.");
        tvNfcStatus.setVisibility(View.VISIBLE);
    }
}