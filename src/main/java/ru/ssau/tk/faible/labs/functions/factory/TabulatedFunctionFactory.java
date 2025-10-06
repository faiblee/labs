package ru.ssau.tk.faible.labs.functions.factory;

import ru.ssau.tk.faible.labs.functions.TabulatedFunction;

public interface TabulatedFunctionFactory {
    public TabulatedFunction create(double[] xValues, double[] yValues);
}
