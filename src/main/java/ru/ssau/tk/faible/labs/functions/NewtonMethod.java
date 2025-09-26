package ru.ssau.tk.faible.labs.functions;

public class NewtonMethod implements MathFunction {
    private static final double DEFAULT_TOLERANCE = 1e-10;
    private static final int DEFAULT_MAX_ITERATIONS = 1000;

    private final MathFunction function;
    private final double initialGuess;
    // Убрали: private Double solution;

    public NewtonMethod(MathFunction function, double initialGuess) {
        this.function = function;
        this.initialGuess = initialGuess;
    }

    @Override
    public double apply(double x) {
        return function.apply(x);
    }

    public double solve() {

        MathFunction derivative = createDerivative(function);
        double x = initialGuess;

        for (int i = 0; i < DEFAULT_MAX_ITERATIONS; i++) {
            double fx = function.apply(x);
            double fpx = derivative.apply(x);

            if (Math.abs(fpx) < DEFAULT_TOLERANCE) {
                throw new IllegalArgumentException("Производная близка к нулю");
            }

            double xNew = x - fx / fpx;

            if (Math.abs(xNew - x) < DEFAULT_TOLERANCE) {
                return xNew; // Просто возвращаем, не кэшируем
            }

            x = xNew;
        }

        throw new IllegalArgumentException("Метод не сошелся");
    }

    public double getRoot() {
        return solve(); // Всегда вычисляет заново
    }

    private MathFunction createDerivative(MathFunction function) {
        final double h = 1e-8;
        return x -> (function.apply(x + h) - function.apply(x - h)) / (2 * h);
    }
}