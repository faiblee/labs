package ru.ssau.tk.faible.labs.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleIteration {
    private static final Logger log = LoggerFactory.getLogger(SimpleIteration.class);

    private double precision = 1e-6; // точность
    private int maxIterations = 1000; // максимальное количество итераций
    private double initialApproximation = 0.0; // начальное приближение
    private final MathFunction phi;

    public SimpleIteration(MathFunction func) { // конструктор с одним параметром - функцией
        if (func == null) {
            log.error("В конструктор SimpleIteration передана null функция");
            throw new IllegalArgumentException("The iteration function cannot be null");
        }
        phi = func;
    }

    // Конструктор с точностью и максимальным количеством итераций
    public SimpleIteration(MathFunction func, double prec, int maxIter, double initialApprox) {
        if (func == null) { // если функция - null
            log.error("В конструктор передана null функция");
            throw new IllegalArgumentException("The iteration function cannot be null");
        }
        if (prec <= 0) { // если точность отрицательная
            log.error("В конструктор передана отрицательная точность");
            throw new IllegalArgumentException("Precision must be positive");
        }
        if (Double.isNaN(prec) || Double.isInfinite(prec)) { // если точность - NaN или бесконечность
            log.error("В конструктор передана точность NaN или бесконечность");
            throw new IllegalArgumentException(("Precision cannot be NaN or infinite"));
        }
        if (Double.isNaN(initialApprox) || Double.isInfinite(initialApprox)) { // если начальное приближение - NaN или бесконечность
            log.error("В конструктор передано начальное приближение NaN или бесконечность");
            throw new IllegalArgumentException(("Initial approximation cannot be NaN or infinite"));
        }
        if (maxIter <= 0 || maxIter >= 10_000_000) { // если максимальное число итераций отрицательное или очень большое
            log.error("В конструктор передано отрицательное или очень большое максимальное число итераций ");
            throw new IllegalArgumentException("The number of iterations must be positive and < 10_000_000");
        }
        phi = func;
        initialApproximation = initialApprox;
        precision = prec;
        maxIterations = maxIter;
    }

    public double solve() {
        double x0 = initialApproximation;
        double x1;
        int iteration = 0;

        while (iteration <= maxIterations) {
            x1 = phi.apply(x0);

            if (Math.abs(x1 - x0) < precision) {
                return x1;
            }

            x0 = x1;
            iteration++;
        }

        log.error("Метод не сошелся за выбранное число итераций");
        throw new ArithmeticException("The method did not converge within the selected number of iterations");
    }

}
