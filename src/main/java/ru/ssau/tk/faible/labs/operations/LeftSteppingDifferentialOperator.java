package ru.ssau.tk.faible.labs.operations;

import ru.ssau.tk.faible.labs.functions.MathFunction;

public class LeftSteppingDifferentialOperator extends SteppingDifferentialOperator{

    public LeftSteppingDifferentialOperator(double step){ // конструктор
        super(step); // вызов конструктора родительского класса
    }

    @Override
    public MathFunction derive (MathFunction function) {
        return new MathFunction() {
            @Override
            public double apply(double x){
                // левая разностная производная
                return (function.apply(x) - function.apply(x - step)) / step;

            }
        };

    }

}
