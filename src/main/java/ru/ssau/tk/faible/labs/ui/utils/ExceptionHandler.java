// ui/utils/ErrorNotifier.java
package ru.ssau.tk.faible.labs.ui.utils;

import com.vaadin.flow.component.notification.Notification;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

public class ExceptionHandler {

    public static void notifyUser(Exception e) {
        if (e instanceof HttpClientErrorException ex) {
            String message = extractMessageFromResponse(ex);
            if (message.equals("Неверный формат данных")) {
                Notification.show("Ошибка: Пользователь с таким именем уже существует", 5000, Notification.Position.BOTTOM_CENTER);
            } else {
                Notification.show("Ошибка: " + message, 5000, Notification.Position.BOTTOM_CENTER);
            }
        } else if (e instanceof ResourceAccessException) {
            Notification.show("Сервер недоступен. Проверьте подключение к сети.", 5000, Notification.Position.BOTTOM_CENTER);
        } else {
            // Общая ошибка
            Notification.show("Произошла непредвиденная ошибка: " + e.getMessage(), 5000, Notification.Position.BOTTOM_CENTER);
        }
    }

    private static String extractMessageFromResponse(HttpClientErrorException ex) {
        try {
            // Попробуем извлечь JSON-сообщение
            String responseBody = ex.getResponseBodyAsString();
            if (!responseBody.trim().isEmpty()) {
                // Если ответ в формате { "error": "Текст" }
                if (responseBody.contains("\"error\"")) {
                    int start = responseBody.indexOf("\"error\":\"") + 9;
                    int end = responseBody.indexOf('"', start);
                    if (start > 8 && end > start) {
                        return responseBody.substring(start, end);
                    }
                }
                // Или просто вернуть весь ответ (на случай, если это plain text)
                return responseBody;
            }
        } catch (Exception ignored) {}
        return "Не удалось получить детали ошибки";
    }
}