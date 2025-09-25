package ru.ssau.tk.faible.labs.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IdentityFunctionTest {
    @Test
    void applyOneTest() {
        IdentityFunction function = new IdentityFunction();
        double result = function.apply(1.0);
        Assertions.assertEquals(1.0, result);
    }
}