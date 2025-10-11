package ru.ssau.tk.faible.labs.io;

import ru.ssau.tk.faible.labs.functions.TabulatedFunction;
import ru.ssau.tk.faible.labs.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.functions.factory.TabulatedFunctionFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TabulatedFunctionFileReader {
    public static void main(String[] args) {
        // создаем фабрики для разных типов функций
        TabulatedFunctionFactory arrayFactory = new ArrayTabulatedFunctionFactory();
        TabulatedFunctionFactory linkedListFactory = new LinkedListTabulatedFunctionFactory();

        try (
                // два потока чтения из одного файла
                FileReader fileReader1 = new FileReader("input/function.txt");
                FileReader fileReader2 = new FileReader("input/function.txt");

                // обертываем в BufferedReader
                BufferedReader reader1 = new BufferedReader(fileReader1);
                BufferedReader reader2 = new BufferedReader(fileReader2)
        ) {
            // читаем функцию с помощью Array фабрики
            TabulatedFunction arrayFunction = FunctionsIO.readTabulatedFunction(reader1, arrayFactory);
            System.out.println("ArrayTabulatedFunction:");
            System.out.println(arrayFunction.toString());

            System.out.println(); // Пустая строка для разделения

            // читаем функцию с помощью LinkedList фабрики
            TabulatedFunction linkedListFunction = FunctionsIO.readTabulatedFunction(reader2, linkedListFactory);
            System.out.println("LinkedListTabulatedFunction:");
            System.out.println(linkedListFunction.toString());

        } catch (IOException e) {
            //передачи стектрейса в поток ошибок
            e.printStackTrace();
        }
    }
}
