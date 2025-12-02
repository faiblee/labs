package ru.ssau.tk.faible.labs.io;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.faible.labs.functions.Point;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;
import ru.ssau.tk.faible.labs.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.functions.factory.TabulatedFunctionFactory;

import java.io.*;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class FunctionsIOTest {

    private final static double PRECISION = 1e-6;
    private final TabulatedFunctionFactory arrayFactory = new ArrayTabulatedFunctionFactory();
    private final TabulatedFunctionFactory linkedListFactory = new LinkedListTabulatedFunctionFactory();

    private TabulatedFunction createTestFunction(TabulatedFunctionFactory factory) {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        return factory.create(xValues, yValues);
    }

    @Test
    void testWriteAndReadBinaryArrayFunction() throws IOException {
        TabulatedFunction original = createTestFunction(arrayFactory);

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOut = new BufferedOutputStream(byteOut);

        FunctionsIO.writeTabulatedFunction(bufferedOut, original);
        bufferedOut.flush();

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        BufferedInputStream bufferedIn = new BufferedInputStream(byteIn);

        TabulatedFunction restored = FunctionsIO.readTabulatedFunction(bufferedIn, arrayFactory);

        assertEquals(3, restored.getCount(), "Количество точек должно сохраниться");

        for (int i = 0; i < original.getCount(); i++) {
            assertEquals(original.getX(i), restored.getX(i), PRECISION,
                    "X координаты должны совпадать");
            assertEquals(original.getY(i), restored.getY(i), PRECISION,
                    "Y координаты должны совпадать");
        }
    }
    @Test
    void testWriteAndReadBinaryLinkedListFunction() throws IOException {
        TabulatedFunction original = createTestFunction(linkedListFactory);

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOut = new BufferedOutputStream(byteOut);

        FunctionsIO.writeTabulatedFunction(bufferedOut, original);
        bufferedOut.flush();

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        BufferedInputStream bufferedIn = new BufferedInputStream(byteIn);

        TabulatedFunction restored = FunctionsIO.readTabulatedFunction(bufferedIn, linkedListFactory);

        assertEquals(3, restored.getCount(), "Количество точек должно сохраниться");

        Iterator<Point> originalIterator = original.iterator();
        Iterator<Point> restoredIterator = restored.iterator();

        while (originalIterator.hasNext() && restoredIterator.hasNext()) {
            Point originalPoint = originalIterator.next();
            Point restoredPoint = restoredIterator.next();

            assertEquals(originalPoint.x, restoredPoint.x, PRECISION);
            assertEquals(originalPoint.y, restoredPoint.y, PRECISION);
        }
    }

    @Test
    void testSerializeAndDeserialize() throws IOException, ClassNotFoundException {
        TabulatedFunction original = createTestFunction(arrayFactory);

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOut = new BufferedOutputStream(byteOut);

        FunctionsIO.serialize(bufferedOut, original);
        bufferedOut.flush();

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        BufferedInputStream bufferedIn = new BufferedInputStream(byteIn);


        TabulatedFunction restored = FunctionsIO.deserialize(bufferedIn);

        assertEquals(original.getCount(), restored.getCount());

        for (int i = 0; i < original.getCount(); i++) {
            assertEquals(original.getX(i), restored.getX(i), PRECISION);
            assertEquals(original.getY(i), restored.getY(i), PRECISION);
        }
    }

    @Test
    void testParseException() {
        StringReader stringReader = new StringReader("2\nabc 1,5\n2,0 3,0\n");
        BufferedReader bufferedReader = new BufferedReader(stringReader);

        IOException exception = assertThrows(IOException.class, () -> {
            FunctionsIO.readTabulatedFunction(bufferedReader, arrayFactory);
        }, "Должна быть IOException при некорректных данных");

        assertTrue(exception.getMessage().contains("парсинга"),
                "Сообщение об ошибке должно содержать 'парсинга'");
    }
    @Test
    void testLargeFunction() throws IOException {
        int pointCount = 100;
        double[] xValues = new double[pointCount];
        double[] yValues = new double[pointCount];

        for (int i = 0; i < pointCount; i++) {
            xValues[i] = i;
            yValues[i] = Math.sin(i * 0.1);
        }

        TabulatedFunction largeFunction = arrayFactory.create(xValues, yValues);

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOut = new BufferedOutputStream(byteOut);

        FunctionsIO.writeTabulatedFunction(bufferedOut, largeFunction);
        bufferedOut.flush();

        byte[] data = byteOut.toByteArray();
        int expectedSize = 4 + (pointCount * 2 * 8);
        assertEquals(expectedSize, data.length,
                "Размер данных должен соответствовать ожидаемому");

        ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
        BufferedInputStream bufferedIn = new BufferedInputStream(byteIn);

        TabulatedFunction restored = FunctionsIO.readTabulatedFunction(bufferedIn, arrayFactory);

        assertEquals(pointCount, restored.getCount());
        assertEquals(0.0, restored.getX(0), PRECISION);
        assertEquals(99.0, restored.getX(99), PRECISION);
        assertEquals(Math.sin(50 * 0.1), restored.getY(50), PRECISION);
    }
    @Test
    void testBothFactoriesProduceSameResults() throws IOException {
        TabulatedFunction arrayFunc = createTestFunction(arrayFactory);
        TabulatedFunction linkedListFunc = createTestFunction(linkedListFactory);

        ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
        FunctionsIO.writeTabulatedFunction(
                new BufferedOutputStream(arrayOut), arrayFunc);

        ByteArrayOutputStream linkedListOut = new ByteArrayOutputStream();
        FunctionsIO.writeTabulatedFunction(
                new BufferedOutputStream(linkedListOut), linkedListFunc);

        byte[] arrayData = arrayOut.toByteArray();
        byte[] linkedListData = linkedListOut.toByteArray();

        assertArrayEquals(arrayData, linkedListData,
                "Данные должны быть одинаковыми независимо от реализации функции");

        TabulatedFunction arrayRestored = FunctionsIO.readTabulatedFunction(
                new BufferedInputStream(new ByteArrayInputStream(arrayData)),
                arrayFactory);

        TabulatedFunction linkedListRestored = FunctionsIO.readTabulatedFunction(
                new BufferedInputStream(new ByteArrayInputStream(linkedListData)),
                linkedListFactory);

        assertEquals(arrayRestored.getCount(), linkedListRestored.getCount());

        for (int i = 0; i < arrayRestored.getCount(); i++) {
            assertEquals(arrayRestored.getX(i), linkedListRestored.getX(i), PRECISION);
            assertEquals(arrayRestored.getY(i), linkedListRestored.getY(i), PRECISION);
        }
    }




}
