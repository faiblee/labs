package ru.ssau.tk.faible.labs.functions;

public interface MathFunction {
    double apply(double x);

    default MathFunction andThen(MathFunction afterFunction) {
        if (afterFunction == null) {
            throw new IllegalArgumentException("Функция afterFunction не может быть null");
        }

        return new CompositeFunction(this, afterFunction);
    }
}
