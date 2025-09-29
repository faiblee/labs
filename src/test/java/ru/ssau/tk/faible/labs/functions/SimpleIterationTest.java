package ru.ssau.tk.faible.labs.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleIterationTest {
    private SimpleIteration solver;
    private static final double PRECISION = 1e-6;

    private final MathFunction function = x -> 0.5*x + 1; // сходящаяся функция

    @Test
    void SimpleIterationNormalTest() { // обычная сходящаяся функция
        solver = new SimpleIteration(function);
        double result = solver.solve();
        assertEquals(2.0, result, PRECISION);
    }

    @Test
    void SimpleIterationIdenticalFunctionTest() { // тождественная функция
        MathFunction f = x -> x;
        solver = new SimpleIteration(f);
        double result = solver.solve();
        assertEquals(0.0, result, PRECISION);
    }

    @Test
    void SimpleIterationAbnormalTest() {
        MathFunction f = x -> 2 * x + 1; // расходящаяся функция, т.к. производная = 2 > 1
        solver = new SimpleIteration(f);
        assertThrows(ArithmeticException.class, () -> solver.solve());
    }

    @Test
    void SimpleIterationNullFunctionTest() { // если функция = null
        assertThrows(IllegalArgumentException.class,
                () -> new SimpleIteration(null, 1e-6, 100, 0.0));
    }

    @Test
    void SimpleIterationPrecisionTest() { // отрицательная точность
        assertThrows(IllegalArgumentException.class,
                () -> new SimpleIteration(function, -1e-6, 1000, 0.0));
    }

    @Test
    void SimpleIterationMaxIterationsTest() { // отрицательное число итераций
        assertThrows(IllegalArgumentException.class,
                () -> new SimpleIteration(function, 1e-6, -100, 0.0));
    }

    @Test
    // линейная функция с маленькими коэффициентами
    void SimpleIterationSmallCoefficientTest() {
        // phi(x) = 0.1x + 2
        MathFunction linear = x -> 0.1 * x + 2.0;
        SimpleIteration solver = new SimpleIteration(linear, 1e-8, 100, 0.0);

        double result = solver.solve();
        double expected = 2.0 / (1 - 0.1); // x = b/(1-a)

        assertEquals(expected, result, PRECISION);
    }

    @Test
    void SimpleIterationSqrtFunctionTest() {
        //  phi(x) = sqrt(x + 2)
        MathFunction sqrtFunction = x -> Math.sqrt(x + 2);
        SimpleIteration solver = new SimpleIteration(sqrtFunction, 1e-8, 100, 1.5);

        double result = solver.solve();

        assertEquals(2.0, result, PRECISION);
    }

    @Test
    void SimpleIterationHighPrecisionTest() {
        MathFunction phi = x -> 0.5 * x + 1; // Корень: x = 2
        SimpleIteration solver = new SimpleIteration(phi, 1e-12, 1000, 0.0);

        double result = solver.solve();

        assertEquals(2.0, result, PRECISION);
    }

    @Test
    void SimpleIterationDifferentInitialApproxTest() {
        SimpleIteration solver;
        MathFunction phi = Math::cos; // x = cos(x)


        solver = new SimpleIteration(phi, 1e-8, 1000, 0.0);
        double result = solver.solve();
        assertEquals(result, Math.cos(result), PRECISION);

        solver = new SimpleIteration(phi, 1e-8, 1000, 0.5);
        result = solver.solve();
        assertEquals(result, Math.cos(result), PRECISION);

        solver = new SimpleIteration(phi, 1e-8, 1000, 1.0);
        result = solver.solve();
        assertEquals(result, Math.cos(result), PRECISION);


    }
}