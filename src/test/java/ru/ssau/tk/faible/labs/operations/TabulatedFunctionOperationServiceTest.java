package ru.ssau.tk.faible.labs.operations;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.faible.labs.functions.ArrayTabulatedFunction;
import ru.ssau.tk.faible.labs.functions.Point;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionOperationServiceTest {
    private final double PRESICION = 1e-10;
    @Test
    void testAsPoints(){
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);
        assertEquals(3, points.length,PRESICION);
        assertEquals(1.0, points[0].x,PRESICION);
        assertEquals(10.0, points[0].y,PRESICION);
        assertEquals(2.0, points[1].x,PRESICION);
        assertEquals(20.0, points[1].y,PRESICION);
        assertEquals(3.0, points[2].x,PRESICION);
        assertEquals(30.0, points[2].y,PRESICION);
    }
    @Test
    void testAsPoints2(){
        double[] xValues = {5.0};
        double[] yValues = {52.0};
        assertThrows(IllegalArgumentException.class, ()-> new ArrayTabulatedFunction(xValues,yValues));
    }
    @Test
    void testAsPoints_WithNegativeValues() {
        double[] xValues = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] yValues = {4.0, 1.0, 0.0, 1.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(5, points.length);
        assertEquals(-2.0, points[0].x, PRESICION);
        assertEquals(4.0, points[0].y, PRESICION);
        assertEquals(2.0, points[4].x, PRESICION);
        assertEquals(4.0, points[4].y, PRESICION);
    }
}
