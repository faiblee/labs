package ru.ssau.tk.faible.labs.concurrent;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.faible.labs.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.faible.labs.functions.Point;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;
import ru.ssau.tk.faible.labs.functions.UnitFunction;

import static org.junit.jupiter.api.Assertions.*;

class SynchronizedTabulatedFunctionTest {

    @Test
    void getCount() {
        TabulatedFunction function = new LinkedListTabulatedFunction(new UnitFunction(),1,10,10);

        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(function);

        assertEquals(syncFunction.getCount(),10);
    }

    @Test
    void getX() {
        TabulatedFunction function = new LinkedListTabulatedFunction(new UnitFunction(),1,10,10);

        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(function);

        assertEquals(syncFunction.getX(0),1);
        assertEquals(syncFunction.getX(2),3);
        assertEquals(syncFunction.getX(8),9);
    }


    @Test
    void getYAndSetY() {
        TabulatedFunction function = new LinkedListTabulatedFunction(new UnitFunction(),1,10,10);

        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(function);

        assertEquals(syncFunction.getY(0),1);
        assertEquals(syncFunction.getY(2),1);

        syncFunction.setY(1,52); // меняем значение

        assertEquals(syncFunction.getY(1),52);
    }

    @Test
    void indexOfX() {
        TabulatedFunction innerFunction = new LinkedListTabulatedFunction(new UnitFunction(), 1, 10, 10);

        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(innerFunction);

        assertEquals(syncFunction.indexOfX(3.0), 2);   // значение существует
        assertEquals(syncFunction.indexOfX(20.0), -1); // значение несуществует
    }

    @Test
    void indexOfY() {
        TabulatedFunction innerFunction = new LinkedListTabulatedFunction(new UnitFunction(), 1, 3, 3);

        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(innerFunction);

        assertEquals(syncFunction.indexOfY(1.0), 0);

        syncFunction.setY(1, 2.0); // меняем значение

        assertEquals(syncFunction.indexOfY(2.0), 1);
    }

    @Test
    void leftBoundAndRightBound () {
        TabulatedFunction innerFunction = new LinkedListTabulatedFunction(new UnitFunction(), 3, 52, 4);

        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(innerFunction);

        assertEquals(syncFunction.leftBound(), 3.0);  // Левая граница
        assertEquals(syncFunction.rightBound(), 52.0); // Правая граница
    }


    @Test
    void iterator() {
        TabulatedFunction innerFunction = new LinkedListTabulatedFunction(new UnitFunction(), 1, 3, 3);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(innerFunction);

        int count = 0;
        for (Point point : syncFunction) {
            assertEquals(point.y, 1.0);
            count++;
        }
        assertEquals(count, 3);
    }

    @Test
    void apply() {
        TabulatedFunction innerFunction = new LinkedListTabulatedFunction(new UnitFunction(), 1, 5, 5);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(innerFunction);

        assertEquals(syncFunction.apply(3.0), 1.0);
        syncFunction.setY(2, 10.0);
        assertEquals(syncFunction.apply(3.0), 10.0);
    }


}