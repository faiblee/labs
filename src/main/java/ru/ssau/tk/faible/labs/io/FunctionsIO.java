package ru.ssau.tk.faible.labs.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk.faible.labs.functions.Point;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;
import ru.ssau.tk.faible.labs.functions.factory.TabulatedFunctionFactory;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public final class FunctionsIO {
    private final static Logger log = LoggerFactory.getLogger(FunctionsIO.class);

    private FunctionsIO() {
        throw new UnsupportedOperationException();
    }

    public static void serialize(BufferedOutputStream stream, TabulatedFunction function) throws IOException {
        log.info("Запущена сериализация");
        // создаем ObjectOutputStream для сериализации объектов
        ObjectOutputStream objectStream = new ObjectOutputStream(stream);

        // сериализуем функцию в поток
        objectStream.writeObject(function);

        // сбрасываем буфер
        objectStream.flush();

        log.info("Сериализация завершена");
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

        // записываем количество точек
        printWriter.println(function.getCount());

        // записываем все точки функции
        for (Point point : function) {
            printWriter.printf("%f %f\n", point.x, point.y);
        }
        printWriter.flush();
    }

    public static TabulatedFunction readTabulatedFunction(BufferedInputStream inputStream, TabulatedFunctionFactory factory) throws IOException {
        DataInputStream dis = new DataInputStream(inputStream);

        int count = dis.readInt();

        double[] xValues = new double[count];
        double[] yValues = new double[count];

        for (int i = 0; i < count; i++) {
            xValues[i] = dis.readDouble();
            yValues[i] = dis.readDouble();
        }

        return factory.create(xValues, yValues);
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
            log.error("Выброшено ParseException");
            throw new IOException("Ошибка парсинга чисел", e);
        }
    }

    public static TabulatedFunction deserialize(BufferedInputStream stream) throws IOException, ClassNotFoundException {
        ObjectInputStream objectIS = new ObjectInputStream(stream);
        return (TabulatedFunction) objectIS.readObject();
    }

}
