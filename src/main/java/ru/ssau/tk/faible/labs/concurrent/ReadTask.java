package ru.ssau.tk.faible.labs.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;

public class ReadTask implements Runnable{

    private static final Logger log = LoggerFactory.getLogger(ReadTask.class);
    private final TabulatedFunction function;

    public ReadTask(TabulatedFunction function) {
        this.function = function;
    }

    @Override
    public void run() {
        log.info("Thread {} was started", Thread.currentThread().getName());
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (function) {
                System.out.printf("After read: i = %d, x = %f, y = %f\n", i, function.getX(i), function.getY(i));
            }
        }
        log.info("Thread {} was finished", Thread.currentThread().getName());
    }
}