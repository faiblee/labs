package ru.ssau.tk.faible.labs.performance;

public class BenchmarkResult {
    private long duration;
    private String queryName;
    private int recordsCount;

    public BenchmarkResult(long duration, String queryName, int recordsCount) {
        this.duration = duration;
        this.queryName = queryName;
        this.recordsCount = recordsCount;
    }

    public long getDuration() {
        return duration;
    }

    public String getQueryName() {
        return queryName;
    }

    public int getRecordsCount() {
        return recordsCount;
    }
}