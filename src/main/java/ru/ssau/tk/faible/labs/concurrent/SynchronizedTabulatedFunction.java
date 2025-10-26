package ru.ssau.tk.faible.labs.concurrent;
import ru.ssau.tk.faible.labs.functions.Point;

import java.util.Iterator;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;

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
            return function.iterator();
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
