package ru.ssau.tk.faible.labs.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;

public class MultiplyingTask implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(MultiplyingTask.class);
    private final TabulatedFunction function; // приватное поле

    // конструктор
    public MultiplyingTask(TabulatedFunction function){
        this.function = function;
    }

    @Override
    public void run(){
        log.info("Thread {} was started", Thread.currentThread().getName());
        // проходимся циклом по записям табулированной функции
        for (int i = 0; i < function.getCount(); i++){
            synchronized (function) {
                double currentY = function.getY(i);
                // увеличиваем y в 2 раза
                function.setY(i, currentY * 2);
            }
        }
        // выводим информацию в консоль о том, что поток закончил выполнение задачи
        log.info("Thread {} was finished", Thread.currentThread().getName());
    }
}

