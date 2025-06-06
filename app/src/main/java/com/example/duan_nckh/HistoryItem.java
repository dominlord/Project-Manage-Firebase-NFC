package com.example.duan_nckh;

import com.google.firebase.Timestamp;

public class HistoryItem {
    private String field;
    private String oldValue;
    private String newValue;
    private Timestamp timestamp;
    private String source;

    // Constructor
    public HistoryItem(String field, String oldValue, String newValue, Timestamp timestamp, String source) {
        this.field = field;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.timestamp = timestamp;
        this.source = source;
    }

    // Getters
    public String getField() {
        return field;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getSource() {
        return source;
    }
}
