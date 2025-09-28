package ru.ssau.tk.faible.labs.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunctionTest {
    private LinkedListTabulatedFunction function;
    private final double PRECISION = 1e-6;

    private final double[] xValues = new double[]{1.0, 2.0, 3.0, 4.0, 5.0};
    private final double[] yValues = new double[]{2.0, 4.0, 6.0, 8.0, 10.0};

    @Test
    void firstConstructorTest() { // проверка первого конструктора, методов getCount, getX, getY
        function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(5, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(4.0, function.getX(3));
        assertEquals(8.0, function.getY(3));
    }

    @Test
    void secondConstructorTest() { // проверка второго конструктора, методов leftBound, rightBound
        MathFunction func = x -> x * x; // x^2
        function = new LinkedListTabulatedFunction(func, 0, 4, 5);

        assertEquals(5, function.getCount());
        assertEquals(0.0, function.leftBound(), PRECISION);
        assertEquals(4.0, function.rightBound(), PRECISION);
        assertEquals(1.0, function.getX(1), PRECISION);
        assertEquals(4.0, function.getY(2), PRECISION);
    }

    @Test
    void swapXFromXToTest() { // смена местами xFrom и xTo прр xTo < xFrom
        MathFunction source = x -> x + 1;
        function = new LinkedListTabulatedFunction(source, 5, 1, 3);

        assertEquals(1.0, function.getX(0), PRECISION);
        assertEquals(3.0, function.getX(1), PRECISION);
        assertEquals(5.0, function.getX(2), PRECISION);
    }

    @Test
    void EqualsXFromXToTest() { // xFrom = xTo
        MathFunction source = Math::sin;
        function = new LinkedListTabulatedFunction(source, Math.PI, Math.PI, 4);

        assertEquals(4, function.getCount());
        for (int i = 0; i < 4; i++) { // все четыре x должны быть равны = pi
            assertEquals(Math.PI, function.getX(i), PRECISION);
            assertEquals(Math.sin(Math.PI), function.getY(i), PRECISION);
        }
    }

    @Test
    void setYTest() { // тест setY
        function = new LinkedListTabulatedFunction(xValues, yValues);

        function.setY(2, 100.0);
        assertEquals(100.0, function.getY(2), PRECISION);

        function.setY(0, 5.0);
        assertEquals(5.0, function.getY(0), PRECISION);

        function.setY(4, 99.0);
        assertEquals(99.0, function.getY(4), PRECISION);
    }

    @Test
    void indexOfXTest() { // тест indexOfX
        function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfX(1.0));
        assertEquals(2, function.indexOfX(3.0));
        assertEquals(4, function.indexOfX(5.0));
        assertEquals(-1, function.indexOfX(0.5));
    }

    @Test
    void indexOfYTest() { // тест indexOfY
        function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfY(2.0));
        assertEquals(2, function.indexOfY(6.0));
        assertEquals(4, function.indexOfY(10.0));
        assertEquals(-1, function.indexOfY(5.0));
    }

    @Test
    void floorIndexOfXTest() {
        function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(2, function.floorIndexOfX(3.0)); // точное совпадение

        assertEquals(1, function.floorIndexOfX(2.5)); // между существующими
        assertEquals(3, function.floorIndexOfX(4.7));

        assertEquals(0, function.floorIndexOfX(0.5)); // меньше всех

        assertEquals(5, function.floorIndexOfX(6.0)); // больше всех

    }

    @Test
    void interpolateTest() {
        function = new LinkedListTabulatedFunction(xValues, yValues);

        double result = function.interpolate(1.5, 0);
        assertEquals(3.0, result, PRECISION); // 2 + (4-2)*(1.5-1)/(2-1) = 3.0

        result = function.interpolate(2.5, 1);
        assertEquals(5.0, result, PRECISION); // 4 + (6-4)*(2.5-2)/(3-2) = 5.0

        result = function.interpolate(4.5, 3);
        assertEquals(9.0, result, PRECISION); // 8 + (10-8)*(4.5-4)/(5-4) = 9.0
    }

    @Test
    void interpolateSinglePointTest() { // интерполяция с одной точкой
        double[] xValues = {2.0};
        double[] yValues = {4.0};
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);

        double result = func.interpolate(3.0, 0);
        assertEquals(4.0, result, PRECISION);
    }

    @Test
    void extrapolateLeftTest() { // экстраполяция слева
        function = new LinkedListTabulatedFunction(xValues, yValues);

        double result = function.extrapolateLeft(0.0);
        assertEquals(0.0, result, PRECISION); // 2 + (4-2)*(0-1)/(2-1) = 0.0

        result = function.extrapolateLeft(0.5);
        assertEquals(1.0, result, PRECISION); // 2 + (4-2)*(0.5-1)/(2-1) = 1.0

        result = function.extrapolateLeft(-1.0);
        assertEquals(-2.0, result, PRECISION); // 2 + (4-2)*(-1-1)/(2-1) = -2.0
    }

    @Test
    void extrapolateLeftSinglePointTest() { // экстраполяция слева с одной точкой
        double[] xValues = {2.0};
        double[] yValues = {4.0};
        LinkedListTabulatedFunction singlePointFunc = new LinkedListTabulatedFunction(xValues, yValues);

        double result = singlePointFunc.extrapolateLeft(1.0);
        assertEquals(4.0, result, PRECISION);

        result = singlePointFunc.extrapolateLeft(0.0);
        assertEquals(4.0, result, PRECISION);
    }

    @Test
    void extrapolateRightTest() { // экстраполяция справа
        function = new LinkedListTabulatedFunction(xValues, yValues);

        double result = function.extrapolateRight(6.0);
        assertEquals(12.0, result, PRECISION); // 8 + (10-8)*(6-4)/(5-4) = 12.0

        result = function.extrapolateRight(5.5);
        assertEquals(11.0, result, PRECISION); // 8 + (10-8)*(5.5-4)/(5-4) = 11.0

        result = function.extrapolateRight(7.0);
        assertEquals(14.0, result, PRECISION); // 8 + (10-8)*(7-4)/(5-4) = 14.0
    }

    @Test
    void extrapolateRightSinglePointTest() { // экстраполяция справа с одной точкой
        double[] xValues = {2.0};
        double[] yValues = {4.0};
        LinkedListTabulatedFunction singlePointFunc = new LinkedListTabulatedFunction(xValues, yValues);

        double result = singlePointFunc.extrapolateRight(3.0);
        assertEquals(4.0, result, PRECISION);

        result = singlePointFunc.extrapolateRight(5.0);
        assertEquals(4.0, result, PRECISION);
    }

    @Test
    void interpolateWithCoordinatesTest() { // интерполяция по координатам
        function = new LinkedListTabulatedFunction(xValues, yValues);

        double result = function.interpolate(2.0, 1.0, 3.0, 2.0, 6.0);
        assertEquals(4.0, result, PRECISION); // 2 + (6-2)*(2-1)/(3-1) = 4.0

        result = function.interpolate(5.0, 0.0, 10.0, 0.0, 100.0);
        assertEquals(50.0, result, PRECISION); // 0 + (100-0)*(5-0)/(10-0) = 50.0

        result = function.interpolate(-1.0, -2.0, 2.0, 4.0, 10.0);
        assertEquals(5.5, result, PRECISION); // 4 + (10-4)*(-1+2)/(2+2) = 6.0
    }

    @Test
    void applyTest() { // тест для метода apply
        function = new LinkedListTabulatedFunction(xValues, yValues);

        // точное совпадение
        assertEquals(2.0, function.apply(1.0), PRECISION);
        assertEquals(6.0, function.apply(3.0), PRECISION);
        assertEquals(10.0, function.apply(5.0), PRECISION);

        // интерполяция
        assertEquals(3.0, function.apply(1.5), PRECISION);
        assertEquals(5.0, function.apply(2.5), PRECISION);
        assertEquals(7.0, function.apply(3.5), PRECISION);
        assertEquals(9.0, function.apply(4.5), PRECISION);

        // экстраполяция слева
        assertEquals(0.0, function.apply(0.0), PRECISION);
        assertEquals(-2.0, function.apply(-1.0), PRECISION);

        // экстраполяция справа
        assertEquals(12.0, function.apply(6.0), PRECISION);
        assertEquals(14.0, function.apply(7.0), PRECISION);
    }

    @Test
    void applyNonLinearFunctionTest() { // тест для нелинейной функции y=x^2
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction quadraticFunc = new LinkedListTabulatedFunction(xValues, yValues);

        // точное совпадение
        assertEquals(1.0, quadraticFunc.apply(1.0), PRECISION);
        assertEquals(4.0, quadraticFunc.apply(2.0), PRECISION);
        assertEquals(9.0, quadraticFunc.apply(3.0), PRECISION);
        assertEquals(16.0, quadraticFunc.apply(4.0), PRECISION);

        // интерполяция (линейная между квадратичными точками)
        assertEquals(2.5, quadraticFunc.apply(1.5), PRECISION);
        assertEquals(6.5, quadraticFunc.apply(2.5), PRECISION);
        assertEquals(12.5, quadraticFunc.apply(3.5), PRECISION);

        // экстраполяция
        assertEquals(-2.0, quadraticFunc.apply(0.0), PRECISION); // слева
        assertEquals(23.0, quadraticFunc.apply(5.0), PRECISION); // справа
    }

}