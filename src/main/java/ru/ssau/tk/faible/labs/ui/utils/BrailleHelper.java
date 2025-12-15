package ru.ssau.tk.faible.labs.ui.utils;

import com.vaadin.flow.server.VaadinSession;

public class BrailleHelper {

    private static final String BRAILLE_MODE_KEY = "braille_mode_enabled";

    //Проверяет, включён ли режим Брайля у текущего пользователя и возвращает текст в нужном виде.
    public static String applyBrailleIfEnabled(String text) {
        if (text == null) return null;

        Boolean brailleMode = getBrailleModeFromSession();
        if (brailleMode != null && brailleMode) {
            return BrailleConverter.convert(text);
        }
        return text;
    }

    // Устанавливает режим Брайля для текущего пользователя.
    public static void setBrailleModeEnabled(boolean enabled) {
        VaadinSession.getCurrent().setAttribute(BRAILLE_MODE_KEY, enabled);
    }


    // Проверяет, включён ли режим Брайля у текущего пользователя
    public static boolean isBrailleModeEnabled() {
        Boolean brailleMode = getBrailleModeFromSession();
        return brailleMode != null && brailleMode;
    }


     // Вспомогательный метод для получения флага из сессии
    private static Boolean getBrailleModeFromSession() {
        return (Boolean) VaadinSession.getCurrent().getAttribute(BRAILLE_MODE_KEY);
    }
}