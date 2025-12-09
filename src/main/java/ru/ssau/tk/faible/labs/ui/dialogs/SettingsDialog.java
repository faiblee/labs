package ru.ssau.tk.faible.labs.ui.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.ssau.tk.faible.labs.ui.models.CurrentUser;
import ru.ssau.tk.faible.labs.ui.utils.ExceptionHandler;
import ru.ssau.tk.faible.labs.ui.utils.NotificationManager;

import java.io.Serial;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SettingsDialog extends Dialog {

    @Serial
    private static final long serialVersionUID = -3038251719462255126L;
    private final RestTemplate restTemplate = new RestTemplate();
    private final TextField usernameField = new TextField("Имя пользователя");
    private final PasswordField oldPasswordField = new PasswordField("Текущий пароль");
    private final PasswordField newPasswordField = new PasswordField("Новый пароль");
    private final PasswordField confirmPasswordField = new PasswordField("Подтвердите новый пароль");
    private final Select<String> factorySelect = new Select<>();

    public SettingsDialog() {
        setWidth("500px");

        add(new H3("Настройки профиля"));

        CurrentUser currentUser = VaadinSession.getCurrent().getAttribute(CurrentUser.class);
        if (currentUser == null) {
            add(new com.vaadin.flow.component.html.Div("Ошибка: пользователь не авторизован"));
            return;
        }
        usernameField.setValue(currentUser.getUsername());

        factorySelect.setLabel("Фабрика функций");
        factorySelect.setItems("array factory", "linkedList factory");
        log.info("Factory type: {}", currentUser.getFactory_type());
        factorySelect.setValue(currentUser.getFactory_type() + " factory");

        FormLayout form = new FormLayout();
        form.add(usernameField, factorySelect, oldPasswordField, newPasswordField, confirmPasswordField);
        add(form);

        HorizontalLayout buttons = new HorizontalLayout();
        Button saveBtn = new Button("Сохранить", e -> saveChanges(currentUser));
        Button cancelBtn = new Button("Отмена", e -> close());
        buttons.add(saveBtn, cancelBtn);

        add(form, buttons);
    }

    private void saveChanges(CurrentUser currentUser) {
        // Берем значения из форм
        String newUsername = usernameField.getValue();
        String oldPassword = oldPasswordField.getValue();
        String newPassword = newPasswordField.getValue();
        String confirmPassword = confirmPasswordField.getValue();
        String factoryTypeFromSelect = factorySelect.getValue();

        if (!newPassword.equals(confirmPassword)) {
            ExceptionHandler.notifyUser(new IllegalArgumentException("Новые пароли не совпадают"));
            return;
        }

        String factory_type = factoryTypeFromSelect.split(" ")[0]; // array или linkedList

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
        if (!factory_type.equals(currentUser.getFactory_type())) {
            updateRequest.put("factory_type", factory_type);
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
            if (!factory_type.equals(currentUser.getFactory_type())) {
                currentUser.setFactory_type(factory_type);
            }

            String credentials = new String(Base64.getDecoder().decode(currentUser.getEncodedCredentials()), StandardCharsets.UTF_8);
            String[] parts = credentials.split(":", 2); // декодируем данные и разбиваем логин с паролем
            String passwordFromHeader = parts[1];

            if (newPassword.isEmpty() || oldPassword.isEmpty()) { // если меняли только username
                // Обновляем encodedCredentials
                String auth = currentUser.getUsername() + ":" + passwordFromHeader;
                String encodedCredentials = Base64.getEncoder().encodeToString(auth.getBytes());
                currentUser.setEncodedCredentials(encodedCredentials);
            } else { // если меняли и пароль
                String auth = currentUser.getUsername() + ":" + newPassword;
                String encodedCredentials = Base64.getEncoder().encodeToString(auth.getBytes());
                currentUser.setEncodedCredentials(encodedCredentials);
            }

            NotificationManager.show("Изменения сохранены", 3000, Notification.Position.BOTTOM_CENTER);
        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
        }
    }
}