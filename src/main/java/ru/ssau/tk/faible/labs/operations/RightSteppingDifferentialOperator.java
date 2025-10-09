package ru.ssau.tk.faible.labs.operations;

import ru.ssau.tk.faible.labs.functions.MathFunction;

public class RightSteppingDifferentialOperator extends SteppingDifferentialOperator {

    public RightSteppingDifferentialOperator(double step) {
        super(step); // вызов конструктора родительского класса
    }

    @Override
    public MathFunction derive(MathFunction function) {
        return new MathFunction() {
            @Override
            public double apply(double x) {
                // правая разностная производная
                return (function.apply(x + step) - function.apply(x)) / step;
            }
        };
    }
}
