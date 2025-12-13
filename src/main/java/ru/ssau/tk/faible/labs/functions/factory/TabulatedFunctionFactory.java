package ru.ssau.tk.faible.labs.functions.factory;

import ru.ssau.tk.faible.labs.functions.MathFunction;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;

public interface TabulatedFunctionFactory {
    TabulatedFunction create(double[] xValues, double[] yValues);
    TabulatedFunction create(MathFunction source, double xFrom, double xTo, int count);
}