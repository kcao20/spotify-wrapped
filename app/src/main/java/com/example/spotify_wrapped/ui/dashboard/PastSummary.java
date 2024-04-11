package com.example.spotify_wrapped.ui.dashboard;

public class PastSummary {
    private String date;
    private String time_span;

    public PastSummary(String date, String time_span) {
        this.date = date;
        this.time_span = time_span;
    }

    public String getDate() {
        return date;
    }

    public String getTimeSpan() {
        return time_span;
    }
}
