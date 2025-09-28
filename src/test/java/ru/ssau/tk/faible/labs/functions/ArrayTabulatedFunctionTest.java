package ru.ssau.tk.faible.labs.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ArrayTabulatedFunctionTest {

    @Test
    void constructorWithArraysTest() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Assertions.assertEquals(3, function.getCount());
        Assertions.assertEquals(1.0, function.leftBound());
        Assertions.assertEquals(3.0, function.rightBound());
    }

    @Test
    void floorIndexOfXSinglePointTest() {
        double[] xValues = {5.0};
        double[] yValues = {10.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Assertions.assertEquals(0.0, function.floorIndexOfX(3.0),1e-10);
        Assertions.assertEquals(0.0, function.floorIndexOfX(5.0),1e-10);
        Assertions.assertEquals(1.0, function.floorIndexOfX(7.0),1e-10);
    }

    @Test
    void extrapolateLeftSinglePointTest() {
        double[] xValues = {5.0};
        double[] yValues = {10.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        double result = function.extrapolateLeft(1.0);
        Assertions.assertEquals(10.0, result);
    }

    @Test
    void applySinglePointTest() {
        double[] xValues = {5.0};
        double[] yValues = {10.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Assertions.assertEquals(10.0, function.apply(1.0),1e-10);
        Assertions.assertEquals(10.0, function.apply(5.0),1e-10);
        Assertions.assertEquals(10.0, function.apply(10.0),1e-10);
    }

    @Test
    void applyMultiplePointsTest() {
        double[] xValues = {2.0, 4.0, 6.0};
        double[] yValues = {4.0, 8.0, 12.0}; // y = x²
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Assertions.assertEquals(2.0, function.apply(1.0),1e-10);
        Assertions.assertEquals(3.0, function.apply(1.5),1e-10);
        Assertions.assertEquals(0.0, function.apply(0.0),1e-10);
        Assertions.assertEquals(8.0, function.apply(4.0),1e-10);
    }
}