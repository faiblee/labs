package ru.ssau.tk.faible.labs.functions;

public class CompositeFunction implements MathFunction{
    private final MathFunction firstFunction;
    private final MathFunction secondFunction;

    CompositeFunction(MathFunction func1, MathFunction func2) {
        if (func1 == null || func2 == null) {
            throw new IllegalArgumentException("Functions cannot be null");
        }

        firstFunction = func1;
        secondFunction = func2;
    }

    @Override
    public double apply(double x) {
        double intermediateValue = firstFunction.apply(x);
        return secondFunction.apply(intermediateValue);
    }
}
