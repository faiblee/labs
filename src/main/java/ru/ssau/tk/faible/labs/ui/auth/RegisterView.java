package ru.ssau.tk.faible.labs.ui.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Route("register")
@PageTitle("Регистрация")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    private final TextField usernameField = new TextField("Логин");
    private final PasswordField passwordField = new PasswordField("Пароль");
    private final PasswordField confirmPasswordField = new PasswordField("Подтвердите пароль");
    private final Button registerButton = new Button("Зарегистрироваться");

    private final RestTemplate restTemplate = new RestTemplate();
    // Используем ObjectMapper для создания JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RegisterView() {
        addClassName("register-view");
        setSizeFull();

        usernameField.setRequired(true);
        passwordField.setRequired(true);
        confirmPasswordField.setRequired(true);

        registerButton.addClickListener(e -> register());

        H1 title = new H1("Регистрация");
        title.getStyle().set("text-align", "center");

        add(title, usernameField, passwordField, confirmPasswordField, registerButton);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }

    private void register() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();
        String confirmPassword = confirmPasswordField.getValue();

        if (!password.equals(confirmPassword)) {
            Notification.show("Пароли не совпадают!", 3000, Notification.Position.MIDDLE);
            return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            Notification.show("Логин и пароль обязательны!", 3000, Notification.Position.MIDDLE);
            return;
        }

        // Создаём Map для JSON-объекта
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", password);
        requestBody.put("role", "USER"); // По умолчанию

        try {
            // Сериализуем Map в JSON строку
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            // Создаём HttpEntity с JSON и заголовками
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Content-Type", "application/json");
            org.springframework.http.HttpEntity<String> request = new org.springframework.http.HttpEntity<>(jsonBody, headers);

            // Отправляем POST-запрос
            restTemplate.postForObject("http://localhost:8080/api/auth/register", request, Object.class);
            Notification.show("Регистрация успешна! Перейдите на страницу входа.", 3000, Notification.Position.MIDDLE);
            // Очистить поля
            usernameField.clear();
            passwordField.clear();
            confirmPasswordField.clear();
        } catch (RestClientException ex) {
            // Показываем ошибку, которую вернул бэкенд
            Notification.show("Ошибка регистрации: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
        } catch (JsonProcessingException e) {
            // Ошибка при сериализации JSON (маловероятно, но на всякий случай)
            Notification.show("Ошибка подготовки данных: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }
}