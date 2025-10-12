package ru.ssau.tk.faible.labs.io;

import ru.ssau.tk.faible.labs.functions.Point;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;
import ru.ssau.tk.faible.labs.functions.factory.TabulatedFunctionFactory;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public final class FunctionsIO {
    private FunctionsIO() {
        throw new UnsupportedOperationException();
    }

    public static void writeTabulatedFunction(BufferedOutputStream outputStream, TabulatedFunction function) throws IOException {
        DataOutputStream dos = new DataOutputStream(outputStream);
        // записываем количество точек
        dos.writeInt(function.getCount());
        // записываем все точки
        for (Point point : function) {
            dos.writeDouble(point.x);
            dos.writeDouble(point.y);
        }
        dos.flush();
    }

    public static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction function){
        PrintWriter printWriter = new PrintWriter(writer);

        // записывваем количество точек
        printWriter.println(function.getCount());

        // записываем все точки функции
        for (Point point : function) {
            printWriter.printf("%f %f\n", point.x, point.y);
        }
        printWriter.flush();
    }

    public static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory) throws IOException {
        try {
            // читаем первую строку
            String countLine = reader.readLine();
            int count = Integer.parseInt(countLine);

            // создаем массивы для x и y значений
            double[] xValues = new double[count];
            double[] yValues = new double[count];

            // Создаем форматтер для чисел с запятой как разделителем
            NumberFormat formatter = NumberFormat.getInstance(Locale.forLanguageTag("ru"));

            // читаем количество строк с данными
            for (int i = 0; i < count; i++) {
                String line = reader.readLine();
                // разбиваем строку на две части по пробелу
                String[] parts = line.split(" ");

                // парсим x значение
                Number xNumber = formatter.parse(parts[0]);
                xValues[i] = xNumber.doubleValue();

                // парсим y значение
                Number yNumber = formatter.parse(parts[1]);
                yValues[i] = yNumber.doubleValue();
            }

            // создаем и возвращаем функцию через фабрику
            return factory.create(xValues, yValues);

        } catch (ParseException e) {
            // оборачиваем ParseException в IOException
            throw new IOException("Ошибка парсинга чисел", e);
        }
    }

}
