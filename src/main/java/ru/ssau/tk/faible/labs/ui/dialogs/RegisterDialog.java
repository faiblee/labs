package ru.ssau.tk.faible.labs.ui.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.ssau.tk.faible.labs.ui.models.UserRegistrationDTO;
import ru.ssau.tk.faible.labs.ui.utils.BrailleHelper;
import ru.ssau.tk.faible.labs.ui.utils.ExceptionHandler;
import ru.ssau.tk.faible.labs.ui.utils.NotificationManager;


public class RegisterDialog extends Dialog {

    private final TextField usernameField = new TextField(BrailleHelper.applyBrailleIfEnabled("Логин"));
    private final PasswordField passwordField = new PasswordField(BrailleHelper.applyBrailleIfEnabled("Пароль"));
    private final PasswordField confirmPasswordField = new PasswordField(BrailleHelper.applyBrailleIfEnabled("Подтвердите пароль"));
    private final Button registerButton = new Button(BrailleHelper.applyBrailleIfEnabled("Зарегистрироваться"));

    private final RestTemplate restTemplate = new RestTemplate();

    public RegisterDialog() {
        addClassName(BrailleHelper.applyBrailleIfEnabled("register-view"));
        setSizeFull();

        usernameField.setRequired(true);
        passwordField.setRequired(true);
        confirmPasswordField.setRequired(true);

        registerButton.addClickListener(e -> {
            register();
        });

        H1 title = new H1(BrailleHelper.applyBrailleIfEnabled("Регистрация"));
        title.getStyle().set("text-align", "center");

        FormLayout form = new FormLayout();
        form.add(title, usernameField, passwordField, confirmPasswordField);
        add(form);

        HorizontalLayout buttons = new HorizontalLayout();
        Button cancelButton = new Button(BrailleHelper.applyBrailleIfEnabled("Отмена"), e -> close());

        buttons.add(registerButton, cancelButton);
        add(buttons);
    }

    private void register() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();
        String confirmPassword = confirmPasswordField.getValue();

        if (!password.equals(confirmPassword)) {
            NotificationManager.show(BrailleHelper.applyBrailleIfEnabled("Пароли не совпадают!"), 3000, Notification.Position.BOTTOM_CENTER);
            return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            NotificationManager.show(BrailleHelper.applyBrailleIfEnabled("Логин и пароль обязательны!"), 3000, Notification.Position.BOTTOM_CENTER);
            return;
        }

        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername(username);
        dto.setPassword(password);
        // По умолчанию роль USER
        dto.setRole("USER");

        try {
            restTemplate.postForObject("http://localhost:8080/api/auth/register", dto, Object.class);
            NotificationManager.show(BrailleHelper.applyBrailleIfEnabled("Регистрация успешна! Перейдите на страницу входа"), 3000, Notification.Position.BOTTOM_CENTER);
            // Очистить поля
            usernameField.clear();
            passwordField.clear();
            confirmPasswordField.clear();
            close();
        } catch (RestClientException ex) {
            // Показываем ошибку, которую вернул бэкенд (например, "Username already exists")
            ExceptionHandler.notifyUser(ex);
        }
    }
}
