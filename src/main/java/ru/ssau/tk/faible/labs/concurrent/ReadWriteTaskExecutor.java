package ru.ssau.tk.faible.labs.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk.faible.labs.functions.ConstantFunction;
import ru.ssau.tk.faible.labs.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;

public class ReadWriteTaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(ReadWriteTaskExecutor.class);

    public static void main(String[] args) {
        log.info("Thread {} was started", Thread.currentThread().getName());

        ConstantFunction negativeFunction = new ConstantFunction(-1);
        TabulatedFunction function = new LinkedListTabulatedFunction(negativeFunction, 1, 1000, 1000);

        // создаем поток для ReadTask
        Runnable readTask = new ReadTask(function);
        Thread readThread = new Thread(readTask);

        // создаем поток для WriteTask
        Runnable writeTask = new WriteTask(0.5, function);
        Thread writeThread = new Thread(writeTask);

        // запускаем потоки
        readThread.start();
        writeThread.start();

        log.info("Thread {} was finished", Thread.currentThread().getName());
    }
}