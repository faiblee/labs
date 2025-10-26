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

        assertEquals(10, syncFunction.getCount());
    }

    @Test
    void getX() {
        TabulatedFunction function = new LinkedListTabulatedFunction(new UnitFunction(),1,10,10);

        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(function);

        assertEquals(1, syncFunction.getX(0));
        assertEquals(3, syncFunction.getX(2));
        assertEquals(9, syncFunction.getX(8));
    }


    @Test
    void getYAndSetY() {
        TabulatedFunction function = new LinkedListTabulatedFunction(new UnitFunction(),1,10,10);

        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(function);

        assertEquals(1, syncFunction.getY(0));
        assertEquals(1, syncFunction.getY(2));

        syncFunction.setY(1,52); // меняем значение

        assertEquals(52, syncFunction.getY(1));
    }

    @Test
    void indexOfX() {
        TabulatedFunction innerFunction = new LinkedListTabulatedFunction(new UnitFunction(), 1, 10, 10);

        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(innerFunction);

        assertEquals(2, syncFunction.indexOfX(3.0));   // значение существует
        assertEquals(-1, syncFunction.indexOfX(20.0)); // значение несуществует
    }

    @Test
    void indexOfY() {
        TabulatedFunction innerFunction = new LinkedListTabulatedFunction(new UnitFunction(), 1, 3, 3);

        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(innerFunction);

        assertEquals(0, syncFunction.indexOfY(1.0));

        syncFunction.setY(1, 2.0); // меняем значение

        assertEquals(1, syncFunction.indexOfY(2.0));
    }

    @Test
    void leftBoundAndRightBound () {
        TabulatedFunction innerFunction = new LinkedListTabulatedFunction(new UnitFunction(), 3, 52, 4);

        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(innerFunction);

        assertEquals(3.0, syncFunction.leftBound());  // Левая граница
        assertEquals(52.0, syncFunction.rightBound()); // Правая граница
    }


    @Test
    void iterator() {
        TabulatedFunction innerFunction = new LinkedListTabulatedFunction(new UnitFunction(), 1, 3, 3);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(innerFunction);

        int count = 0;
        for (Point point : syncFunction) {
            assertEquals(1.0, point.y);
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    void apply() {
        TabulatedFunction innerFunction = new LinkedListTabulatedFunction(new UnitFunction(), 1, 5, 5);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(innerFunction);

        assertEquals(1.0, syncFunction.apply(3.0));
        syncFunction.setY(2, 10.0);
        assertEquals(10.0, syncFunction.apply(3.0));
    }
    @Test
    public void testDoSynchronouslyWithDoubleOperation() {
        // создаем функцию с 5 точками, где все y = 1.0
        TabulatedFunction innerFunction = new LinkedListTabulatedFunction(new UnitFunction(), 1, 5, 5);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(innerFunction);

        // создаем операцию
        SynchronizedTabulatedFunction.Operation<Double> aOperation =
                new SynchronizedTabulatedFunction.Operation<Double>() {
                    @Override
                    public Double apply(SynchronizedTabulatedFunction func) {
                        double sum = 0;

                        for (int i = 0; i < func.getCount(); i++) {
                            sum += func.getY(i);
                        }

                        return sum / func.getCount();
                    }
                };


        Double result = syncFunction.doSynchronously(aOperation);


        assertEquals(1.0, result);
    }

    @Test
    public void testDoSynchronouslyWithVoidOperation() {

        TabulatedFunction innerFunction = new LinkedListTabulatedFunction(new UnitFunction(), 1, 3, 3);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(innerFunction);

        // операция не возвращает результата
        SynchronizedTabulatedFunction.Operation<Void> multiplyOperation =
                new SynchronizedTabulatedFunction.Operation<Void>() {
                    @Override
                    public Void apply(SynchronizedTabulatedFunction func) {
                        // Умножаем все значения Y на 2
                        for (int i = 0; i < func.getCount(); i++) {
                            func.setY(i, func.getY(i) * 2);
                        }
                        // возвращаем null для Void
                        return null;
                    }
                };

        // результат должен быть null
        Void result = syncFunction.doSynchronously(multiplyOperation);
        assertNull(result);
    }

    @Test
    public void testDoSynchronouslyWithStringOperation() {
        TabulatedFunction innerFunction = new LinkedListTabulatedFunction(new UnitFunction(), 1, 3, 3);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(innerFunction);

        // Используем лямбда-выражение вместо класса
        SynchronizedTabulatedFunction.Operation<String> infoOperation =
                func -> {

                    return String.format("Points: %d, Range: [%.1f, %.1f]",
                            func.getCount(),        // читаем количество точек
                            func.leftBound(),       // читаем левую границу
                            func.rightBound());     // читаем правую границу
                };

        String result = syncFunction.doSynchronously(infoOperation);


        assertEquals("Points: 3, Range: [1,0, 3,0]", result);
    }

    @Test
    void toStringTest() {
        TabulatedFunction function = new LinkedListTabulatedFunction(new UnitFunction(), 1, 5, 5);
        SynchronizedTabulatedFunction synchronizedFunction = new SynchronizedTabulatedFunction(function);

        assertEquals("LinkedListTabulatedFunction size = 5\n[1.0; 1.0]\n[2.0; 1.0]\n[3.0; 1.0]\n[4.0; 1.0]\n[5.0; 1.0]\n", synchronizedFunction.toString());
    }

    @Test
    void getLock() {
        TabulatedFunction function = new LinkedListTabulatedFunction(new UnitFunction(), 1, 5, 5);
        SynchronizedTabulatedFunction synchronizedFunction = new SynchronizedTabulatedFunction(function);

        assertInstanceOf(Object.class, synchronizedFunction.getLock());
    }
}