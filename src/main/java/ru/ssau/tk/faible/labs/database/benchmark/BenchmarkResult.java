package ru.ssau.tk.faible.labs.database.benchmark;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BenchmarkResult {
    private long duration;
    private String queryName;
    private int records_count;
}
