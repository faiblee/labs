package ru.ssau.tk.faible.labs.functions;

import ru.ssau.tk.faible.labs.exceptions.ArrayIsNotSortedException;
import ru.ssau.tk.faible.labs.exceptions.DifferentLengthOfArraysException;

public abstract class AbstractTabulatedFunction implements TabulatedFunction {


    protected abstract int floorIndexOfX(double x);

    protected abstract double extrapolateLeft(double x);

    protected abstract double extrapolateRight(double x);

    protected abstract double interpolate(double x, int floorIndex);

    static void checkLengthIsTheSame(double[]xValues, double[] yValues){ // метод, который проверяет, одинаковая ли длина массивов
        if (xValues.length != yValues.length){
            throw new DifferentLengthOfArraysException("Длины массивов не совпадают"); // выбрасываем исключение
        }
    }
    static void checkSorted(double[] xValues){ // метод, который проверяет массив на отсортированность
        for(int j = 1; j < xValues.length; j++){
            if (xValues[j] <= xValues[j-1]){
                throw new ArrayIsNotSortedException("Массив не отсортирован");
            }

        }
    }

    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        if (leftX == rightX) {
            throw new IllegalArgumentException("Левая и правая границы интервала не могут совпадать");
        }

        return leftY + (rightY - leftY) * (x - leftX) / (rightX - leftX);
    }


    @Override
    public double apply(double x) {
        int count = getCount();

        if (x < getX((0))) { // экстраполяция слева проверяется первой
            return extrapolateLeft(x);
        }

        // Проверяем, есть ли точное совпадение
        int exactIndex = indexOfX(x);
        if (exactIndex != -1) {
            return getY(exactIndex);
        }

        // Определяем положение x относительно таблицы
        int floorIndex = floorIndexOfX(x);

        // Экстраполяция справа
        if (floorIndex == count) {
            return extrapolateRight(x);
        }

        // Интерполяция внутри интервала
        return interpolate(x, floorIndex);
    }
}