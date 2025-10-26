package ru.ssau.tk.faible.labs.concurrent;
import ru.ssau.tk.faible.labs.functions.Point;

import java.util.Iterator;
import java.util.NoSuchElementException;

import ru.ssau.tk.faible.labs.functions.TabulatedFunction;
import ru.ssau.tk.faible.labs.operations.TabulatedFunctionOperationService;

public class SynchronizedTabulatedFunction implements TabulatedFunction {
    private final TabulatedFunction function;
    private final Object lock = new Object();

    @FunctionalInterface // внутренний публичный интерфейс
    public interface Operation<T>{
        T apply(SynchronizedTabulatedFunction function);
    }

    // конструктор
    public SynchronizedTabulatedFunction(TabulatedFunction function){
        this.function = function;
    }

    public <T> T doSynchronously(Operation<? extends T> operation){
        synchronized (lock){
            return operation.apply(this);
        }

    }


    @Override
    public int getCount() {
        synchronized (lock) {
            return function.getCount();
        }
    }

    @Override
    public double getX(int index) {
        synchronized (lock) {
            return function.getX(index);
        }
    }

    @Override
    public double getY(int index) {
        synchronized (lock) {
            return function.getY(index);
        }
    }

    @Override
    public void setY(int index, double value) {
        synchronized (lock) {
            function.setY(index, value);
        }
    }

    @Override
    public int indexOfX(double x) {
        synchronized (lock) {
            return function.indexOfX(x);
        }
    }

    @Override
    public int indexOfY(double y) {
        synchronized (lock) {
            return function.indexOfY(y);
        }
    }

    @Override
    public double leftBound() {
        synchronized (lock) {
            return function.leftBound();
        }
    }

    @Override
    public double rightBound() {
        synchronized (lock) {
            return function.rightBound();
        }
    }

    @Override
    public Iterator<Point> iterator() {
        synchronized (lock) {
            Point[] points = TabulatedFunctionOperationService.asPoints(function); // создаем копию точек из функции
            return new Iterator<Point>() { // возвращаем анонимный итератор
                private int currentIndex = 0;

                @Override
                public boolean hasNext() {
                    return currentIndex < points.length;
                }

                @Override
                public Point next() {
                    if (!hasNext()) throw new NoSuchElementException("В таблице не осталось элементов");
                    return points[currentIndex++];
                }
            };
        }
    }

    @Override
    public double apply(double x) {
        synchronized (lock) {
            return function.apply(x);
        }
    }

    public Object getLock() {
        return lock;
    }

    @Override
    public String toString() {
        synchronized (lock) {
            return function.toString();
        }
    }
}
