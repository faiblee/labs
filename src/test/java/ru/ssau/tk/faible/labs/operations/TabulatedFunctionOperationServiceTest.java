package ru.ssau.tk.faible.labs.operations;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.faible.labs.exceptions.InconsistentFunctionsException;
import ru.ssau.tk.faible.labs.functions.ArrayTabulatedFunction;
import ru.ssau.tk.faible.labs.functions.Point;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;
import ru.ssau.tk.faible.labs.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.functions.LinkedListTabulatedFunction;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionOperationServiceTest {
    private final double PRESICION = 1e-10;
    @Test
    void testAssPoints(){
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
    @Test
    void testAdd_SameTypeFunctions() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {5.0, 15.0, 25.0};
        ArrayTabulatedFunction func2 = new ArrayTabulatedFunction(xValues2, yValues2);

        TabulatedFunction result = service.add(func1, func2);

        assertEquals(3, result.getCount());
        assertEquals(15.0, result.getY(0), PRESICION);
        assertEquals(35.0, result.getY(1), PRESICION);
        assertEquals(55.0, result.getY(2), PRESICION);
    }

    @Test
    void testAdd_DifferentTypeFunctions() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {5.0, 15.0, 25.0};
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction result = service.add(func1, func2);

        assertEquals(3, result.getCount());
        assertEquals(15.0, result.getY(0), PRESICION);
        assertEquals(35.0, result.getY(1), PRESICION);
        assertEquals(55.0, result.getY(2), PRESICION);
    }

    @Test
    void testSubtract_SameTypeFunctions() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {20.0, 30.0, 40.0};
        LinkedListTabulatedFunction func1 = new LinkedListTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {5.0, 15.0, 25.0};
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction result = service.subtract(func1, func2);

        assertEquals(3, result.getCount());
        assertEquals(15.0, result.getY(0), PRESICION);
        assertEquals(15.0, result.getY(1), PRESICION);
        assertEquals(15.0, result.getY(2), PRESICION);
    }


    @Test
    void testAdd_DifferentCountException() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0}; // Разное количество точек
        double[] yValues2 = {5.0, 15.0};
        ArrayTabulatedFunction func2 = new ArrayTabulatedFunction(xValues2, yValues2);

        assertThrows(InconsistentFunctionsException.class, () -> service.add(func1, func2));
    }

    @Test
    void testAdd_DifferentXValuesException() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.5, 3.0}; // Разные x-значения
        double[] yValues2 = {5.0, 15.0, 25.0};
        ArrayTabulatedFunction func2 = new ArrayTabulatedFunction(xValues2, yValues2);

        assertThrows(InconsistentFunctionsException.class, () -> service.add(func1, func2));
    }

    @Test
    void testMultiplication() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction func1 = new LinkedListTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {2.0, 4.0, 6.0};
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction result = service.multiplication(func1, func2);

        assertEquals(3, result.getCount());
        assertEquals(20.0, result.getY(0), PRESICION);
        assertEquals(80.0, result.getY(1), PRESICION);
        assertEquals(180.0, result.getY(2), PRESICION);
    }

    @Test
    void testDivision() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues2 = {0.0, 1.0, 2.0, 3.0};
        double[] yValues2 = {1.0, 2.0, 3.0, 4.0};
        LinkedListTabulatedFunction func1 = new LinkedListTabulatedFunction(xValues2, yValues2);

        double[] xValues1 = {0.0, 1.0, 2.0, 3.0};
        double[] yValues1 = {0.0, 10.0, 20.0, 30.0};
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(xValues1, yValues1);

        TabulatedFunction result = service.division(func1, func2);

        assertEquals(4, result.getCount());
        assertEquals(Double.POSITIVE_INFINITY, result.getY(0), PRESICION);
        assertEquals(0.2, result.getY(1), PRESICION);
        assertEquals(3.0/20.0, result.getY(2), PRESICION);
        assertEquals(4.0/30.0, result.getY(3), PRESICION);
    }


    @Test
    void testDefaultConstructor() {
        // создаем фабрику с конструктором по умолчанию
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        // проверяем, что фабрика установлена корректно
        assertNotNull(service.getFactory());
        assertInstanceOf(ArrayTabulatedFunctionFactory.class, service.getFactory());
    }
    @Test
    void testConstructorWithFactory() {

        TabulatedFunctionFactory customFactory = new LinkedListTabulatedFunctionFactory();

        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService(customFactory);

        // проверяем, что фабрика установлена корректно
        assertNotNull(service.getFactory());
        assertEquals(customFactory, service.getFactory());
        assertInstanceOf(LinkedListTabulatedFunctionFactory.class, service.getFactory());
    }
    @Test
    void testSetFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();


        TabulatedFunctionFactory originalFactory = service.getFactory();

        // создаем новую фабрику для установки
        TabulatedFunctionFactory newFactory = new LinkedListTabulatedFunctionFactory();

        // устанавливаем новую фабрику
        service.setFactory(newFactory);

        // проверяем, что фабрика изменилась
        assertEquals(newFactory, service.getFactory());
        assertNotEquals(originalFactory, service.getFactory());
        assertInstanceOf(LinkedListTabulatedFunctionFactory.class, service.getFactory());
    }


}
