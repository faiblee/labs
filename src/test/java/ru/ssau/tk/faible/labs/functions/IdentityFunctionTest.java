package ru.ssau.tk.faible.labs.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IdentityFunctionTest {

    private final IdentityFunction function = new IdentityFunction();

    @Test
    void applyPositiveNumberTest() {
        double result = function.apply(103.3);
        Assertions.assertEquals(103.3, result);
    }

    @Test
    void applyNegativeNumberTest() {
        double result = function.apply(-15.75);
        Assertions.assertEquals(-15.75, result);
    }

    @Test
    void applyZeroTest() {
        double result = function.apply(0.0);
        Assertions.assertEquals(0.0, result);
    }

    @Test
    void applyPositiveInfinityTest() {
        double result = function.apply(Double.POSITIVE_INFINITY);
        Assertions.assertEquals(Double.POSITIVE_INFINITY, result);
    }

    @Test
    void applyNegativeInfinityTest() {
        double result = function.apply(Double.NEGATIVE_INFINITY);
        Assertions.assertEquals(Double.NEGATIVE_INFINITY, result);
    }

    @Test
    void applyNaNTest() {
        double result = function.apply(Double.NaN);
        Assertions.assertTrue(Double.isNaN(result));
    }

    @Test
    void applyMaxValueTest() {
        double result = function.apply(Double.MAX_VALUE);
        Assertions.assertEquals(Double.MAX_VALUE, result);
    }

    @Test
    void applyMinValueTest() {
        double result = function.apply(Double.MIN_VALUE);
        Assertions.assertEquals(Double.MIN_VALUE, result);
    }

    @Test
    void applyHighPrecisionTest() {
        double result = function.apply(123.456789101112);
        Assertions.assertEquals(123.456789101112, result);
    }

    @Test
    void applySmallNegativeNumberTest() {
        double result = function.apply(-0.00000000001);
        Assertions.assertEquals(-0.00000000001, result);
    }
}