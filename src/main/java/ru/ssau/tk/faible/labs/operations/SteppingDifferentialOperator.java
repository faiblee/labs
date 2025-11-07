package ru.ssau.tk.faible.labs.operations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk.faible.labs.functions.MathFunction;

public abstract class SteppingDifferentialOperator implements DifferentialOperator<MathFunction> {
    private static final Logger log = LoggerFactory.getLogger(SteppingDifferentialOperator.class);

    protected double step; // шаг дифференцирования

    public SteppingDifferentialOperator(double step){ // конструктор
        setStep (step);
    }
    public double getStep(){ // геттер
        return step;
    }

    public void setStep(double step){ // сеттер

        if (step <= 0) { // проверка на положительность шага
            log.error("В метод setStep передан отрицательный шаг");
            throw new IllegalArgumentException("Шаг должен быть положительным числом");
        }

        if (Double.isInfinite(step)) { // проверка, что шаг не является бесконечностью
            log.error("В метод setStep передан бесконечный шаг");
            throw new IllegalArgumentException("Шаг не может быть бесконечностью");
        }

        if (Double.isNaN(step)) { // проверка, что шаг не является NaN
            log.error("В метод setStep передан NaN шаг");
            throw new IllegalArgumentException("Шаг не может быть NaN");
        }

        this.step = step;
    }

    // абстрактный метод для вычисления производной функции
    @Override
    public abstract MathFunction derive(MathFunction function);

}
