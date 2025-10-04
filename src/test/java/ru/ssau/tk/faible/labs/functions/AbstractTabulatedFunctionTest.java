package ru.ssau.tk.faible.labs.functions;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.faible.labs.exceptions.ArrayIsNotSortedException;
import ru.ssau.tk.faible.labs.exceptions.DifferentLengthOfArraysException;

import static org.junit.jupiter.api.Assertions.*;

class AbstractTabulatedFunctionTest {
    @Test
    void testTrueCheckLengthIsTheSame(){
        // не должно быть исключений
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0,20.0,30.0};
        assertDoesNotThrow(() -> AbstractTabulatedFunction.checkLengthIsTheSame(xValues,yValues));
    }
    @Test
    void testCheckLengthIsTheSameWithDifferentLengthX() {
        //  xValues длиннее yValues
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0};
        assertThrows(DifferentLengthOfArraysException.class,
                () -> AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues));
    }
    @Test
    void testCheckLengthIsTheSameWithDifferentLengthY() {
        //  yValues длиннее xValues
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};

        assertThrows(DifferentLengthOfArraysException.class, () ->
                AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues));
    }
    @Test
    void testCheckLengthIsTheSameWithEmpty() {
        // оба массива пустые
        double[] xValues = {};
        double[] yValues = {};
        assertDoesNotThrow(() ->
                AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues));
    }
    @Test
    void testCheckLengthIsTheSame_WithSingleElement() {
        // оба массива с одним элементом
        double[] xValues = {5.0};
        double[] yValues = {50.0};
        assertDoesNotThrow(() ->
                AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues));
    }
    @Test
    void testCheckSortedWithVerySmallDifferenceFalse() {
        // маленькая разница,не отсортированы
        double[] xValues = {1.0000000002, 1.0000000001, 1.0};
        assertThrows(ArrayIsNotSortedException.class, () ->
                AbstractTabulatedFunction.checkSorted(xValues));
    }
    @Test
    void testCheckSorted_WithVerySmallDifferenceTrue() {
        // очень маленькая разница между элементами
        double[] xValues = {1.0, 1.0000000001, 1.0000000002};
        assertDoesNotThrow(() ->
                AbstractTabulatedFunction.checkSorted(xValues));
    }



    @Test
    void testTrueCheckSorted(){
        // не должно быть искоючений
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        assertDoesNotThrow(()
                -> AbstractTabulatedFunction.checkSorted(xValues));
    }
    @Test
    void testFalseCheckSorted_FirstElements(){
        //исключение, неотсортированны два первых элемента
        double[] xValues = {2.0, 1.0, 4.0, 5.0};
        assertThrows(ArrayIsNotSortedException.class,
                () ->AbstractTabulatedFunction.checkSorted(xValues));
    }
    @Test
    void testFalseCheckSorted_MiddleElement(){
        //исключение, неотсортированны элемент в середине
        double[] xValues = {1.0, 2.0, 7.0, 4.0, 5.0};
        assertThrows(ArrayIsNotSortedException.class,
                () ->AbstractTabulatedFunction.checkSorted(xValues));
    }
    @Test
    void testFalseCheckSorted_LastElement(){
        //исключение, неотсортированны два последних элемента
        double[] xValues = {1.0, 2.0, 3.0, 5.0, 4.0};
        assertThrows(ArrayIsNotSortedException.class,
                () ->AbstractTabulatedFunction.checkSorted(xValues));
    }
    @Test
    void testFalseCheckSorted_WithDublicateElements(){
        // исключение, два одинаковых элемента
        double[] xValues = {1.0, 2.0, 3.0, 3.0, 4.0, 5.0};
        assertThrows(ArrayIsNotSortedException.class,
                () ->AbstractTabulatedFunction.checkSorted(xValues));
    }
    @Test
    void testCheckSortedWithOneleElement() {
        // массив с одним элементом
        double[] xValues = {52.0};
        assertDoesNotThrow(() ->
                AbstractTabulatedFunction.checkSorted(xValues));
    }
    @Test
    void testCheckSorted_WithEmptyArray() {
        // пустой массив
        double[] xValues = {};
        assertDoesNotThrow(() ->
                AbstractTabulatedFunction.checkSorted(xValues));
    }
    @Test
    void testCheckSorted_WithAllDuplicateValues() {
        // все элементы одинаковые
        double[] xValues = {5.0, 5.0, 5.0, 5.0};
        assertThrows(ArrayIsNotSortedException.class, () ->
                AbstractTabulatedFunction.checkSorted(xValues));
    }
    @Test
    void testCheckSorted_WithDescendingArray() {
        // убывающий массив
        double[] xValues = {5.0, 4.0, 3.0, 2.0, 1.0};
        assertThrows(ArrayIsNotSortedException.class, () ->
                AbstractTabulatedFunction.checkSorted(xValues));
    }
    @Test
    void testCheckSorted_WithNegativeNumbers_Sorted() {
        // отрицательные числа, отсортированны
        double[] xValues = {-5.0, -3.0, -1.0, 0.0, 2.0};
        assertDoesNotThrow(() ->
                AbstractTabulatedFunction.checkSorted(xValues));
    }
    @Test
    void testCheckSorted_WithNegativeNumbers_Unsorted() {
        // отрицательные числа, не отсортированы
        double[] xValues = {-3.0, -5.0, -1.0, 0.0, 2.0};
        assertThrows(ArrayIsNotSortedException.class, () ->
                AbstractTabulatedFunction.checkSorted(xValues));
    }
    @Test
    void testCheckSorted_WithLargeSortedArray() {
        // большой отсортированный массив
        double[] xValues = new double[1000];
        for (int i = 0; i < 1000; i++) {
            xValues[i] = i * 0.1;
        }
        assertDoesNotThrow(() ->
                AbstractTabulatedFunction.checkSorted(xValues));
    }


}