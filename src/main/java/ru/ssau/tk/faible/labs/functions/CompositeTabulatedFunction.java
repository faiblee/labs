package ru.ssau.tk.faible.labs.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class CompositeTabulatedFunction implements TabulatedFunction {

    private static final Logger log = LoggerFactory.getLogger(CompositeTabulatedFunction.class);

    private final TabulatedFunction inner;   // g(x)
    private final TabulatedFunction outer;   // f(x)

    public CompositeTabulatedFunction(TabulatedFunction inner, TabulatedFunction outer) {
        if (inner == null || outer == null) {
            log.error("В конструктор CompositeTabulatedFunction передана null функция");
            throw new IllegalArgumentException("TabulatedFunction arguments cannot be null");
        }
        this.inner = inner;
        this.outer = outer;
    }

    @Override
    public double apply(double x) {
        double intermediate = inner.apply(x);
        return outer.apply(intermediate);
    }

    // === Реализация TabulatedFunction ===

    @Override
    public int getCount() {
        // Используем сетку X из ВНЕШНЕЙ функции (outer)
        // Это определяет, где мы будем вычислять f(g(x))
        return outer.getCount();
    }

    @Override
    public double getX(int index) {
        return outer.getX(index);
    }

    @Override
    public double getY(int index) {
        double x = getX(index);
        return apply(x); // = outer.apply(inner.apply(x))
    }

    @Override
    public void setY(int index, double value) {
        throw new UnsupportedOperationException("CompositeTabulatedFunction is immutable");
    }

    @Override
    public int indexOfX(double x) {
        return outer.indexOfX(x);
    }

    @Override
    public int indexOfY(double y) {
        // Поиск по вычисленным значениям — неэффективно и неоднозначно
        // Лучше не поддерживать или перебирать
        for (int i = 0; i < getCount(); i++) {
            if (Double.compare(getY(i), y) == 0) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public double leftBound() {
        return outer.leftBound();
    }

    @Override
    public double rightBound() {
        return outer.rightBound();
    }

    @Override
    public Iterator<Point> iterator() {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < getCount();
            }

            @Override
            public Point next() {
                double x = getX(index);
                double y = getY(index);
                index++;
                return new Point(x, y);
            }
        };
    }

    // === Composition chaining (опционально) ===

    public CompositeTabulatedFunction andThen(TabulatedFunction next) {
        return new CompositeTabulatedFunction(this, next);
    }

    public CompositeTabulatedFunction compose(TabulatedFunction before) {
        return new CompositeTabulatedFunction(before, this);
    }
}