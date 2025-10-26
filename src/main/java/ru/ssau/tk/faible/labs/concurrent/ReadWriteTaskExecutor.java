package ru.ssau.tk.faible.labs.concurrent;

import ru.ssau.tk.faible.labs.functions.ConstantFunction;
import ru.ssau.tk.faible.labs.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;

public class ReadWriteTaskExecutor {
    public static void main(String[] args) {
        ConstantFunction negativeFunction = new ConstantFunction(-1);
        TabulatedFunction function = new LinkedListTabulatedFunction(negativeFunction, 1, 1000, 1000);
        Object synchronizedMonitor = new Object(); // объект, по которому будем синхронизироваться

        // создаем поток для ReadTask
        Runnable readTask = new ReadTask(function, synchronizedMonitor);
        Thread readThread = new Thread(readTask);

        // создаем поток для WriteTask
        Runnable writeTask = new WriteTask(0.5, function, synchronizedMonitor);
        Thread writeThread = new Thread(writeTask);

        // запускаем потоки
        readThread.start();
        writeThread.start();
    }
}
