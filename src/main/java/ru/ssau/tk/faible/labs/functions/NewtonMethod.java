package ru.ssau.tk.faible.labs.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewtonMethod {
    private static final Logger log = LoggerFactory.getLogger(NewtonMethod.class);

    private static final double DEFAULT_TOLERANCE = 1e-10; // точность решения
    private static final int DEFAULT_MAX_ITERATIONS = 1000; // максимальное количество итераций

    private final MathFunction function; // функция, для которой ищется корень
    private final double initialGuess; // начальное приближение корня


    public NewtonMethod(MathFunction function, double initialGuess) {
        if (function == null) {
            log.error("В конструктор NewtonMethod передана null функция");
            throw new IllegalArgumentException("Функция не может быть null");
        }
        this.function = function;
        this.initialGuess = initialGuess;
    }

    public double solve() {

        MathFunction derivative = createDerivative(function); // создаем численную производную функции
        double x = initialGuess;

        // итерационный процесс метода Ньютона
        for (int i = 0; i < DEFAULT_MAX_ITERATIONS; i++) {

            // Вычисляем значение функции и производной в текущей точке
            double fx = function.apply(x);
            double fpx = derivative.apply(x);

            // проверка на то, чтобы производная не была равна нулю, иначе будет деление на 0
            if (Math.abs(fpx) < DEFAULT_TOLERANCE) {
                log.error("Производная функции близка к нулю");
                throw new IllegalArgumentException("Производная близка к нулю");
            }

            double xNew = x - fx / fpx; // формула Ньютона

            // проверка на условие сходимости
            if (Math.abs(xNew - x) < DEFAULT_TOLERANCE) {
                return xNew; // корень найден
            }

            x = xNew; // переходим к следующей итерации
        }

        // если достигнуто максимальное число итераций без сходимости, то выбрасываем исключение
        log.error("Метод не сошелся");
        throw new IllegalArgumentException("Метод не сошелся");
    }


    // создаем численную производную функции с помощью формулы центральной разности
    private MathFunction createDerivative(MathFunction function) {
        final double h = 1e-8; // шаг дифференцирования

        // вычисляем производную по формуле центральной разности
        return x -> (function.apply(x + h) - function.apply(x - h)) / (2 * h);
    }
}