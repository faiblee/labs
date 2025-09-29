package ru.ssau.tk.faible.labs.functions;

public class SimpleIteration {
    private double precision = 1e-6; // точность
    private int maxIterations = 1000; // максимальное количество итераций
    private double initialApproximation = 0.0; // начальное приближение
    private final MathFunction phi;

    public SimpleIteration(MathFunction func) { // конструктор с одним параметром - функцией
        if (func == null) {
            throw new IllegalArgumentException("The iteration function cannot be null");
        }
        phi = func;
    }

    // Конструктор с точностью и максимальным количеством итераций
    public SimpleIteration(MathFunction func, double prec, int maxIter, double initialApprox) {
        if (func == null) { // если функция - null
            throw new IllegalArgumentException("The iteration function cannot be null");
        }
        if (prec <= 0) { // если точность отрицательная
            throw new IllegalArgumentException("Precision must be positive");
        }
        if (Double.isNaN(prec) || Double.isInfinite(prec)) { // если точность - NaN или бесконечность
            throw new IllegalArgumentException(("Precision cannot be NaN or infinite"));
        }
        if (Double.isNaN(initialApprox) || Double.isInfinite(initialApprox)) { // если точность - NaN или бесконечность
            throw new IllegalArgumentException(("Initial approximation cannot be NaN or infinite"));
        }
        if (maxIter <= 0 || maxIter >= 10_000_000) { // если максимальное число итераций отрицательное или очень большое
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

        throw new ArithmeticException("The method did not converge within the selected number of iterations");
    }

}
