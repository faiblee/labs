package ru.ssau.tk.faible.labs.concurrent;

import ru.ssau.tk.faible.labs.functions.TabulatedFunction;

public class WriteTask implements Runnable {

    private final TabulatedFunction function;
    private final double value;
    private final Object synchronizedMonitor; // объект, по которому будем синхронизироваться

    public WriteTask(double value, TabulatedFunction function, Object synchronizedMonitor) {
        this.value = value;
        this.function = function;
        this.synchronizedMonitor = synchronizedMonitor;
    }

    @Override
    public void run() {
        for (int i = 0; i< function.getCount(); i++) {
            synchronized (synchronizedMonitor) {
                function.setY(i, value);
                System.out.printf("Writing for index %d complete\n", i);
            }
        }
    }
}