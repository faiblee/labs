package ru.ssau.tk.faible.labs.operations;

import ru.ssau.tk.faible.labs.functions.Point;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;

public class TabulatedFunctionOperationService {
    public static Point[] asPoints(TabulatedFunction tabulatedFunction){
        // создаем массив точек, размером что и функция
        Point[] points = new Point[tabulatedFunction.getCount()];

        int i = 0;
        for(Point point : tabulatedFunction){
            points[i] = point; // записываем точку в массив
            i++; // увеличиваем ндекс

        }
        return points; // возвращаем заполненный массив

    }
}
