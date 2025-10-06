package ru.ssau.tk.faible.labs.operations;

import ru.ssau.tk.faible.labs.functions.MathFunction;

public interface DifferentialOperator<T extends MathFunction> {
    T derive(T function);
}
