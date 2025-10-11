package ru.ssau.tk.faible.labs.io;

import ru.ssau.tk.faible.labs.functions.ArrayTabulatedFunction;
import ru.ssau.tk.faible.labs.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TabulatedFunctionFileWriter {
    public static void main(String[] args){
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0, 50.0};

        // создаем две функции разных типов
        TabulatedFunction ArrayFunction = new ArrayTabulatedFunction(xValues,yValues);
        TabulatedFunction linkedListFunction = new LinkedListTabulatedFunction(xValues, yValues);

        try (
                // создаем FileWriter для массива
                FileWriter arrayWriter = new FileWriter("output/array function.txt");
                // создаем FileWriter для связного списка
                FileWriter linkedListWriter = new FileWriter("output/linked list function.txt");

                // обертываем в BufferedWriter
                BufferedWriter bufferedArrayWriter = new BufferedWriter(arrayWriter);
                BufferedWriter bufferedLinkedListWriter = new BufferedWriter(linkedListWriter)
        ) {
            // записываем функции в соответствующие файлы
            FunctionsIO.writeTabulatedFunction(bufferedArrayWriter, ArrayFunction);
            FunctionsIO.writeTabulatedFunction(bufferedLinkedListWriter, linkedListFunction);

        } catch (IOException e) {
            // обрабатываем исключение ввода-вывода
            e.printStackTrace();
        }




    }
}
