package ru.ssau.tk.faible.labs.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArrayTabulatedFunctionTest {

    private final double PRECISION = 1e-10;

    @Test
    void constructorWithArraysTest() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // проверка корректного создания объекта
        Assertions.assertEquals(3, function.getCount(), PRECISION);
        Assertions.assertEquals(1.0, function.leftBound(), PRECISION);
        Assertions.assertEquals(3.0, function.rightBound(), PRECISION);
    }

    @Test
    void constructorWithFunctionTest() {
        MathFunction linear = x -> 2 * x + 1;
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(linear, 0.0, 4.0, 5);

        // проверка табулирования функции
        Assertions.assertEquals(5, function.getCount());
        Assertions.assertEquals(1.0, function.getY(0));  // 2*0 + 1 = 1
        Assertions.assertEquals(3.0, function.getY(1));  // 2*1 + 1 = 3
        Assertions.assertEquals(9.0, function.getY(4));  // 2*4 + 1 = 9
    }


    @Test
    void applyTest() {
        double[] xValues = {2.0, 4.0, 6.0};
        double[] yValues = {4.0, 8.0, 12.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // проверка интерполяции и экстраполяции
        Assertions.assertEquals(2.0, function.apply(1.0), PRECISION);  // экстраполяция слева
        Assertions.assertEquals(8.0, function.apply(4.0), PRECISION);  // точное значение
        Assertions.assertEquals(10.0, function.apply(5.0), PRECISION); // интерполяция
    }

    @Test
    void floorIndexOfXTest() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // проверка поиска индекса для разных позиций
        Assertions.assertEquals(1, function.floorIndexOfX(1.5)); // Между 1 и 2
        Assertions.assertEquals(3, function.floorIndexOfX(3.5)); // Между 3 и 4
    }

    @Test
    void interpolateTest() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 2.0, 3.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // проверка линейной интерполяции
        Assertions.assertEquals(1.5, function.interpolate(1.5, 0), PRECISION);
        Assertions.assertEquals(2.5, function.interpolate(2.5, 1), PRECISION);
    }

    @Test
    void extrapolateTest() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 2.0, 3.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // проверка экстраполяции
        Assertions.assertEquals(0.0, function.extrapolateLeft(0.0), PRECISION);
        Assertions.assertEquals(4.0, function.extrapolateRight(4.0), PRECISION);
    }


    @Test
    void getXGetYTest() {
        double[] xValues = {10.0, 20.0, 30.0};
        double[] yValues = {100.0, 200.0, 300.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // проверка доступа к данным
        Assertions.assertEquals(10.0, function.getX(0), PRECISION);
        Assertions.assertEquals(30.0, function.getX(2), PRECISION);
        Assertions.assertEquals(100.0, function.getY(0), PRECISION);
        Assertions.assertEquals(300.0, function.getY(2), PRECISION);
    }

    @Test
    void setYTest() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.setY(1, 25.0);
        Assertions.assertEquals(25.0, function.getY(1), PRECISION);
    }

    @Test
    void indexOfTest() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // проверка поиска элементов
        Assertions.assertEquals(0, function.indexOfX(1.0));
        Assertions.assertEquals(3, function.indexOfX(4.0));
        Assertions.assertEquals(-1, function.indexOfX(5.0)); // не найден
    }


    @Test
    void singlePointFunctionTest() {
        double[] xValues = {5.0};
        double[] yValues = {10.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // проверка работы с функцией из одной точки
        Assertions.assertEquals(10.0, function.apply(1.0));  // всегда возвращает Y
        Assertions.assertEquals(10.0, function.apply(5.0));
        Assertions.assertEquals(10.0, function.apply(10.0));
    }

    @Test
    void constructorWithSameBoundsTest() {
        MathFunction square = x -> x * x;
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(square, 3.0, 3.0, 4);

        // проверка создания функции с одинаковыми границами
        Assertions.assertEquals(4, function.getCount());
        for (int i = 0; i < 4; i++) {
            Assertions.assertEquals(3.0, function.getX(i), PRECISION);
            Assertions.assertEquals(9.0, function.getY(i), PRECISION);
        }
    }

    @Test
    void constructorInvalidArraysTest() {
        // разные длины массивов
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0};

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void constructorEmptyArraysTest() {
        double[] xValues = {};
        double[] yValues = {};

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void constructorInvalidDataTest() {
        double[] xValues = {1.0, 3.0, 2.0}; // не упорядочены
        double[] yValues = {10.0, 30.0, 20.0};

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void getInvalidIndexTest() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // проверка обработки неверных индексов
        Assertions.assertThrows(IllegalArgumentException.class, () -> function.getX(-1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> function.getX(3));
    }


    @Test
    void andThenTest() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 2.0, 3.0};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues, yValues);

        MathFunction square = x -> x * x;

        // проверка композиции функций
        MathFunction composite = func1.andThen(square);
        double result = composite.apply(2.0);
        // func1(2.0) = 2.0, затем square(2.0) = 4.0
        Assertions.assertEquals(4.0, result, PRECISION);
    }

    ArrayTabulatedFunction function;

    @BeforeEach
    void setUp() {
        double[] xValues = {1.0, 2.0, 4.0, 5.0};
        double[] yValues = {10.0, 20.0, 40.0, 50.0};
        function = new ArrayTabulatedFunction(xValues, yValues);
    }


    @Test
        // замена y при существующем x
    void insertExistsXTest() {
        function.insert(2.0, 25.0);

        Assertions.assertEquals(4, function.getCount());
        Assertions.assertEquals(25.0, function.getY(1), PRECISION);
    }

    @Test
        // вставка в начало
    void insertToBeginningTest() {
        function.insert(0.5, 5.0);

        Assertions.assertEquals(5, function.getCount());
        Assertions.assertEquals(0.5, function.getX(0), PRECISION);
        Assertions.assertEquals(5.0, function.getY(0), PRECISION);
    }

    @Test
        // вставка в конец
    void insertToEndTest() {
        function.insert(6.0, 60.0);

        Assertions.assertEquals(5, function.getCount());
        Assertions.assertEquals(5.0, function.getX(3), PRECISION);
        Assertions.assertEquals(50.0, function.getY(3), PRECISION);
        Assertions.assertEquals(6.0, function.getX(4), PRECISION);
        Assertions.assertEquals(60.0, function.getY(4), PRECISION);
    }

    @Test
        // вставка между существующими значениями
    void insertBetweenExistingValuesTest() {
        function.insert(3.0, 30.0);

        Assertions.assertEquals(5, function.getCount());
        Assertions.assertEquals(2.0, function.getX(1), PRECISION);
        Assertions.assertEquals(20.0, function.getY(1), PRECISION);
        Assertions.assertEquals(3.0, function.getX(2), PRECISION);
        Assertions.assertEquals(30.0, function.getY(2), PRECISION);
        Assertions.assertEquals(4.0, function.getX(3), PRECISION);
        Assertions.assertEquals(40.0, function.getY(3), PRECISION);
    }

    @Test
    // множественные вставки
    void insertMultipleInsertsTest() {
        function.insert(3.0, 30.0);
        function.insert(0.0, 0.0);
        function.insert(2.5, 25.0);

        Assertions.assertEquals(7, function.getCount());
        Assertions.assertEquals(0.0, function.getX(0));
        Assertions.assertEquals(0.0, function.getY(0));
        Assertions.assertEquals(2.5, function.getX(3));
        Assertions.assertEquals(25.0, function.getY(3));
        Assertions.assertEquals(3.0, function.getX(4));
        Assertions.assertEquals(30.0, function.getY(4));
    }

    @Test
    // проверка сохранения работоспособности после вставки
    void insertPreserveFunctionalityTest() {
        function.insert(3.0, 30.0);
        function.insert(0.0, 0.0);

        // проверяем, что функция продолжает корректно работать
        Assertions.assertEquals(0.0, function.apply(0.0), PRECISION);
        Assertions.assertEquals(15.0, function.apply(1.5), PRECISION);
        Assertions.assertEquals(30.0, function.apply(3.0), PRECISION);
        Assertions.assertEquals(-10.0, function.apply(-1.0), PRECISION);
        Assertions.assertEquals(70.0, function.apply(7.0), PRECISION);

        // проверяем поиск индексов
        Assertions.assertEquals(0, function.indexOfX(0.0));
        Assertions.assertEquals(3, function.indexOfX(3.0));
        Assertions.assertEquals(0, function.indexOfY(0.0));
        Assertions.assertEquals(3, function.indexOfY(30.0));
    }
    @Test
    void removeFirstElementTest() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(0);

        Assertions.assertEquals(3, function.getCount());
        Assertions.assertEquals(2.0, function.getX(0), PRECISION);
        Assertions.assertEquals(3.0, function.getX(1), PRECISION);
        Assertions.assertEquals(4.0, function.getX(2), PRECISION);
        Assertions.assertEquals(20.0, function.getY(0), PRECISION);
        Assertions.assertEquals(30.0, function.getY(1), PRECISION);
        Assertions.assertEquals(40.0, function.getY(2), PRECISION);
        Assertions.assertEquals(2.0, function.leftBound(), PRECISION);
    }

    @Test
    void removeLastElementTest() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(3);

        Assertions.assertEquals(3, function.getCount());
        Assertions.assertEquals(1.0, function.getX(0), PRECISION);
        Assertions.assertEquals(2.0, function.getX(1), PRECISION);
        Assertions.assertEquals(3.0, function.getX(2), PRECISION);
        Assertions.assertEquals(10.0, function.getY(0), PRECISION);
        Assertions.assertEquals(20.0, function.getY(1), PRECISION);
        Assertions.assertEquals(30.0, function.getY(2), PRECISION);
        Assertions.assertEquals(3.0, function.rightBound(), PRECISION);
    }

    @Test
    void removeMiddleElementTest() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(1);

        Assertions.assertEquals(3, function.getCount());
        Assertions.assertEquals(1.0, function.getX(0), PRECISION);
        Assertions.assertEquals(3.0, function.getX(1), PRECISION);
        Assertions.assertEquals(4.0, function.getX(2), PRECISION);
        Assertions.assertEquals(10.0, function.getY(0), PRECISION);
        Assertions.assertEquals(30.0, function.getY(1), PRECISION);
        Assertions.assertEquals(40.0, function.getY(2), PRECISION);
    }

    @Test
    void removeMultipleElementsTest() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0, 50.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(2); // удаляем элемент 3.0
        function.remove(1); // удаляем элемент 2.0 (теперь он на позиции 1 после первого удаления)

        Assertions.assertEquals(3, function.getCount());
        Assertions.assertEquals(1.0, function.getX(0), PRECISION);
        Assertions.assertEquals(4.0, function.getX(1), PRECISION);
        Assertions.assertEquals(5.0, function.getX(2), PRECISION);
        Assertions.assertEquals(10.0, function.getY(0), PRECISION);
        Assertions.assertEquals(40.0, function.getY(1), PRECISION);
        Assertions.assertEquals(50.0, function.getY(2), PRECISION);
    }

    @Test
    void removeFromTwoElementArrayTest() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(0);

        Assertions.assertEquals(1, function.getCount());
        Assertions.assertEquals(2.0, function.getX(0), PRECISION);
        Assertions.assertEquals(20.0, function.getY(0), PRECISION);
        Assertions.assertEquals(2.0, function.leftBound(), PRECISION);
        Assertions.assertEquals(2.0, function.rightBound(), PRECISION);
    }

    @Test
    void removeInvalidIndexTest() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            function.remove(-1);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            function.remove(3);
        });
    }

    @Test
    void removeLastPointTest() {
        double[] xValues = {1.0};
        double[] yValues = {10.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Assertions.assertThrows(IllegalStateException.class, () -> {
            function.remove(0);
        });
    }

    @Test
    void removeAndCheckInterpolationTest() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(1); // Удаляем точку (2.0, 20.0)

        // проверяем интерполяцию между оставшимися точками
        Assertions.assertEquals(15.0, function.apply(1.5), PRECISION); // между 1.0 и 3.0
        Assertions.assertEquals(25.0, function.apply(2.5), PRECISION); // между 1.0 и 3.0
        Assertions.assertEquals(35.0, function.apply(3.5), PRECISION); // между 3.0 и 4.0
    }

    @Test
    void removeAndCheckExtrapolationTest() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(0); // Удаляем первую точку

        // проверяем экстраполяцию слева
        Assertions.assertEquals(0.0, function.apply(0.0), PRECISION);

        function.remove(function.getCount() - 1); // Удаляем последнюю точку

        // проверяем экстраполяцию справа
        Assertions.assertEquals(50.0, function.apply(5.0), PRECISION);
    }

    @Test
    void removeAndFloorIndexOfXTest() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(1); // Удаляем точку 2.0

        // проверяем floorIndexOfX после удаления
        Assertions.assertEquals(0, function.floorIndexOfX(0.5));  // до первого
        Assertions.assertEquals(0, function.floorIndexOfX(1.0));  // точно первый
        Assertions.assertEquals(0, function.floorIndexOfX(1.5));  // между 1 и 3
        Assertions.assertEquals(0, function.floorIndexOfX(2.0));  // между 1 и 3
        Assertions.assertEquals(0, function.floorIndexOfX(2.5));  // между 1 и 3
        Assertions.assertEquals(1, function.floorIndexOfX(3.0));  // точно второй
        Assertions.assertEquals(1, function.floorIndexOfX(3.5));  // между 3 и 4
        Assertions.assertEquals(2, function.floorIndexOfX(4.0));  // точно третий
        Assertions.assertEquals(3, function.floorIndexOfX(5.0));  // после последнего
    }

    @Test
    void removeAndIndexOfTest() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(1); // удаляем точку (2.0, 20.0)

        // проверяем поиск индексов после удаления
        Assertions.assertEquals(0, function.indexOfX(1.0));
        Assertions.assertEquals(1, function.indexOfX(3.0));
        Assertions.assertEquals(2, function.indexOfX(4.0));
        Assertions.assertEquals(-1, function.indexOfX(2.0)); // удаленная точка

        Assertions.assertEquals(0, function.indexOfY(10.0));
        Assertions.assertEquals(1, function.indexOfY(30.0));
        Assertions.assertEquals(2, function.indexOfY(40.0));
        Assertions.assertEquals(-1, function.indexOfY(20.0)); // удаленное значение
    }

    @Test
    void removeAllButOneTest() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(0); // удаляем первую точку
        function.remove(0); // удаляем новую первую точку (бывшую вторую)

        Assertions.assertEquals(1, function.getCount());
        Assertions.assertEquals(3.0, function.getX(0), PRECISION);
        Assertions.assertEquals(30.0, function.getY(0), PRECISION);

        // проверяем, что нельзя удалить последнюю точку
        Assertions.assertThrows(IllegalStateException.class, () -> {
            function.remove(0);
        });
    }

}
