package ru.ssau.tk.faible.labs.functions;

public class SimpleIteration implements MathFunction {
    private double precision = 1e-6; // точность
    private int maxIterations = 1000; // максимальное количество итераций
    private final MathFunction phi;

    public SimpleIteration(MathFunction func) { // конструктор с одним параметром - функцией
        if (func == null) {
            throw new IllegalArgumentException("The iteration function cannot be null");
        }
        phi = func;
    }

    // Конструктор с точностью и максимальным количеством итераций
    public SimpleIteration(MathFunction func, double prec, int maxIter) {
        if (func == null) {
            throw new IllegalArgumentException("The iteration function cannot be null");
        }
        if (prec <= 0) {
            throw new IllegalArgumentException("Precision must be positive");
        }
        if (Double.isNaN(prec) || Double.isInfinite(prec)) {
            throw new IllegalArgumentException(("Precision cannot be NaN or infinite"));
        }
        if (maxIter <= 0 || maxIter >= 1_000_000) {
            throw new IllegalArgumentException("The number of iterations must be positive");
        }
        phi = func;
        precision = prec;
        maxIterations = maxIter;
    }

    /**
     * Solves the equation x = phi(x) using simple iterations
     * @return root of the equation
     * @throws IllegalArgumentException if initialApproximation is NaN or infinite
     * @throws ArithmeticException if method did not converge
     */

    @Override
    public double apply(double initialApproximation) {
        if (Double.isNaN(initialApproximation) || Double.isInfinite(initialApproximation)) {
            throw new IllegalArgumentException(("Initial approximation cannot be NaN or infinite"));
        }
        double x0 = initialApproximation;
        double x1;
        int iteration = 0;

        while(iteration <= maxIterations) {
            x1 = phi.apply(x0);

            if (Math.abs(x1 - x0) < precision) {
                return x1;
            }

            x0 = x1;
            iteration++;
        }

        throw new ArithmeticException("The method did not converge within the selected number of iterations");
    }

    // геттеры и сеттеры
    public double getPrecision() {
        return precision;
    }
    public int getMaxIterations() {
        return maxIterations;
    }
}
