package ru.ssau.tk.faible.labs.concurrent;

import ru.ssau.tk.faible.labs.functions.TabulatedFunction;

public class ReadTask implements Runnable{

    private final TabulatedFunction function;
    private final Object synchronizedMonitor; // объект, по которому будем синхронизироваться

    public ReadTask(TabulatedFunction function, Object synchronizedMonitor) {
        this.function = function;
        this.synchronizedMonitor = synchronizedMonitor;
    }

    @Override
    public void run() {
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (synchronizedMonitor) {
                System.out.printf("After read: i = %d, x = %f, y = %f\n", i, function.getX(i), function.getY(i));
            }
        }
    }
}
