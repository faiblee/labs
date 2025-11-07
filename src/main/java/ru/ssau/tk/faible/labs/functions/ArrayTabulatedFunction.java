package ru.ssau.tk.faible.labs.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk.faible.labs.exceptions.InterpolationException;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;


public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable, Serializable {

    private static final Logger log = LoggerFactory.getLogger(ArrayTabulatedFunction.class);

    @Serial
    private static final long serialVersionUID = -6906250891256385040L;
    private double[] xValues; // массив значений аргумента
    private double[] yValues; // массив значений функции
    private int count; // количество точек


    public ArrayTabulatedFunction(double[] xValues, double[] yValues) {
        AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues);
        if (xValues.length < 2) {
            log.error("В конструктор ArrayTabulatedFunction передан массив длины < 2");
            throw new IllegalArgumentException("Длина массива не может быть меньше 2");
        }
        AbstractTabulatedFunction.checkSorted(xValues);


        this.count = xValues.length;
        this.xValues = Arrays.copyOf(xValues, count);
        this.yValues = Arrays.copyOf(yValues, count);
    }

    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        if (count < 2) { // проверка минимального количества точек
            log.error("В конструктор ArrayTabulatedFunction передано количество точек < 2");
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
        if (Math.abs(xFrom - xTo) <= 1e-10) {
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
    public Iterator<Point> iterator() {
        return new Iterator<Point>() { // анонимный класс
            public int i = 0;

            @Override
            public boolean hasNext() {
                return (i < count); // след элем сущ, если индекс меньше кол-ва
            }

            @Override
            public Point next() {
                if (!hasNext()) { // если нет элементов
                    log.error("Вызван метод next() когда элементы закончились");
                    throw new NoSuchElementException(); // бросаем исключение
                }
                Point point = new Point(getX(i), getY(i)); // создаем точку из массиов
                i++;
                return point;
            }
        };
    }

    @Override
    public void insert(double x, double y) {
        int index = indexOfX(x);
        if (index != -1) { // если элемент существует
            yValues[index] = y;
        } else { // если элемента нет
            int insertIndex = 0;
            while (insertIndex < count && xValues[insertIndex] < x) { // находим индекс для вставки
                insertIndex++;
            }
            double[] newXValues = new double[count + 1];
            double[] newYValues = new double[count + 1];
            // копируем элементы до insertIndex
            if (insertIndex > 0) {
                System.arraycopy(xValues, 0, newXValues, 0, insertIndex);
                System.arraycopy(yValues, 0, newYValues, 0, insertIndex);
            }

            // вставляем новый элемент
            newXValues[insertIndex] = x;
            newYValues[insertIndex] = y;

            // копируем оставшиеся элементы
            if (insertIndex < count) {
                System.arraycopy(xValues, insertIndex, newXValues, insertIndex + 1, count - insertIndex);
                System.arraycopy(yValues, insertIndex, newYValues, insertIndex + 1, count - insertIndex);
            }

            xValues = newXValues; // меняем ссылки в массивах
            yValues = newYValues;
            count++; // увеличиваем размерность массива
        }
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override // метод проверки границ x
    public double getX(int index) {
        if (index < 0 || index >= count) {
            log.error("Вызван метод getX для некорректного индекса");
            throw new IllegalArgumentException("Индекс вне диапазона: " + index);
        }
        return xValues[index];
    }

    @Override // метод проверки границ y
    public double getY(int index) {
        if (index < 0 || index >= count) {
            log.error("Вызван метод getY для некорректного индекса");
            throw new IllegalArgumentException("Индекс вне диапазона: " + index);
        }
        return yValues[index];
    }

    @Override
    public void setY(int index, double value) {
        if (index < 0 || index >= count) {
            log.error("Вызван метод setY для некорректного индекса");
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
    public void remove(int index) {
        if (index < 0 || index >= count) {
            log.error("Вызван метод remove для некорректного индекса");
            throw new IllegalArgumentException("Индекс вне диапазона: " + index);
        }
        if (count == 1) {
            log.error("Вызван метод remove для последнего элемента");
            throw new IllegalStateException("Нельзя удалить последнюю точку");
        }

        // создаем новые массивы уменьшенного размера
        double[] newXValues = new double[count - 1];
        double[] newYValues = new double[count - 1];

        // копируем элементы до удаляемого индекса
        System.arraycopy(xValues, 0, newXValues, 0, index);
        System.arraycopy(yValues, 0, newYValues, 0, index);

        //  копируем элементы после удаляемого индекса
        System.arraycopy(xValues, index + 1, newXValues, index, count - index - 1);
        System.arraycopy(yValues, index + 1, newYValues, index, count - index - 1);

        // обновляем ссылки и счетчик
        this.xValues = newXValues;
        this.yValues = newYValues;
        this.count--;
    }

    @Override
    protected int floorIndexOfX(double x) {
        if (x < getX(0)) { // если x меньше первого элемента
            log.error("вызван метод floorIndexOfX для некорректного x (меньше левой границы)");
            throw new IllegalArgumentException("x меньше левой границы");
        }
        if (x > getX(count - 1)) { // если x больше всех элементов
            return count;
        }
        for (int i = 0; i < count - 1; i++) {
            double currentX = getX(i); // текущий х
            double nextX = getX(i + 1); // следующий х

            if (Math.abs(currentX - x) < 1e-10) { // если нашли точное совпадение
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
        // границы интервала
        double leftX = getX(floorIndex);
        double rightX = getX(floorIndex + 1);

        if (x < leftX || x > rightX) {
            log.error("вызван метод interpolate для некорректного x (вне зоны интерполяции)");
            throw new InterpolationException("x вне зоны интерполяции"); // бросаем исключение
        }

        return interpolate(x, getX(floorIndex), getX(floorIndex + 1), getY(floorIndex), getY(floorIndex + 1));
    }

    @Override
    protected double extrapolateLeft(double x) { // интерполяция от самого левого промежутка
        if (count == 1) {
            return getY(0);
        }
        return interpolate(x, getX(0), getX(1), getY(0), getY(1));
    }

    @Override
    protected double extrapolateRight(double x) { // интерполяция от самого правого промежутка
        if (count == 1) {
            return getY(0);
        }
        return interpolate(x, getX(count - 2), getX(count - 1), getY(count - 2), getY(count - 1));
    }
}