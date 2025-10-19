package ru.ssau.tk.faible.labs.concurrent;

import ru.ssau.tk.faible.labs.functions.TabulatedFunction;

public class WriteTask implements Runnable {

    private final TabulatedFunction function;
    private final double value;

    public WriteTask(double value, TabulatedFunction function) {
        this.value = value;
        this.function = function;
    }

    @Override
    public void run() {
        for (int i = 0; i< function.getCount(); i++) {
            function.setY(i, value);
            System.out.printf("Writing for index %d complete\n", i);
        }
    }
}
