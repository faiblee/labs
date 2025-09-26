package ru.ssau.tk.faible.labs.functions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ZeroFunctionTest {

    private  final ZeroFunction function = new ZeroFunction();

    @Test
    void applyPositiveTest(){
        double result = function.apply(10.0);
        Assertions.assertEquals(0.0,result);
    }
    @Test
    void applyNegativeTest(){
        double result = function.apply(-10.0);
        Assertions.assertEquals(0.0,result);
    }
    @Test
    void applyZeroTest(){
        double result = function.apply(0.0);
        Assertions.assertEquals(0.0,result);
    }
    @Test
    void applyMaxValueTest() {
        double result = function.apply(Double.MAX_VALUE);
        Assertions.assertEquals(0.0, result);
    }

    @Test
    void applyMinValueTest() {
        double result = function.apply(Double.MIN_VALUE);
        Assertions.assertEquals(0.0, result);
    }
    @Test
    void applyPositiveInfinityTest() {
        double result = function.apply(Double.POSITIVE_INFINITY);
        Assertions.assertEquals(0.0, result);
    }

    @Test
    void applyNegativeInfinityTest() {
        double result = function.apply(Double.NEGATIVE_INFINITY);
        Assertions.assertEquals(0.0, result);
    }
    @Test
    void applyNaNTest() {
        double result = function.apply(Double.NaN);
        Assertions.assertEquals(0.0, result);
    }

    @Test
    void getConstantTest() {
        double constant = function.getConstant();
        Assertions.assertEquals(0.0, constant);
    }


}