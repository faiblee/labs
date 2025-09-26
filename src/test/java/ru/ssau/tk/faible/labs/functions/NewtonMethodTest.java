package ru.ssau.tk.faible.labs.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NewtonMethodTest {

    @Test // тестируем проверку null функции в конструкторе
    void constructor_ShouldThrow_WhenFunctionIsNull() {

        MathFunction nullFunction = null;
        double initialGuess = 10.0;
        Assertions.assertThrows(IllegalArgumentException.class, () -> new NewtonMethod(nullFunction, initialGuess));
    }

    @Test // проверка уравнения с корректными параметрами
    void apply_ShouldReturnFunctionValue() {
        MathFunction f = x -> x * x - 25;
        NewtonMethod solver = new NewtonMethod(f, 10.0);

        double result = solver.apply(0.0);
        Assertions.assertEquals(5.0, result, 1e-10);
    }

    // решение уравнения отрицательный корень
    @Test
    void solve_ShouldFindNegativeRoot() {
        MathFunction f = x -> x * x - 25;
        NewtonMethod solver = new NewtonMethod(f, -10.0);

        double root = solver.apply(0.0);
        Assertions.assertEquals(-5.0, root, 1e-6);
    }

    // решение уравнения с нулевым корнем
    @Test
    void solve_ShouldFindZeroRooSt() {
        MathFunction f = x -> x * x; // x² = 0
        NewtonMethod solver = new NewtonMethod(f, 1.0);

        double root = solver.apply(0.0);
        Assertions.assertEquals(0.0, root, 1e-6);
    }



    // исключение при нулевой производной
    @Test
    void solve_ShouldThrow_WhenDerivativeIsZero() {
        MathFunction constantFunction = x-> 5.0; // f(x) = 5 (константа)
        NewtonMethod solver = new NewtonMethod(constantFunction, 1.0);
        Assertions.assertThrows(IllegalArgumentException.class, () -> solver.apply(0.0));
    }

    // исключение при отсутствии вещественных корней
    @Test
    void solve_ShouldThrow_WhenNoRealRoots() {
        MathFunction noRealRoots = x -> x * x + 1; // x² + 1 = 0
        NewtonMethod solver = new NewtonMethod(noRealRoots, 1.0);
    }

    // Тестирует решение тригонометрического уравнения
    @Test
    void solve_ShouldTrigonometricFunction() {
        MathFunction f = Math::sin; // sin(x) = 0, x = π
        NewtonMethod solver = new NewtonMethod(f, 3.0);

        double root = solver.apply(0.0);
        Assertions.assertEquals(Math.PI, root, 1e-6);
    }


}