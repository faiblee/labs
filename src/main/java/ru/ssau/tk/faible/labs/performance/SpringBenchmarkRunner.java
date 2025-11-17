package ru.ssau.tk.faible.labs.performance;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.ssau.tk.faible.labs.config.DatabaseConfig;

public class SpringBenchmarkRunner {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(
                        DatabaseConfig.class,
                        BenchmarkService.class,
                        ExcelWriter.class
                );

        BenchmarkService benchmarkService = context.getBean(BenchmarkService.class);
        benchmarkService.runPerformanceBenchmark();

        context.close();
    }
}