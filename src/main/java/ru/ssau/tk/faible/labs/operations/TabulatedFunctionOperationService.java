package ru.ssau.tk.faible.labs.operations;

import ru.ssau.tk.faible.labs.exceptions.InconsistentFunctionsException;
import ru.ssau.tk.faible.labs.functions.Point;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;
import ru.ssau.tk.faible.labs.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.functions.factory.TabulatedFunctionFactory;

public class TabulatedFunctionOperationService {
    private TabulatedFunctionFactory factory;

    public TabulatedFunctionOperationService(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public TabulatedFunctionOperationService() {
        this.factory = new ArrayTabulatedFunctionFactory();
    }


    private interface BiOperation { // вложенный интерфейс
        double apply(double u, double v);
    }

    private TabulatedFunction doOperation(TabulatedFunction a, TabulatedFunction b, BiOperation operation) {
        if (a.getCount() != b.getCount()) {
            throw new InconsistentFunctionsException("Количество записей в первой функции не равно количеству записей во второй функции ");
        }
        Point[] pointsA = asPoints(a);
        Point[] pointsB = asPoints(b);
        int count = pointsA.length;
        double[] xValues = new double[count];
        double[] yValues = new double[count];

        for (int i = 0; i < count; i++) {
            if (Math.abs(pointsA[i].x - pointsB[i].x) > 1e-10) {
                throw new InconsistentFunctionsException("Значения x функций не совпадают");
            }
            xValues[i] = pointsA[i].x;
            yValues[i] = operation.apply(pointsA[i].y, pointsB[i].y);
        }
        return factory.create(xValues, yValues);
    }

    public TabulatedFunction add(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, ((u, v) -> u + v));
    }

    public TabulatedFunction subtract(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, ((u, v) -> u - v));
    }

    public TabulatedFunction multiplication(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, (u, v) -> u * v);
    }

    public TabulatedFunction division(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, (u, v) -> u / v);
    }

    public TabulatedFunctionFactory getFactory() {
        return this.factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }


    public static Point[] asPoints(TabulatedFunction tabulatedFunction) {
        // создаем массив точек, размером что и функция
        Point[] points = new Point[tabulatedFunction.getCount()];

        int i = 0;
        for (Point point : tabulatedFunction) {
            points[i] = point; // записываем точку в массив
            i++; // увеличиваем индекс

        }
        return points; // возвращаем заполненный массив
    }


}
