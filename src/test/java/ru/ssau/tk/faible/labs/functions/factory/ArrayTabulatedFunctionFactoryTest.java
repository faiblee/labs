package ru.ssau.tk.faible.labs.functions.factory;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.faible.labs.functions.ArrayTabulatedFunction;
import ru.ssau.tk.faible.labs.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTabulatedFunctionFactoryTest {
    private final double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
    private final double[] yValues = {10.0, 20.0, 30.0, 40.0, 50.0};

    @Test
    void LinkedListFactoryTest() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();

        TabulatedFunction function = factory.create(xValues, yValues);

        assertInstanceOf(ArrayTabulatedFunction.class, function);
    }
}