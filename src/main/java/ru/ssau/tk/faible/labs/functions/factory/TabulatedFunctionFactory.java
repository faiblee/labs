package ru.ssau.tk.faible.labs.functions.factory;

import ru.ssau.tk.faible.labs.functions.TabulatedFunction;

public interface TabulatedFunctionFactory {
    TabulatedFunction create(double[] xValues, double[] yValues);
}
