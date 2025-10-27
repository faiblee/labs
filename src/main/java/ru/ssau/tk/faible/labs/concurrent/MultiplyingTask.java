package ru.ssau.tk.faible.labs.concurrent;

import ru.ssau.tk.faible.labs.functions.TabulatedFunction;

public class MultiplyingTask implements Runnable{

    private final TabulatedFunction function; // приватное поле

    // конструктор
    public MultiplyingTask(TabulatedFunction function){
        this.function = function;
    }

    @Override
    public void run(){
        // проходимся циклом по записям табулированной функции
        for (int i = 0; i < function.getCount(); i++){
            synchronized (function) {
                double currentY = function.getY(i);
                // увеличиваем y в 2 раза
                function.setY(i, currentY * 2);
            }
        }
        // выводим информацию в консоль о том, что поток закончил выполнение задачи
        System.out.println("Поток " + Thread.currentThread().getName() +
                "закончил выполнение задачи.");
    }
}

