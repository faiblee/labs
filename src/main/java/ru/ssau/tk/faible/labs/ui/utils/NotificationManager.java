// ui/utils/NotificationManager.java
package ru.ssau.tk.faible.labs.ui.utils;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import java.util.LinkedList;
import java.util.List;

public class NotificationManager {

    private static final int MAX_NOTIFICATIONS = 3; // Максимум 3 уведомления
    private static final List<Notification> activeNotifications = new LinkedList<>();

    public static void show(String message, int durationMs, Notification.Position position) {
        // Если уже показано MAX_NOTIFICATIONS — закрываем старейшее
        if (activeNotifications.size() >= MAX_NOTIFICATIONS) {
            Notification oldest = activeNotifications.remove(0);
            oldest.close();
        }

        Notification notification = new Notification(message, durationMs, position);
        notification.open();

        activeNotifications.add(notification);

        // Удаляем из списка, когда уведомление закрывается
        notification.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                activeNotifications.remove(notification);
            }
        });
    }
}