package ru.ssau.tk.faible.labs.ui.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.ssau.tk.faible.labs.ui.models.CurrentUser;
import ru.ssau.tk.faible.labs.ui.utils.ExceptionHandler;
import ru.ssau.tk.faible.labs.ui.utils.NotificationManager;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SettingsDialog extends Dialog {

    private final RestTemplate restTemplate = new RestTemplate();
    private final TextField usernameField = new TextField("Имя пользователя");
    private final PasswordField oldPasswordField = new PasswordField("Текущий пароль");
    private final PasswordField newPasswordField = new PasswordField("Новый пароль");
    private final PasswordField confirmPasswordField = new PasswordField("Подтвердите новый пароль");

    public SettingsDialog() {
        setWidth("500px");

        add(new H3("Настройки профиля"));

        CurrentUser currentUser = VaadinSession.getCurrent().getAttribute(CurrentUser.class);
        if (currentUser == null) {
            add(new com.vaadin.flow.component.html.Div("Ошибка: пользователь не авторизован"));
            return;
        }
        usernameField.setValue(currentUser.getUsername());

        Select<String> factorySelect = new Select<>();
        factorySelect.setLabel("Фабрика функций");
        factorySelect.setItems("array factory", "linkedList factory");
        factorySelect.setValue(currentUser.getFactory_type() + " factory");

        FormLayout form = new FormLayout();
        form.add(usernameField, oldPasswordField, newPasswordField, confirmPasswordField);
        add(form);

        HorizontalLayout buttons = new HorizontalLayout();
        Button saveBtn = new Button("Сохранить", e -> saveChanges(currentUser));
        Button cancelBtn = new Button("Отмена", e -> close());
        buttons.add(saveBtn, cancelBtn);

        add(form, buttons);
    }

    private void saveChanges(CurrentUser currentUser) {
        // Создаём HttpEntity с заголовком Authorization
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//        restTemplate.put()
        // Берем значения из форм
        String newUsername = usernameField.getValue();
        String oldPassword = oldPasswordField.getValue();
        String newPassword = newPasswordField.getValue();
        String confirmPassword = confirmPasswordField.getValue();

        if (!newPassword.equals(confirmPassword)) {
            ExceptionHandler.notifyUser(new IllegalArgumentException("Новые пароли не совпадают"));
            return;
        }

        String url = "http://localhost:8080/api/users/" + currentUser.getId();

        // Создаем тело запроса
        Map<String, Object> updateRequest = new HashMap<>();
        if (!newUsername.equals(currentUser.getUsername())) {
            updateRequest.put("username", newUsername);
        }
        if (!newPassword.isEmpty() && !newPassword.equals(oldPassword)) {
            updateRequest.put("old_password", oldPassword);
            updateRequest.put("new_password", newPassword);
        }

        if (updateRequest.isEmpty()) {
            // Нечего обновлять
            close();
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(updateRequest, headers);

            restTemplate.put(url, entity);

            if (!newUsername.equals(currentUser.getUsername())) {
                currentUser.setUsername(newUsername);
            }
            // Обновляем encodedCredentials
            String auth = currentUser.getUsername() + ":" + newPassword;
            String encodedCredentials = Base64.getEncoder().encodeToString(auth.getBytes());
            currentUser.setEncodedCredentials(encodedCredentials);

            NotificationManager.show("Изменения сохранены", 3000, Notification.Position.BOTTOM_CENTER);
        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
        }
    }
}