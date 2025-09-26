package ru.ssau.tk.faible.labs.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MathFunctionTest {

    @Test //сложная функция: сначала квадрат, затем инкремент
    void andThenSquareAndIncrementTest() {
        MathFunction square = x -> x * x;
        MathFunction increment = x -> x + 1;

        MathFunction composite = square.andThen(increment);
        double result = composite.apply(3.0);

        Assertions.assertEquals(10.0, result);
    }

    @Test // сложная функция с ConstantFunction
    void andThenConstantAndSquareTest() {
        MathFunction constant = new ConstantFunction(5.0);
        MathFunction square = x -> x * x;

        MathFunction composite = constant.andThen(square);
        double result = composite.apply(10.0);

        Assertions.assertEquals(25.0, result);
    }

    @Test // сложная функцию с ZeroFunction
    void andThenZeroFunctionTest() {
        MathFunction square = x -> x * x;
        MathFunction zeroFunction = new ZeroFunction();

        MathFunction composite = square.andThen(zeroFunction);
        double result = composite.apply(5.0);

        Assertions.assertEquals(0.0, result);
    }

    @Test // сложная  функцию с UnitFunction
    void andThenUnitFunctionTest() {
        MathFunction increment = x -> x + 10;
        MathFunction unitFunction = new UnitFunction();

        MathFunction composite = increment.andThen(unitFunction);
        double result = composite.apply(3.0);

        Assertions.assertEquals(1.0, result);
    }

    @Test // цепочка из 3 функций
    void andThenThreeFunctionsTest() {
        MathFunction addFive = x -> x + 5;
        MathFunction multiplyByTwo = x -> x * 2;
        MathFunction subtractThree = x -> x - 3;

        MathFunction composite = addFive.andThen(multiplyByTwo).andThen(subtractThree);
        double result = composite.apply(4.0);

        Assertions.assertEquals(15.0, result);
    }

    @Test // одна и та же функция дважды
    void andThenSameFunctionTest() {
        MathFunction square = x -> x * x;

        MathFunction composite = square.andThen(square);
        double result = composite.apply(2.0);

        Assertions.assertEquals(16.0, result);
    }

    @Test // обработка null-аргумента
    void andThenWithNullTest() {
        MathFunction square = x -> x * x;

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            square.andThen(null);
        });
    }

    @Test // порядок применения функций в сложной функции
    void andThenOrderImportantTest() {
        MathFunction addTwo = x -> x + 2;
        MathFunction square = x -> x * x;

        MathFunction firstOrder = addTwo.andThen(square);
        MathFunction secondOrder = square.andThen(addTwo);

        double result1 = firstOrder.apply(3.0);
        double result2 = secondOrder.apply(3.0);

        Assertions.assertEquals(25.0, result1);
        Assertions.assertEquals(11.0, result2);
    }

    @Test // работа сложной функции с максимальным значением double
    void andThenMaxValueTest() {
        MathFunction identity = x -> x;
        MathFunction constant = new ConstantFunction(5.0);

        MathFunction composite = identity.andThen(constant);
        double result = composite.apply(Double.MAX_VALUE);

        Assertions.assertEquals(5.0, result);
    }

    @Test // // работа сложной функции с минимальным значением double
    void andThenMinValueTest() {
        MathFunction identity = x -> x;
        MathFunction constant = new ConstantFunction(5.0);

        MathFunction composite = identity.andThen(constant);
        double result = composite.apply(Double.MIN_VALUE);

        Assertions.assertEquals(5.0, result);
    }

    @Test // работа сложной функции с положительной бесконечностью
    void andThenPositiveInfinityTest() {
        MathFunction identity = x -> x;
        MathFunction square = x -> x * x;

        MathFunction composite = identity.andThen(square);
        double result = composite.apply(Double.POSITIVE_INFINITY);

        Assertions.assertEquals(Double.POSITIVE_INFINITY, result);
    }

    @Test // работа композитной функции с отрицательной бесконечностью
    void andThenNegativeInfinityTest() {
        MathFunction identity = x -> x;
        MathFunction square = x -> x * x;

        MathFunction composite = identity.andThen(square);
        double result = composite.apply(Double.NEGATIVE_INFINITY);

        Assertions.assertEquals(Double.POSITIVE_INFINITY, result);
    }

    @Test // работа сложной функции с NaN
    void andThenNaNTest() {
        MathFunction identity = x -> x;
        MathFunction square = x -> x * x;

        MathFunction composite = identity.andThen(square);
        double result = composite.apply(Double.NaN);

        Assertions.assertTrue(Double.isNaN(result));
    }


    @Test // применение сложной функции без сохранения в переменную
    void andThenImmediateApplyTest() {
        double result = new ConstantFunction(3.0)
                .andThen(x -> x * x)
                .andThen(x -> x + 1)
                .apply(10.0);

        Assertions.assertEquals(10.0, result);
    }

    @Test // сложная функцию с математическими функциями sin и exp
    void andThenSinAndExpTest() {
        MathFunction sin = Math::sin;
        MathFunction exp = Math::exp;

        MathFunction composite = sin.andThen(exp);
        double result = composite.apply(Math.PI / 2);

        Assertions.assertEquals(Math.E, result, 1e-10);
    }

    @Test // тестирует, что цепочка andThen вызовов возвращает CompositeFunction
    void andThenChainReturnsCompositeFunctionTest() {
        MathFunction f1 = x -> x + 1;
        MathFunction f2 = x -> x * 2;
        MathFunction f3 = x -> x - 3;

        MathFunction composite = f1.andThen(f2).andThen(f3);

        Assertions.assertTrue(composite instanceof CompositeFunction);
    }

    @Test // работа сложной функции с очень большими числами
    void andThenWithLargeNumbersTest() {
        MathFunction identity = x -> x;
        MathFunction multiplier = x -> x * 1e100;

        MathFunction composite = identity.andThen(multiplier);
        double result = composite.apply(1.0);

        Assertions.assertEquals(1e100, result, 1e90);
    }

    @Test // работа сложной функции с очень большими числами
    void andThenWithSmallNumbersTest() {
        MathFunction identity = x -> x;
        MathFunction divider = x -> x / 1e100;

        MathFunction composite = identity.andThen(divider);
        double result = composite.apply(1.0);

        Assertions.assertEquals(1e-100, result, 1e-110);
    }
}
