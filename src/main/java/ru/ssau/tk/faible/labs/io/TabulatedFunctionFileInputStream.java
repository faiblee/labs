package ru.ssau.tk.faible.labs.io;

import ru.ssau.tk.faible.labs.functions.TabulatedFunction;
import ru.ssau.tk.faible.labs.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.operations.TabulatedDifferentialOperator;

import java.io.*;

public class TabulatedFunctionFileInputStream {
    public static void main(String[] args) {
        // 1 часть: считываем функцию из файла .bin
        try (
                FileInputStream fileIS = new FileInputStream("input/binary function.bin"); // оборачиваем файл во входной байтовый поток
                BufferedInputStream bufferedIS = new BufferedInputStream(fileIS); // а затем оборачиваем в буферизированный
        ) {

            TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory(); // фабрика array функции
            TabulatedFunction function = FunctionsIO.readTabulatedFunction(bufferedIS, factory); // считываем функцию из файла

            System.out.println(function.toString()); // выводим считанную функцию
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 2 часть: считываем функцию из консоли
        try {
            InputStreamReader ISReader = new InputStreamReader(System.in); // оборачиваем сначала System.in во входной символьный поток
            BufferedReader bufferedReader = new BufferedReader(ISReader); // а затем оборачиваем в буферизированный

            TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory(); // фабрика linkedList функции
            System.out.println("Введите размер и значения функции");
            TabulatedFunction function = FunctionsIO.readTabulatedFunction(bufferedReader, factory); // считываем саму функцию из потока

            // дифференциальный оператор с linkedList фабрикой
            TabulatedDifferentialOperator deriveOperator = new TabulatedDifferentialOperator(factory);
            TabulatedFunction derive = deriveOperator.derive(function); // находим производную считанной функции
            System.out.println(derive.toString()); // выводим производную
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
