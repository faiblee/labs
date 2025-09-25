package ru.ssau.tk.faible.labs.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqrFunctionTest {

    SqrFunction function = new SqrFunction();

    @Test
    void applyOneTest() {
        double result = function.apply(1.0);
        Assertions.assertEquals(1.0,result);
    }
    @Test
    void applyPositiveTest() {
        double result = function.apply(5.0);
        Assertions.assertEquals(25.0, result);
    }
    @Test
    void applyZeroTest() {
        double result = function.apply(0.0);
        Assertions.assertEquals(0.0, result);
    }
    @Test
    void applyMinusOneTest() {
        double result = function.apply(-1.0);
        Assertions.assertEquals(1.0, result);
    }
    @Test
    void applyNegativeFractionTest() {
        double result = function.apply(-2.5);
        Assertions.assertEquals(6.25, result);
    }
    @Test
    void squareLargeNumberTest() {
        double result = function.apply(1000000.0);
        Assertions.assertEquals(1000000000000.0, result);
    }
    @Test
    void applyInfinityTest() {
        double result = function.apply(Double.POSITIVE_INFINITY);
        Assertions.assertEquals(Double.POSITIVE_INFINITY, result);
    }

    @Test
    void applyNaNTest() {
        double result = function.apply(Double.NaN);
        Assertions.assertTrue(Double.isNaN(result));
    }
}