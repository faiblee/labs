package ru.ssau.tk.faible.labs.operations;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;
import ru.ssau.tk.faible.labs.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.functions.factory.TabulatedFunctionFactory;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedDifferentialOperatorTest {

    private final static double PRECISION = 1e-6;

    @Test
    void deriveLinearFunctionArrayFunctionTest() {
        // f(x) = 3x + 5, f'(x) = 3
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);

        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {8.0, 11.0, 14.0, 17.0}; // 3x + 5

        TabulatedFunction function = factory.create(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertEquals(4, derivative.getCount());

        // производная во всех точках должна быть константой 2
        for (int i = 0; i < derivative.getCount(); i++) {
            assertEquals(3.0, derivative.getY(i), PRECISION);
        }
    }

    @Test
    void deriveQuadraticFunctionLinkedListFunctionTest() {
        // f(x) = x^2, f'(x) = 2x
        // но точного совпадения не будет в связи с особенностями метода численного дифференцирования (погрешность)
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);

        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0}; // x^2

        TabulatedFunction function = factory.create(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertEquals(4, derivative.getCount());

        // правые разностные производные
        assertEquals(3.0, derivative.getY(0), PRECISION);  // (4-1)/(2-1) = 3
        assertEquals(5.0, derivative.getY(1), PRECISION);  // (9-4)/(3-2) = 5
        assertEquals(7.0, derivative.getY(2), PRECISION);  // (16-9)/(4-3) = 7

        // левая разностная производная для последней точки
        assertEquals(7.0, derivative.getY(3), PRECISION);  // (16-9)/(4-3) = 7
    }

    @Test
    void getterAndSetterFactoryTest() {
        TabulatedFunctionFactory factoryLinkedList = new LinkedListTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        // проверка конструктора без параметров и геттера
        assertInstanceOf(ArrayTabulatedFunctionFactory.class, operator.getFactory());
        // проверка сеттера
        operator.setFactory(factoryLinkedList);
        // проверка соответствия фабрики после изменения сеттером
        assertInstanceOf(LinkedListTabulatedFunctionFactory.class, operator.getFactory());

    }
}