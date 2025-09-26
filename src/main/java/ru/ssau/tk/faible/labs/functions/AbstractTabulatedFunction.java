package ru.ssau.tk.faible.labs.functions;

/**
 * Абстрактный класс для табличных функций, реализующий общую логику интерполяции и экстраполяции.
 * Реализует интерфейс TabulatedFunction и предоставляет готовую реализацию метода apply().
 */
public abstract class AbstractTabulatedFunction implements TabulatedFunction {


    protected abstract int floorIndexOfX(double x);

    protected abstract double extrapolateLeft(double x);

    protected  abstract double extrapolateRight(double x);

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

        // Если точек нет
        if (count == 0) {
            throw new IllegalStateException("Табличная функция не содержит точек");
        }

        // Если только одна точка
        if (count == 1) {
            return getY(0); // Всегда возвращаем единственное значение
        }

        // Проверяем, есть ли точное совпадение
        int exactIndex = indexOfX(x);
        if (exactIndex != -1) {
            return getY(exactIndex);
        }

        // Определяем положение x относительно таблицы
        int floorIndex = floorIndexOfX(x);

        // Экстраполяция слева
        if (floorIndex == 0) {
            return extrapolateLeft(x);
        }

        // Экстраполяция справа
        if (floorIndex == count) {
            return extrapolateRight(x);
        }

        // Интерполяция внутри интервала
        return interpolate(x, floorIndex);
    }
}