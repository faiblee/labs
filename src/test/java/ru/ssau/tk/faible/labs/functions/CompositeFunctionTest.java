package ru.ssau.tk.faible.labs.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompositeFunctionTest {

    private static final double PRECISION = 1e-6;

    @Test // сложная функция f(g(x))
    void CompositeFunctionBasicTest() { // ((x+1)+2)
        MathFunction add1 = x -> x + 1;
        MathFunction add2 = x -> x + 2;
        CompositeFunction function = new CompositeFunction(add1, add2);

        assertEquals(4.0, function.apply(1.0), PRECISION);
    }

    @Test // сложная функция f(f(x))
    void CompositeFunctionSameFunction() { // (x^2)^2
        MathFunction pow2 = x -> Math.pow(x, 2);
        CompositeFunction function = new CompositeFunction(pow2, pow2);

        assertEquals(16.0, function.apply(2.0), PRECISION);
    }

    @Test // сложная функция f(g(h(x)))
    void CompositeFunctionComplexFunction() { // (2*(x+1))^2
        MathFunction add1 = x -> x + 1;
        MathFunction mult2 = x -> 2 * x;
        MathFunction pow2 = x -> Math.pow(x, 2);
        CompositeFunction func1 = new CompositeFunction(add1, mult2); // (x + 1) * 2
        CompositeFunction function = new CompositeFunction(func1, pow2); // (2*(x+1))^2

        assertEquals(36.0, function.apply(2.0), PRECISION);
    }

    @Test // проверка исключительных ситуаций
    void CompositeFunctionNullArgs() {
        MathFunction add1 = x -> x + 1;
        assertThrows(IllegalArgumentException.class, () -> new CompositeFunction(null, add1)); // firstFunc - null
        assertThrows(IllegalArgumentException.class, () -> new CompositeFunction(add1, null)); // secondFunc - null
        assertThrows(IllegalArgumentException.class, () -> new CompositeFunction(null, null)); // first & second func - null
    }

    @Test // тест для специальных значений Double
    void CompositeFunctionSpecialValues() {
        MathFunction add1 = x -> x + 1;

        CompositeFunction function = new CompositeFunction(add1, add1); // (x + 1) + 1

        assertTrue(Double.isNaN(function.apply(Double.NaN))); // (NaN + 1) + 1 = NaN
        assertEquals(Double.POSITIVE_INFINITY, function.apply(Double.POSITIVE_INFINITY)); //(PosInf + 1) + 1 = PosInf
        assertEquals(Double.NEGATIVE_INFINITY, function.apply(Double.NEGATIVE_INFINITY)); //(NegInf + 1) + 1 = NegInf
    }

    @Test // тестирование больших сложных функций
    void CompositeFunctionHugeComplex() {
        MathFunction add5 = x -> x + 5;
        MathFunction mult3 = x -> x * 3;
        CompositeFunction func1 = new CompositeFunction(add5, mult3); // (x + 5) * 3
        CompositeFunction func2 = new CompositeFunction(mult3, add5); // x * 3 + 5
        CompositeFunction function = new CompositeFunction(func1, func2); // ((x + 5) * 3) * 3 + 5

        assertEquals(68.0, function.apply(2.0), PRECISION);
    }

    @Test // тестирование с классами SqrFunction и IdentityFunction
    void CompositeFunctionSqrIdentityFunction() {
        MathFunction square = new SqrFunction();
        MathFunction identity = new IdentityFunction();

        CompositeFunction function = new CompositeFunction(identity, square);
        assertEquals(16.0, function.apply(4.0), PRECISION);
    }

    @Test // тестирование с классами NewtonMethod и SimpleIteration
    void CompositeFunctionNewtonSimpleIterationMethod() {
        MathFunction Newton = new NewtonMethod(x -> x * x - 25, -10.0); // -5
        MathFunction SimpleIter = new SimpleIteration(x -> 0.5 * x + 2, PRECISION, 100, 1.0); // 4
        MathFunction add3 = x -> x + 3;

        CompositeFunction functionNewton = new CompositeFunction(Newton, add3); // (-5) + 3
        CompositeFunction functionSimpleIter = new CompositeFunction(SimpleIter, add3); // 4 + 3

        assertEquals(-2.0, functionNewton.apply(0.0), PRECISION);
        assertEquals(7.0, functionSimpleIter.apply(0.0), PRECISION);
    }

    @Test // тест andThen с двумя LinkedList функциями
    void andThenWithTwoLinkedListTabulatedFunctionsTest() {
        // Первая функция: f(x) = x + 1
        double[] xValues1 = {1.0, 2.0, 3.0, 4.0};
        double[] yValues1 = {2.0, 3.0, 4.0, 5.0};
        MathFunction func1 = new LinkedListTabulatedFunction(xValues1, yValues1);

        // Вторая функция: g(x) = x * 2
        double[] xValues2 = {2.0, 3.0, 4.0, 5.0};
        double[] yValues2 = {4.0, 6.0, 8.0, 10.0};
        MathFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        // Композиция: h(x) = g(f(x)) = (x + 1) * 2 = 2x + 2
        MathFunction composite = func1.andThen(func2);

        // значения которые уже есть
        assertEquals(4.0, composite.apply(1.0), PRECISION); // 2*1 + 2 = 4
        assertEquals(6.0, composite.apply(2.0), PRECISION); // 2*2 + 2 = 6
        assertEquals(8.0, composite.apply(3.0), PRECISION); // 2*3 + 2 = 8

        // интерполяция
        assertEquals(5.0, composite.apply(1.5), PRECISION); // 2*1.5 + 2 = 5
        assertEquals(7.0, composite.apply(2.5), PRECISION); // 2*2.5 + 2 = 7
    }

    @Test // тест andThen с комбинацией разных видов функций
    void andThenWithThreeTabulatedFunctions() {

        MathFunction phi = x -> 0.5*x + 1;
        MathFunction f = new SimpleIteration(phi, PRECISION, 500, 0.5);

        // f(x) = x + 1
        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {2.0, 3.0, 4.0};
        MathFunction g = new LinkedListTabulatedFunction(xValues1, yValues1);

        // g(x) = x * 2 - простая лямбда-функция
        MathFunction h = x -> x * 2;

        // сначала находим корень x=0.5x+1 через метод простых итераций, потом прибавляем 1 и умножаем на 2
        MathFunction composite = f.andThen(g).andThen(h);

        // Проверяем значения
        assertEquals(6.0, composite.apply(0.0), PRECISION);

    }

}