package ru.ssau.tk.faible.labs.performance;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import ru.ssau.tk.faible.labs.Application;

public class SpringBenchmarkRunner {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder()
                .sources(Application.class)
                .web(WebApplicationType.NONE)
                .run();

        BenchmarkService benchmarkService = context.getBean(BenchmarkService.class);
        benchmarkService.runPerformanceBenchmark();

        context.close();
    }
}