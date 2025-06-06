package com.example.duan_nckh;

import android.icu.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

public class PhanHoi {
    public String from;
    public String noidung;
    public Date timestamp;

    public PhanHoi(String from, String noidung, Date timestamp) {
        this.from = from;
        this.noidung = noidung;
        this.timestamp = timestamp;
    }

    // để hiển thị trong ListView, ta sẽ override toString()
    @Override
    public String toString() {
        // chỉ trả về: "Từ: [from] — [dd/MM/yyyy HH:mm]"
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return "Từ: " + from + " — " + fmt.format(timestamp);
    }
}
