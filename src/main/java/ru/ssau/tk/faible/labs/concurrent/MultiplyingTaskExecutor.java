package ru.ssau.tk.faible.labs.concurrent;

import ru.ssau.tk.faible.labs.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.faible.labs.functions.MathFunction;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;
import ru.ssau.tk.faible.labs.functions.UnitFunction;

import java.util.ArrayList;
import java.util.List;

public class MultiplyingTaskExecutor {
    public static void main(String[] args){

        MathFunction unitfunction = new UnitFunction();
        double xFrom = 1;
        double xTo = 1000;
        int Countpoints = 100;

        TabulatedFunction tabulatedFunction = new
                LinkedListTabulatedFunction(unitfunction,xFrom,xTo,Countpoints);


        List<Thread> threads = new ArrayList<>(); // список List потоков

        int threadsCount = 10; // количество потоков

        // создание задач и потоков
        for (int i = 0; i < threadsCount; i++){
            MultiplyingTask task = new MultiplyingTask(tabulatedFunction);
            Thread thread = new Thread(task);
            threads.add(thread);
        }

        // запуск всех потоков
        for (Thread thread : threads){
            thread.start();
        }

        // усыпление текущего потока на 2 секунды
        try {
            Thread.sleep(2000);
        }catch (InterruptedException e){ // исключение при прерывании потока
            e.printStackTrace();
        }
        // вывод функции
        System.out.println(tabulatedFunction);

    }
}
