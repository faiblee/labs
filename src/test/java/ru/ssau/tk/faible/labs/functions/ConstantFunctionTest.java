package ru.ssau.tk.faible.labs.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConstantFunctionTest {

    private final ConstantFunction function = new ConstantFunction(5.0);

    @Test
    void applyPositiveNumberTest() {
        double result = function.apply(10.0);
        Assertions.assertEquals(5.0, result);
    }

    @Test
    void applyNegativeNumberTest() {
        double result = function.apply(-10.0);
        Assertions.assertEquals(5.0, result);
    }

    @Test
    void applyZeroTest() {
        double result = function.apply(0.0);
        Assertions.assertEquals(5.0, result);
    }

    @Test
    void applyMaxValueTest() {
        double result = function.apply(Double.MAX_VALUE);
        Assertions.assertEquals(5.0, result);
    }

    @Test
    void applyMinValueTest() {
        double result = function.apply(Double.MIN_VALUE);
        Assertions.assertEquals(5.0, result);
    }

    @Test
    void applyPositiveInfinityTest() {
        double result = function.apply(Double.POSITIVE_INFINITY);
        Assertions.assertEquals(5.0, result);
    }

    @Test
    void applyNegativeInfinityTest() {
        double result = function.apply(Double.NEGATIVE_INFINITY);
        Assertions.assertEquals(5.0, result);
    }

    @Test
    void applyNaNTest() {
        double result = function.apply(Double.NaN);
        Assertions.assertEquals(5.0, result);
    }

    @Test
    void getConstantTest() {
        double constant = function.getConstant();
        Assertions.assertEquals(5.0, constant);
    }

    private final ConstantFunction fun = new ConstantFunction(-3.5);

    @Test
    void applyPosNumberTest() {
        double result = fun.apply(10.0);
        Assertions.assertEquals(-3.5, result);
    }

    @Test
    void applyNegNumberTest() {
        double result = fun.apply(-10.0);
        Assertions.assertEquals(-3.5, result);
    }

    @Test
    void applyZerTest() {
        double result = fun.apply(0.0);
        Assertions.assertEquals(-3.5, result);
    }

    private final ConstantFunction funct = new ConstantFunction(0.0);

    @Test
    void applyPositNumberTest() {
        double result = funct.apply(10.0);
        Assertions.assertEquals(0.0, result);
    }

    @Test
    void applyNegatNumberTest() {
        double result = funct.apply(-10.0);
        Assertions.assertEquals(0.0, result);
    }

    @Test
    void applyZerooTest() {
        double result = funct.apply(0.0);
        Assertions.assertEquals(0.0, result);
    }

    private final ConstantFunction func = new ConstantFunction(2.71828);

    @Test
    void applyIntegerTest() {
        double result = func.apply(100);
        Assertions.assertEquals(2.71828, result);
    }

    @Test
    void applyFractionTest() {
        double result = func.apply(3.14159);
        Assertions.assertEquals(2.71828, result);
    }
}