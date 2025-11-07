package ru.ssau.tk.faible.labs.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompositeFunction implements MathFunction{
    private final MathFunction firstFunction;
    private final MathFunction secondFunction;

    private static final Logger log = LoggerFactory.getLogger(CompositeFunction.class);


    CompositeFunction(MathFunction func1, MathFunction func2) {
        if (func1 == null || func2 == null) {
            log.error("В конструктор CompositeFunction передана null функция");
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
