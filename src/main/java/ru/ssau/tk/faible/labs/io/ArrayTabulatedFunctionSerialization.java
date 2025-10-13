package ru.ssau.tk.faible.labs.io;

import ru.ssau.tk.faible.labs.functions.ArrayTabulatedFunction;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;
import ru.ssau.tk.faible.labs.operations.TabulatedDifferentialOperator;

import java.io.*;

public class ArrayTabulatedFunctionSerialization {
    public static void main(String[] args) throws IOException {

        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0, 25.0};

        ArrayTabulatedFunction originalFunction = new ArrayTabulatedFunction(xValues,yValues);

        // оператор для вычисления производных
        TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator();

        // вычисляем первую производную
        TabulatedFunction FirstDerivat = differentialOperator.derive(originalFunction);

        // вычисляем вторую производную
        TabulatedFunction SecondDerivat = differentialOperator.derive(originalFunction);

        try (FileOutputStream fileOut = new FileOutputStream("output/serialized array functions.bin");
             BufferedOutputStream bufferedOut = new BufferedOutputStream(fileOut)) {

            // сериализуем все три функции
            FunctionsIO.serialize(bufferedOut, originalFunction);
            FunctionsIO.serialize(bufferedOut, FirstDerivat);
            FunctionsIO.serialize(bufferedOut, SecondDerivat);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileInputStream filein = new FileInputStream("output/serialized array functions.bin");
             BufferedInputStream bufferedin = new BufferedInputStream(filein)) {

            // десериализуем все три функции
            TabulatedFunction deserializedOriginal = FunctionsIO.deserialize(bufferedin);
            TabulatedFunction deserializedFirstDeriv = FunctionsIO.deserialize(bufferedin);
            TabulatedFunction deserializedSecondDeriv = FunctionsIO.deserialize(bufferedin);

            // выводим результаты всех трех функций
            System.out.println(deserializedOriginal.toString());
            System.out.println(deserializedFirstDeriv.toString());
            System.out.println(deserializedSecondDeriv.toString());

        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }





    }

}
