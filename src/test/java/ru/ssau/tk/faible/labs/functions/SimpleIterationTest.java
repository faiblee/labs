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
        double result = solver.apply(0.0);
        assertEquals(2.0, result, PRECISION);
    }

    @Test
    void SimpleIterationIdenticalFunctionTest() { // тождественная функция
        MathFunction f = x -> x;
        solver = new SimpleIteration(f);
        double result = solver.apply(0.0);
        assertEquals(0.0, result, PRECISION);
    }

    @Test
    void SimpleIterationAbnormalTest() {
        MathFunction f = x -> 2 * x + 1; // расходщаяся функция, т.к. производная = 2 > 1
        solver = new SimpleIteration(f);
        assertThrows(ArithmeticException.class, () -> solver.apply(0.0));
    }

    @Test
    void SimpleIterationNullFunctionTest() { // если функция = null
        assertThrows(IllegalArgumentException.class,
                () -> new SimpleIteration(null, 1e-6, 100));
    }

    @Test
    void SimpleIterationPrecisionTest() { // отрицательная точность
        assertThrows(IllegalArgumentException.class,
                () -> new SimpleIteration(function, -1e-6, 1000));
    }

    @Test
    void SimpleIterationMaxIterationsTest() { // отрицательное число итераций
        assertThrows(IllegalArgumentException.class,
                () -> new SimpleIteration(function, 1e-6, -100));
    }

    @Test
    void SimpleIterationInitialApproximateNaNTest() { // начальное приближение - NaN
        solver = new SimpleIteration(function);
        assertThrows(IllegalArgumentException.class,
                () -> solver.apply(Double.NaN));
    }

    @Test
    void SimpleIterationInitialApproximatePosInfiniteTest() { // начальное приближение - положительная бесконечность
        solver = new SimpleIteration(function);
        assertThrows(IllegalArgumentException.class,
                () -> solver.apply(Double.POSITIVE_INFINITY));
    }

    @Test
    void SimpleIterationInitialApproximateNegInfiniteTest() { // начальное приближение - отрицательная бесконечность
        solver = new SimpleIteration(function);
        assertThrows(IllegalArgumentException.class,
                () -> solver.apply(Double.NEGATIVE_INFINITY));
    }

    @Test
    void SimpleIterationGetPrecisionTest() {
        solver = new SimpleIteration(function);
        assertEquals(PRECISION, solver.getPrecision());
    }

    @Test
    void SimpleIterationGetMaxIterationsTest() {
        solver = new SimpleIteration(function);
        assertEquals(1000, solver.getMaxIterations());
    }
}