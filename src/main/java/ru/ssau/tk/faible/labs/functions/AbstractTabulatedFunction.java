package ru.ssau.tk.faible.labs.functions;

public abstract class AbstractTabulatedFunction implements TabulatedFunction {


    protected abstract int floorIndexOfX(double x);

    protected abstract double extrapolateLeft(double x);

    protected abstract double extrapolateRight(double x);

    protected abstract double interpolate(double x, int floorIndex);



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