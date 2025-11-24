package ru.ssau.tk.faible.labs.database.benchmark;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Getter
public class BenchmarkResult {
    private long duration;
    private String queryName;
    private int records_count;
}
