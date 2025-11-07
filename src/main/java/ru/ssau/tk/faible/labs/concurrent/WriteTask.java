package ru.ssau.tk.faible.labs.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;

public class WriteTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(WriteTask.class);

    private final TabulatedFunction function;
    private final double value;

    public WriteTask(double value, TabulatedFunction function) {
        this.value = value;
        this.function = function;
    }

    @Override
    public void run() {
        log.info("Thread {} was started", Thread.currentThread().getName());

        for (int i = 0; i< function.getCount(); i++) {
            synchronized (function) {
                function.setY(i, value);
                System.out.printf("Writing for index %d complete\n", i);
            }
        }

        log.info("Thread {} was finished", Thread.currentThread().getName());
    }
}