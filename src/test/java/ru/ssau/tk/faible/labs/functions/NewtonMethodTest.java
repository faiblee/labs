package ru.ssau.tk.faible.labs.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NewtonMethodTest {

    @Test
    void applyMethodTest() {
        MathFunction f = x -> x * x - 25;
        NewtonMethod solver = new NewtonMethod(f, 10.0);

        double result = solver.apply(5.0);
        Assertions.assertEquals(0.0, result, 1e-10);
    }

    @Test
    void solveMethodTest() {
        MathFunction f = x -> x * x - 25;
        NewtonMethod solver = new NewtonMethod(f, 10.0);

        double root = solver.solve();
        Assertions.assertEquals(5.0, root, 1e-6);
    }

    @Test
    void getRootMethodTest() {
        MathFunction f = x -> x * x - 9;
        NewtonMethod solver = new NewtonMethod(f, 5.0);

        double root = solver.getRoot();
        Assertions.assertEquals(3.0, root, 1e-6);
    }

    @Test
    void solveCachedSolutionTest() {
        MathFunction f = x -> x * x - 16;
        NewtonMethod solver = new NewtonMethod(f, 5.0);

        double result1 = solver.solve();
        double result2 = solver.getRoot(); // Должен вернуть кэшированное решение
        Assertions.assertEquals(result1, result2, 1e-10);
    }

    @Test
    void solveThrowsWhenDerivativeZeroTest() {
        MathFunction f = x -> 5.0; // Константная функция f(x) = 5
        NewtonMethod solver = new NewtonMethod(f, 1.0);

        Assertions.assertThrows(IllegalArgumentException.class, solver::solve);
    }

    @Test
    void solveThrowsWhenNotConvergedTest() {
        MathFunction f = x -> x * x + 1; // x² + 1 = 0 (нет вещественных корней)
        NewtonMethod solver = new NewtonMethod(f, 1.0);

        Assertions.assertThrows(IllegalArgumentException.class, solver::solve);
    }
}