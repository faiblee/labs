package ru.ssau.tk.faible.labs.functions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk.faible.labs.exceptions.DifferentLengthOfArraysException;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunctionTest {
    private LinkedListTabulatedFunction function;
    private final static double PRECISION = 1e-6;

    private final double[] xValues = new double[]{1.0, 2.0, 3.0, 4.0, 5.0};
    private final double[] yValues = new double[]{2.0, 4.0, 6.0, 8.0, 10.0};

    @Test
    void firstConstructorTest() { // проверка первого конструктора, методов getCount, getX, getY
        function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(5, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(4.0, function.getX(3));
        assertEquals(8.0, function.getY(3));
    }

    @Test
    void secondConstructorTest() { // проверка второго конструктора, методов leftBound, rightBound
        MathFunction func = x -> x * x; // x^2
        function = new LinkedListTabulatedFunction(func, 0, 4, 5);

        assertEquals(5, function.getCount());
        assertEquals(0.0, function.leftBound(), PRECISION);
        assertEquals(4.0, function.rightBound(), PRECISION);
        assertEquals(1.0, function.getX(1), PRECISION);
        assertEquals(4.0, function.getY(2), PRECISION);
    }

    @Test
    void swapXFromXToTest() { // смена местами xFrom и xTo прр xTo < xFrom
        MathFunction source = x -> x + 1;
        function = new LinkedListTabulatedFunction(source, 5, 1, 3);

        assertEquals(1.0, function.getX(0), PRECISION);
        assertEquals(3.0, function.getX(1), PRECISION);
        assertEquals(5.0, function.getX(2), PRECISION);
    }

    @Test
    void EqualsXFromXToTest() { // xFrom = xTo
        MathFunction source = Math::sin;
        function = new LinkedListTabulatedFunction(source, Math.PI, Math.PI, 4);

        assertEquals(4, function.getCount());
        for (int i = 0; i < 4; i++) { // все четыре x должны быть равны = pi
            assertEquals(Math.PI, function.getX(i), PRECISION);
            assertEquals(Math.sin(Math.PI), function.getY(i), PRECISION);
        }
    }

    @Test
    void setYTest() { // тест setY
        function = new LinkedListTabulatedFunction(xValues, yValues);

        function.setY(2, 100.0);
        assertEquals(100.0, function.getY(2), PRECISION);

        function.setY(0, 5.0);
        assertEquals(5.0, function.getY(0), PRECISION);

        function.setY(4, 99.0);
        assertEquals(99.0, function.getY(4), PRECISION);
    }

    @Test
    void indexOfXTest() { // тест indexOfX
        function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfX(1.0));
        assertEquals(2, function.indexOfX(3.0));
        assertEquals(4, function.indexOfX(5.0));
        assertEquals(-1, function.indexOfX(0.5));
    }

    @Test
    void indexOfYTest() { // тест indexOfY
        function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfY(2.0));
        assertEquals(2, function.indexOfY(6.0));
        assertEquals(4, function.indexOfY(10.0));
        assertEquals(-1, function.indexOfY(5.0));
    }

    @Test
    void floorIndexOfXTest() {
        function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(2, function.floorIndexOfX(3.0)); // точное совпадение

        assertEquals(1, function.floorIndexOfX(2.5)); // между существующими
        assertEquals(3, function.floorIndexOfX(4.7));

        assertEquals(5, function.floorIndexOfX(6.0)); // больше всех

    }

    @Test
    void interpolateTest() {
        function = new LinkedListTabulatedFunction(xValues, yValues);

        double result = function.interpolate(1.5, 0);
        assertEquals(3.0, result, PRECISION); // 2 + (4-2)*(1.5-1)/(2-1) = 3.0

        result = function.interpolate(2.5, 1);
        assertEquals(5.0, result, PRECISION); // 4 + (6-4)*(2.5-2)/(3-2) = 5.0

        result = function.interpolate(4.5, 3);
        assertEquals(9.0, result, PRECISION); // 8 + (10-8)*(4.5-4)/(5-4) = 9.0
    }

    @Test
    void extrapolateLeftTest() { // экстраполяция слева
        function = new LinkedListTabulatedFunction(xValues, yValues);

        double result = function.extrapolateLeft(0.0);
        assertEquals(0.0, result, PRECISION); // 2 + (4-2)*(0-1)/(2-1) = 0.0

        result = function.extrapolateLeft(0.5);
        assertEquals(1.0, result, PRECISION); // 2 + (4-2)*(0.5-1)/(2-1) = 1.0

        result = function.extrapolateLeft(-1.0);
        assertEquals(-2.0, result, PRECISION); // 2 + (4-2)*(-1-1)/(2-1) = -2.0
    }

    @Test
    void extrapolateRightTest() { // экстраполяция справа
        function = new LinkedListTabulatedFunction(xValues, yValues);

        double result = function.extrapolateRight(6.0);
        assertEquals(12.0, result, PRECISION); // 8 + (10-8)*(6-4)/(5-4) = 12.0

        result = function.extrapolateRight(5.5);
        assertEquals(11.0, result, PRECISION); // 8 + (10-8)*(5.5-4)/(5-4) = 11.0

        result = function.extrapolateRight(7.0);
        assertEquals(14.0, result, PRECISION); // 8 + (10-8)*(7-4)/(5-4) = 14.0
    }

    @Test
    void interpolateWithCoordinatesTest() { // интерполяция по координатам
        function = new LinkedListTabulatedFunction(xValues, yValues);

        double result = function.interpolate(2.0, 1.0, 3.0, 2.0, 6.0);
        assertEquals(4.0, result, PRECISION); // 2 + (6-2)*(2-1)/(3-1) = 4.0

        result = function.interpolate(5.0, 0.0, 10.0, 0.0, 100.0);
        assertEquals(50.0, result, PRECISION); // 0 + (100-0)*(5-0)/(10-0) = 50.0

        result = function.interpolate(-1.0, -2.0, 2.0, 4.0, 10.0);
        assertEquals(5.5, result, PRECISION); // 4 + (10-4)*(-1+2)/(2+2) = 6.0
    }

    @Test
    void applyTest() { // тест для метода apply

        function = new LinkedListTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});

        // точное совпадение
        assertEquals(10.0, function.apply(1.0), PRECISION);
        assertEquals(20.0, function.apply(2.0), PRECISION);
        assertEquals(30.0, function.apply(3.0), PRECISION);

        // интерполяция
        assertEquals(15.0, function.apply(1.5), PRECISION);
        assertEquals(25.0, function.apply(2.5), PRECISION);


        // экстраполяция слева
        assertEquals(0.0, function.apply(0.0));
        assertEquals(-10.0, function.apply(-1.0));

        // экстраполяция справа
        assertEquals(60.0, function.apply(6.0), PRECISION);
        assertEquals(70.0, function.apply(7.0), PRECISION);
    }

    @Test
    void applyNonLinearFunctionTest() { // тест для нелинейной функции y=x^2
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction quadraticFunc = new LinkedListTabulatedFunction(xValues, yValues);

        // точное совпадение
        assertEquals(1.0, quadraticFunc.apply(1.0), PRECISION);
        assertEquals(4.0, quadraticFunc.apply(2.0), PRECISION);
        assertEquals(9.0, quadraticFunc.apply(3.0), PRECISION);
        assertEquals(16.0, quadraticFunc.apply(4.0), PRECISION);

        // интерполяция (линейная между квадратичными точками)
        assertEquals(2.5, quadraticFunc.apply(1.5), PRECISION);
        assertEquals(6.5, quadraticFunc.apply(2.5), PRECISION);
        assertEquals(12.5, quadraticFunc.apply(3.5), PRECISION);

        // экстраполяция
        assertEquals(-2.0, quadraticFunc.apply(0.0), PRECISION); // слева
        assertEquals(23.0, quadraticFunc.apply(5.0), PRECISION); // справа
    }

    @Test
    void insertAtBeginningTest() {
        double[] xValues = {2.0, 3.0, 4.0};
        double[] yValues = {20.0, 30.0, 40.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.insert(1.0, 10.0);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0), PRECISION);
        assertEquals(10.0, function.getY(0), PRECISION);
        assertEquals(2.0, function.getX(1), PRECISION);
        assertEquals(20.0, function.getY(1), PRECISION);
        assertEquals(1.0, function.leftBound(), PRECISION);
    }

    @Test
    void insertAtEndTest() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.insert(4.0, 40.0);

        assertEquals(4, function.getCount());
        assertEquals(3.0, function.getX(2), PRECISION);
        assertEquals(30.0, function.getY(2), PRECISION);
        assertEquals(4.0, function.getX(3), PRECISION);
        assertEquals(40.0, function.getY(3), PRECISION);
        assertEquals(4.0, function.rightBound(), PRECISION);
    }

    @Test
    void insertInMiddleTest() {
        double[] xValues = {1.0, 3.0, 5.0};
        double[] yValues = {10.0, 30.0, 50.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.insert(2.0, 20.0);
        function.insert(4.0, 40.0);

        assertEquals(5, function.getCount());
        assertEquals(1.0, function.getX(0), PRECISION);
        assertEquals(2.0, function.getX(1), PRECISION);
        assertEquals(3.0, function.getX(2), PRECISION);
        assertEquals(4.0, function.getX(3), PRECISION);
        assertEquals(5.0, function.getX(4), PRECISION);

        assertEquals(10.0, function.getY(0), PRECISION);
        assertEquals(20.0, function.getY(1), PRECISION);
        assertEquals(30.0, function.getY(2), PRECISION);
        assertEquals(40.0, function.getY(3), PRECISION);
        assertEquals(50.0, function.getY(4), PRECISION);
    }

    @Test
    void insertDuplicateXTest() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // вставляем узел с существующим x
        function.insert(2.0, 25.0);

        // количество узлов не должно измениться
        assertEquals(3, function.getCount());
        // значение y должно обновиться
        assertEquals(25.0, function.getY(1), PRECISION);
        // остальные значения должны остаться прежними
        assertEquals(10.0, function.getY(0), PRECISION);
        assertEquals(30.0, function.getY(2), PRECISION);
    }

    @Test
    void insertMultipleNodesTest() {
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // вставляем узлы в разном порядке
        function.insert(8.0, 16.0);
        function.insert(6.0, 12.0);
        function.insert(10.0, 20.0);
        function.insert(7.0, 14.0);
        function.insert(9.0, 18.0);

        assertEquals(10, function.getCount());

        // проверяем правильную сортировку по x
        assertEquals(1.0, function.getX(0), PRECISION);
        assertEquals(6.0, function.getX(5), PRECISION);
        assertEquals(7.0, function.getX(6), PRECISION);
        assertEquals(8.0, function.getX(7), PRECISION);
        assertEquals(9.0, function.getX(8), PRECISION);

        assertEquals(2.0, function.getY(0), PRECISION);
        assertEquals(12.0, function.getY(5), PRECISION);
        assertEquals(14.0, function.getY(6), PRECISION);
        assertEquals(16.0, function.getY(7), PRECISION);
        assertEquals(18.0, function.getY(8), PRECISION);
    }

    @Test
    void insertWithCircularLinksTest() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.insert(1.5, 15.0);

        // проверяем, что циклические связи сохранились через граничные значения
        assertEquals(4, function.getCount());
        assertEquals(1.0, function.leftBound(), PRECISION);
        assertEquals(3.0, function.rightBound(), PRECISION);

        // проверяем, что можно пройти по всем элементам
        for (int i = 0; i < function.getCount(); i++) {
            function.getX(i);
            function.getY(i);
        }
    }


    @Test
    void insertAndInterpolateTest() {
        double[] xValues = {1.0, 3.0};
        double[] yValues = {10.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // вставляем узел в середину
        function.insert(2.0, 20.0);

        // проверяем интерполяцию
        assertEquals(15.0, function.apply(1.5), PRECISION);
        assertEquals(25.0, function.apply(2.5), PRECISION);
    }

    @Test
    void insertAndExtrapolateTest() {
        double[] xValues = {2.0, 3.0};
        double[] yValues = {20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // вставляем узел в начало
        function.insert(1.0, 10.0);

        // проверяем экстраполяцию слева
        assertEquals(0.0, function.apply(0.0), PRECISION);

        // вставляем узел в конец
        function.insert(4.0, 40.0);

        // проверяем экстраполяцию справа
        assertEquals(50.0, function.apply(5.0), PRECISION);
    }

    @Test
    void insertNegativeValuesTest() {
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(new double[]{1, 2, 3}, new double[]{1, 2, 3});

        function.insert(-3.0, -30.0);
        function.insert(-1.0, -10.0);
        function.insert(-2.0, -20.0);

        assertEquals(6, function.getCount());
        assertEquals(-3.0, function.getX(0), PRECISION);
        assertEquals(-2.0, function.getX(1), PRECISION);
        assertEquals(-1.0, function.getX(2), PRECISION);
    }

    @BeforeEach
    void setUp() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0, 50.0};
        function = new LinkedListTabulatedFunction(xValues, yValues);
    }

    @Test
        // удаление элемента из начала списка
    void removeFirstElementTest() {
        function.remove(0);

        assertEquals(4, function.getCount());
        assertEquals(2.0, function.getX(0), PRECISION);
        assertEquals(20.0, function.getY(0), PRECISION);
        assertEquals(5.0, function.getX(3), PRECISION);
        assertEquals(50.0, function.getY(3), PRECISION);
    }

    @Test
        // удаление элемента из конца
    void removeLastElementTest() {
        function.remove(4);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0), PRECISION);
        assertEquals(10.0, function.getY(0), PRECISION);
        assertEquals(4.0, function.getX(3), PRECISION);
        assertEquals(40.0, function.getY(3), PRECISION);
    }

    @Test
        // удаление элемента из середины
    void removeMiddleElementTest() {
        function.remove(2);

        assertEquals(4, function.getCount());
        assertEquals(2.0, function.getX(1), PRECISION);
        assertEquals(4.0, function.getX(2), PRECISION);

        assertEquals(20.0, function.getY(1), PRECISION);
        assertEquals(40.0, function.getY(2), PRECISION);
    }

    @Test
        // удаление head
    void removeHeadTest() {
        double originalFirstX = function.getX(0);
        function.remove(0);

        // нулевой элемент должен измениться
        assertNotEquals(originalFirstX, function.getX(0));
        assertEquals(2.0, function.getX(0), PRECISION);
    }

    @Test
        // удаление для функции из двух элементов
    void removeShouldWorkWithTwoElements() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        LinkedListTabulatedFunction twoElementFunc = new LinkedListTabulatedFunction(xValues, yValues);

        // удаляем нулевой элемент
        twoElementFunc.remove(0);
        assertEquals(1, twoElementFunc.getCount());
        assertEquals(2.0, twoElementFunc.getX(0), PRECISION);
        assertEquals(20.0, twoElementFunc.getY(0), PRECISION);
    }

    @Test
        // несколько удалений подряд
    void removeSomeRemovalsTest() {
        // Удаляем несколько элементов подряд
        function.remove(2); // Удаляем x=3.0
        assertEquals(4, function.getCount());

        function.remove(0); // Удаляем x=1.0
        assertEquals(3, function.getCount());
        assertEquals(2.0, function.getX(0), PRECISION);

        function.remove(1); // Удаляем x=4.0 (теперь это индекс 1)
        assertEquals(2, function.getCount());
        assertEquals(2.0, function.getX(0), PRECISION);
        assertEquals(5.0, function.getX(1), PRECISION);

        function.remove(1); // Удаляем x=5.0
        assertEquals(1, function.getCount());
        assertEquals(2.0, function.getX(0), PRECISION);

        function.remove(0); // Удаляем последний элемент
        assertEquals(0, function.getCount());
    }

    @Test
    void removeAndInsertInEmptyListTest() {
        for (int i = 0; i < function.getCount(); ) {
            function.remove(i);
        }
        function.insert(0.0, 0.0);
    }

    @Test
    void FirstConstructorExceptionTest() {
        assertThrows(IllegalArgumentException.class,
                () -> new LinkedListTabulatedFunction(new double[]{}, new double[]{}));
        assertThrows(DifferentLengthOfArraysException.class,
                () -> new LinkedListTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0}));
    }

    @Test
    void SecondConstructorExceptionTest() {
        MathFunction source = (x) -> x + 1;
        assertThrows(IllegalArgumentException.class,
                () -> new LinkedListTabulatedFunction(source, 0.0, 10.0, 1));
    }

    @Test
    void getXExceptionTest() {
        // Отрицательный индекс
        assertThrows(IllegalArgumentException.class, () -> function.getX(-1));
        // Индекс больше размера
        assertThrows(IllegalArgumentException.class, () -> function.getX(10));

    }

    @Test
    void getYExceptionTest() {

        assertThrows(IllegalArgumentException.class, () -> function.getY(-1));
        assertThrows(IllegalArgumentException.class, () -> function.getY(10));
    }

    @Test
    void setYExceptionTest() {

        assertThrows(IllegalArgumentException.class, () -> function.setY(-1, 100.0));
        assertThrows(IllegalArgumentException.class, () -> function.setY(10, 100.0));
    }

    @Test
    void floorIndexOfXExceptionTest() {

        assertThrows(IllegalArgumentException.class, () -> function.floorIndexOfX(0.5));
        assertThrows(IllegalArgumentException.class, () -> function.floorIndexOfX(-1.0));
    }

    @Test
    void removeExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> function.remove(-1));
        assertThrows(IllegalArgumentException.class, () -> function.remove(10));
    }

    @Test
    void iteratorWhileTest() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        Iterator<Point> iterator = func.iterator();

        int index = 0;
        while (iterator.hasNext()) {
            Point point = iterator.next();

            assertEquals(xValues[index], point.x, PRECISION);
            assertEquals(yValues[index], point.y, PRECISION);

            index++;
        }
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void iteratorForEachTest() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        int index = 0;

        for (Point point : func) {
            assertEquals(xValues[index], point.x, PRECISION);
            assertEquals(yValues[index], point.y, PRECISION);

            index++;
        }
    }

}

