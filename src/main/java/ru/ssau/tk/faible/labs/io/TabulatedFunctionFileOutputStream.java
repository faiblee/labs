package ru.ssau.tk.faible.labs.io;

import ru.ssau.tk.faible.labs.functions.ArrayTabulatedFunction;
import ru.ssau.tk.faible.labs.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TabulatedFunctionFileOutputStream {
    public static void main(String[] args) {

        double[] xValues = new double[]{0.0, 0.5, 1.0, 1.5, 2.0};
        double[] yValues = new double[]{0.0, 0.25, 1.0, 2.25, 4.0};

        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction linkedListFunction = new LinkedListTabulatedFunction(xValues, yValues);

        try (FileOutputStream arrayFos = new FileOutputStream("output/array function.bin");
             FileOutputStream linkedListFos = new FileOutputStream("output/linked list function.bin");
             BufferedOutputStream arrayBof = new BufferedOutputStream(arrayFos);
             BufferedOutputStream linkedListBof = new BufferedOutputStream(linkedListFos)) {

            FunctionsIO.writeTabulatedFunction(arrayBof, arrayFunction);
            FunctionsIO.writeTabulatedFunction(linkedListBof, linkedListFunction);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
