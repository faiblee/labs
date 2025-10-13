package ru.ssau.tk.faible.labs.io;

import ru.ssau.tk.faible.labs.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;
import ru.ssau.tk.faible.labs.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.operations.TabulatedDifferentialOperator;

import java.io.*;

public class LinkedListTabulatedFunctionSerialization {
    public static void main(String[] args) {
        try (
                FileOutputStream fileOS = new FileOutputStream("output/serialized linked list functions.bin"); // байтовый поток вывода
                BufferedOutputStream bufferedOS = new BufferedOutputStream(fileOS); // оборачиваем в буферизированный
        ) {
            double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
            double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};

            LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues); // сама функция
            LinkedListTabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory(); // linkedList фабрика
            TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator(factory); // дифференциальный оператор

            // находим первую и вторую производные
            TabulatedFunction firstDerive = differentialOperator.derive(function);
            TabulatedFunction secondDerive = differentialOperator.derive(firstDerive);

            // сериализуем все три функции
            FunctionsIO.serialize(bufferedOS, function);
            FunctionsIO.serialize(bufferedOS, firstDerive);
            FunctionsIO.serialize(bufferedOS, secondDerive);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileInputStream fileIS = new FileInputStream("output/serialized linked list functions.bin"); // байтовый поток ввода
                BufferedInputStream bufferedOS = new BufferedInputStream(fileIS); // оборачиваем в буферизированный
        ) {
            // десериализуем три функции
            TabulatedFunction function = FunctionsIO.deserialize(bufferedOS);
            TabulatedFunction firstDerive = FunctionsIO.deserialize(bufferedOS);
            TabulatedFunction secondDerive = FunctionsIO.deserialize(bufferedOS);

            // выводим их в консоль
            System.out.println(function.toString());
            System.out.println(firstDerive.toString());
            System.out.println(secondDerive.toString());

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
