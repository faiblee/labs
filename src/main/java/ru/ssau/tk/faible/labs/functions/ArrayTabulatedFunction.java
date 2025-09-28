package ru.ssau.tk.faible.labs.functions;

import java.util.Arrays;


public class ArrayTabulatedFunction extends AbstractTabulatedFunction {

    private final double[] xValues; // массив значений аргумента
    private final double[] yValues; // массив значений функции
    private final int count; // количсетво точек


    public ArrayTabulatedFunction(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) { // проверка на длины массивов (должныбыть одинаковой длины)
            throw new IllegalArgumentException("Массивы x и y должны быть одинаковой длины");
        }
        if (xValues.length == 0) { // массивы не должны быть пустыми
            throw new IllegalArgumentException("Массивы не могут быть пустыми");
        }

        // проверяем упорядоченность xValues
        for (int i = 1; i < xValues.length; i++) {
            if (xValues[i] <= xValues[i - 1]) {
                throw new IllegalArgumentException("значения x должны быть строго возрастающими");
            }
        }

        this.count = xValues.length;
        this.xValues = Arrays.copyOf(xValues, count);
        this.yValues = Arrays.copyOf(yValues, count);
    }

    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        if (count < 2) { // проверка минимального количества точек
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        this.count = count;
        this.xValues = new double[count];
        this.yValues = new double[count];

        // если xFrom > xTo, меняем местами xFrom и xTo
        if (xFrom > xTo) {
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        // если границы совпадают
        if (xFrom == xTo) {
            Arrays.fill(xValues, xFrom);
            double yValue = source.apply(xFrom);
            Arrays.fill(yValues, yValue);
        } else {
            // равномерная дискретизация
            double step = (xTo - xFrom) / (count - 1);

            for (int i = 0; i < count; i++) {
                xValues[i] = xFrom + i * step;
                yValues[i] = source.apply(xValues[i]);
            }
        }
    }

    @Override
    public int getCount() {
        return count; //
    }

    @Override // метод проверки границ x
    public double getX(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Индекс вне диапазона: " + index);
        }
        return xValues[index];
    }

    @Override // метод проверки границ y
    public double getY(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Индекс вне диапазона: " + index);
        }
        return yValues[index];
    }

    @Override
    public void setY(int index, double value) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Индекс вне диапазона: " + index);
        }
        yValues[index] = value;
    }

    @Override
    public int indexOfX(double x) {
        for (int i = 0; i < count; i++) {
            if (xValues[i] == x) {
                return i;
            }
        }
        return -1; // если элемент не найден, возвращаем -1
    }

    @Override
    public int indexOfY(double y) {
        for (int i = 0; i < count; i++) {
            if (yValues[i] == y) {
                return i;
            }
        }
        return -1; // если элемент не найден, возвращаем -1
    }

    @Override
    public double leftBound() {
        return xValues[0];
    }

    @Override
    public double rightBound() {
        return xValues[count - 1];
    }

    @Override
    protected int floorIndexOfX(double x) {
        if (x < getX(0)) { // если x меньше первого элемента => меньше всех
            return 0;
        }
        if (x > getX(count - 1)) { // если x больше всех элементов
            return count;
        }
        for (int i = 0; i < count - 1; i++) {
            double currentX = getX(i); // текущий х
            double nextX = getX(i + 1); // следующий х

            if (Math.abs(currentX-x) < 1e-10 ) { // если нашли точное совпадение
                return i; // возвращаем его индекс
            }

            if (currentX < x && x < nextX) { // если x находится в интервале (currentX, nextX)
                return i; // возвращаем currentX
            }
        }
        return count - 1;
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        if (count == 1) return getY(0);
        return interpolate(x, getX(floorIndex), getX(floorIndex+1), getY(floorIndex), getY(floorIndex + 1));
    }

    @Override
    protected double extrapolateLeft(double x) { // интерполяция от самого левого промежутка
        if (count == 1) return getY(0);
        return interpolate(x, getX(0), getX(1), getY(0), getY(1));
    }

    @Override
    protected double extrapolateRight(double x) { // интерполяция от самого правого промежутка
        if (count == 1) return getY(0);
        return interpolate(x, getX(count-2), getX(count-1), getY(count-2), getY(count-1));
    }
}