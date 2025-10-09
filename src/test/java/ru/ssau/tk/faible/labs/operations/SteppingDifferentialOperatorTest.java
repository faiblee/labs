package ru.ssau.tk.faible.labs.operations;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.faible.labs.functions.MathFunction;
import ru.ssau.tk.faible.labs.functions.SqrFunction;

import static org.junit.jupiter.api.Assertions.*;

class SteppingDifferentialOperatorTest {
    @Test
    void testLeftSteppingDifferentialOperator() {
        // создаем оператор с шагом 0.1
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.1);

        // тестируем на функции f(x) = x² (производная: 2x)
        SqrFunction sqrFunction = new SqrFunction();
        MathFunction derivative = operator.derive(sqrFunction);

        // проверяем производную в точке x=2
        double result = derivative.apply(2.0);
        assertEquals(3.9, result, 0.1);

        // проверка геттер шага
        assertEquals(0.1, operator.getStep(), 1e-10);
    }

    @Test
    void testRightSteppingDifferentialOperator() {
        // создаем оператор с шагом 0.1
        RightSteppingDifferentialOperator operator = new RightSteppingDifferentialOperator(0.1);

        // тестируем на функции f(x) = x^2
        SqrFunction sqrFunction = new SqrFunction();
        MathFunction derivative = operator.derive(sqrFunction);

        // проверяем производную в точке x=2
        double result = derivative.apply(2.0);
        assertEquals(4.1, result, 0.1); // (4.41 - 4)/0.1 = 4.1
    }

    @Test
    void testInvalidStep() {
        // проверка исключения для неккоректных шагов
        assertThrows(IllegalArgumentException.class, () -> new LeftSteppingDifferentialOperator(0));
        assertThrows(IllegalArgumentException.class, () -> new LeftSteppingDifferentialOperator(-1));
        assertThrows(IllegalArgumentException.class, () -> new LeftSteppingDifferentialOperator(Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> new LeftSteppingDifferentialOperator(Double.NaN));
    }

    @Test
    void testSetter() {
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.1);

        // проверка исключений
        assertThrows(IllegalArgumentException.class, () -> operator.setStep(0));
        assertThrows(IllegalArgumentException.class, () -> operator.setStep(-0.5));
        assertThrows(IllegalArgumentException.class, () -> operator.setStep(Double.NaN));

        // проверяем корректное изменение шага
        operator.setStep(0.5);
        assertEquals(0.5, operator.getStep(), 1e-10);
    }

}