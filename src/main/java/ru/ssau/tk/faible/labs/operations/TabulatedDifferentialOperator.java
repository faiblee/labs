package ru.ssau.tk.faible.labs.operations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk.faible.labs.concurrent.SynchronizedTabulatedFunction;
import ru.ssau.tk.faible.labs.functions.Point;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;
import ru.ssau.tk.faible.labs.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.functions.factory.TabulatedFunctionFactory;

public class TabulatedDifferentialOperator implements DifferentialOperator<TabulatedFunction> {
    private static final Logger log = LoggerFactory.getLogger(TabulatedDifferentialOperator.class);

    private TabulatedFunctionFactory factory;

    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public TabulatedDifferentialOperator(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public TabulatedDifferentialOperator() {
        factory = new ArrayTabulatedFunctionFactory();
    }

    @Override
    public TabulatedFunction derive(TabulatedFunction function) {
        log.info("Метод derive запущен");
        Point[] points = TabulatedFunctionOperationService.asPoints(function);
        int count = points.length;
        double[] xValues = new double[count];
        double[] yValues = new double[count];

        for (int i = 0; i < count; i++) { // заполняем значения xValues, они остаются прежними
            xValues[i] = points[i].x;
        }

        for (int i = 0; i < count; i++) {
            if (i < count - 1) { // для всех точек кроме последней
                // Правая разностная производная
                double x_i = points[i].x;
                double x_i_plus_1 = points[i + 1].x;
                double y_i = points[i].y;
                double y_i_plus_1 = points[i + 1].y;

                yValues[i] = (y_i_plus_1 - y_i) / (x_i_plus_1 - x_i);
            } else { // последняя точка
                // Левая разностная производная
                double x_i = points[i].x;
                double x_i_minus_1 = points[i - 1].x;
                double y_i = points[i].y;
                double y_i_minus_1 = points[i - 1].y;

                yValues[i] = (y_i - y_i_minus_1) / (x_i - x_i_minus_1);
            }
        }

        log.info("Метод derive завершен");
        return factory.create(xValues, yValues);
    }
    public TabulatedFunction deriveSynchronously(TabulatedFunction function) {
        // проверка, что функция является уже синхронизированной
        SynchronizedTabulatedFunction syncFunction;
        if (function instanceof SynchronizedTabulatedFunction){
            syncFunction = (SynchronizedTabulatedFunction) function;
        } else{
            syncFunction = new SynchronizedTabulatedFunction(function);
        }
        return syncFunction.doSynchronously(this::derive); // ссылка на метод
    }

}
