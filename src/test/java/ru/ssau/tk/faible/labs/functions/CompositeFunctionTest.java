package ru.ssau.tk.faible.labs.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompositeFunctionTest {

    private static final double PRECISION = 1e-6;

    @Test
        // сложная функция f(g(x))
    void CompositeFunctionBasicTest() { // ((x+1)+2)
        MathFunction add1 = x -> x + 1;
        MathFunction add2 = x -> x + 2;
        CompositeFunction function = new CompositeFunction(add1, add2);

        assertEquals(4.0, function.apply(1.0), PRECISION);
    }

    @Test
        // сложная функция f(f(x))
    void CompositeFunctionSameFunction() { // (x^2)^2
        MathFunction pow2 = x -> Math.pow(x, 2);
        CompositeFunction function = new CompositeFunction(pow2, pow2);

        assertEquals(16.0, function.apply(2.0), PRECISION);
    }

    @Test
        // сложная функция f(g(h(x)))
    void CompositeFunctionComplexFunction() { // (2*(x+1))^2
        MathFunction add1 = x -> x + 1;
        MathFunction mult2 = x -> 2 * x;
        MathFunction pow2 = x -> Math.pow(x, 2);
        CompositeFunction func1 = new CompositeFunction(add1, mult2); // (x + 1) * 2
        CompositeFunction function = new CompositeFunction(func1, pow2); // (2*(x+1))^2

        assertEquals(36.0, function.apply(2.0), PRECISION);
    }

    @Test
        // проверка исключительных ситуаций
    void CompositeFunctionNullArgs() {
        MathFunction add1 = x -> x + 1;
        assertThrows(IllegalArgumentException.class, () -> new CompositeFunction(null, add1)); // firstFunc - null
        assertThrows(IllegalArgumentException.class, () -> new CompositeFunction(add1, null)); // secondFunc - null
        assertThrows(IllegalArgumentException.class, () -> new CompositeFunction(null, null)); // first & second func - null
    }

    @Test
        // тест для специальных значений Double
    void CompositeFunctionSpecialValues() {
        MathFunction add1 = x -> x + 1;

        CompositeFunction function = new CompositeFunction(add1, add1); // (x + 1) + 1

        assertTrue(Double.isNaN(function.apply(Double.NaN))); // (NaN + 1) + 1 = NaN
        assertEquals(Double.POSITIVE_INFINITY, function.apply(Double.POSITIVE_INFINITY)); //(PosInf + 1) + 1 = PosInf
        assertEquals(Double.NEGATIVE_INFINITY, function.apply(Double.NEGATIVE_INFINITY)); //(NegInf + 1) + 1 = NegInf
    }

    @Test
        // тестирование больших сложных функций
    void CompositeFunctionHugeComplex() {
        MathFunction add5 = x -> x + 5;
        MathFunction mult3 = x -> x * 3;
        CompositeFunction func1 = new CompositeFunction(add5, mult3); // (x + 5) * 3
        CompositeFunction func2 = new CompositeFunction(mult3, add5); // x * 3 + 5
        CompositeFunction function = new CompositeFunction(func1, func2); // ((x + 5) * 3) * 3 + 5

        assertEquals(68.0, function.apply(2.0), PRECISION);
    }

    @Test
        // тестирование с классами SqrFunction и IdentityFunction
    void CompositeFunctionSqrIdentityFunction() {
        MathFunction square = new SqrFunction();
        MathFunction identity = new IdentityFunction();

        CompositeFunction function = new CompositeFunction(identity, square);
        assertEquals(16.0, function.apply(4.0), PRECISION);
    }

    @Test
        // тест andThen с двумя LinkedList функциями
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

    @Test
        // тест andThen с комбинацией разных видов функций
    void andThenWithThreeTabulatedFunctions() {
        // f(x) = x^2
        MathFunction f = new SqrFunction();

        // g(x) = x + 1
        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {2.0, 3.0, 4.0};
        MathFunction g = new LinkedListTabulatedFunction(xValues1, yValues1);

        // h(x) = x * 2 - простая лямбда-функция
        MathFunction h = x -> x * 2;

        // сначала возводим x в квадрат, потом прибавляем 1 и умножаем на 2
        MathFunction composite = f.andThen(g).andThen(h);

        // Проверяем значения
        assertEquals(10.0, composite.apply(2.0), PRECISION);

    }

    @Test // тест andThen с LinkedListTabulated и ArrayTabulated функциями
    void andThenWithLinkedListAndArrayTabulatedFunctionsTest() {
        MathFunction f = x -> x + 1;
        MathFunction func1 = new LinkedListTabulatedFunction(f, 1.0, 4.0, 4);

        // Вторая функция: g(x) = x * 2
        double[] xValues2 = {2.0, 3.0, 4.0, 5.0};
        double[] yValues2 = {4.0, 6.0, 8.0, 10.0};
        MathFunction func2 = new ArrayTabulatedFunction(xValues2, yValues2);

        // композиция: h(x) = g(f(x)) = (x + 1) * 2 = 2x + 2
        MathFunction composite = func1.andThen(func2);

        // значения которые уже есть
        assertEquals(4.0, composite.apply(1.0), PRECISION); // 2*1 + 2 = 4
        assertEquals(6.0, composite.apply(2.0), PRECISION); // 2*2 + 2 = 6
        assertEquals(8.0, composite.apply(3.0), PRECISION); // 2*3 + 2 = 8

        // интерполяция
        assertEquals(5.0, composite.apply(1.5), PRECISION); // 2*1.5 + 2 = 5
        assertEquals(7.0, composite.apply(2.5), PRECISION); // 2*2.5 + 2 = 7
    }
    @Test
    void andThenWithNegativeValuesTest() {
        // f(x) = |x|
        double[] xValues1 = {-3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0};
        double[] yValues1 = {3.0, 2.0, 1.0, 0.0, 1.0, 2.0, 3.0};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);

        // g(x) = √x
        MathFunction sqrt = Math::sqrt;
        ArrayTabulatedFunction func2 = new ArrayTabulatedFunction(sqrt, 0.0, 4.0, 5);

        // композиция: g(f(x)) = √|x|
        MathFunction composite = func1.andThen(func2);

        assertEquals(Math.sqrt(3.0), composite.apply(-3.0), PRECISION);
        assertEquals(Math.sqrt(2.0), composite.apply(-2.0), PRECISION);
        assertEquals(0.0, composite.apply(0.0), PRECISION);
        assertEquals(1.0, composite.apply(1.0), PRECISION);
        assertEquals(Math.sqrt(3.0), composite.apply(3.0), PRECISION);
    }
    @Test
    void andThenWithExtremeValuesTest() {
        // f(x) с очень большими значениями
        double[] xValues1 = {1e-10, 1.0, 1e10};
        double[] yValues1 = {1e-10, 1.0, 1e10};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);

        // g(x) = log(x)
        MathFunction log = Math::log;

        // композиция: g(f(x)) = log(x)
        MathFunction composite = func1.andThen(log);

        assertEquals(Math.log(1e-10), composite.apply(1e-10), PRECISION);
        assertEquals(0.0, composite.apply(1.0), PRECISION);
        assertEquals(Math.log(1e10), composite.apply(1e10), PRECISION);
    }
    @Test
    void andThenWithIdentityFunctionTest() {
        // f(x) = 2x + 1
        MathFunction linear = x -> 2 * x + 1;
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(linear, 0.0, 3.0, 4);

        // g(x) = x (тождественная функция)
        MathFunction identity = x -> x;

        // Композиция: g(f(x)) = 2x + 1
        MathFunction composite = func.andThen(identity);

        assertEquals(1.0, composite.apply(0.0), PRECISION);
        assertEquals(3.0, composite.apply(1.0), PRECISION);
        assertEquals(5.0, composite.apply(2.0), PRECISION);
        assertEquals(7.0, composite.apply(3.0), PRECISION);
    }

}