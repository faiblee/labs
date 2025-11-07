package ru.ssau.tk.faible.labs.functions;


public class ConstantFunction implements MathFunction {
    private final double constant; // константное значение

    public ConstantFunction(double constant) { // конструктор
        this.constant = constant;
    }

    @Override
    public double apply(double x) {
        return constant; // значение, которое всегда будет возвращаться методом apply
    }

    public double getConstant() { // геттер
        return constant;
    }
}