package ru.ssau.tk.faible.labs.ui.auth;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Base64;

@Route("login")
@PageTitle("Вход")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final TextField usernameField = new TextField("Логин");
    private final com.vaadin.flow.component.textfield.PasswordField passwordField = new PasswordField("Пароль");
    private final Button loginButton = new Button("Войти");
    private final Paragraph errorMessage = new Paragraph();

    private final RestTemplate restTemplate = new RestTemplate();

    public LoginView() {
        addClassName("login-view");
        setSizeFull();

        usernameField.setRequired(true);
        passwordField.setRequired(true);

        errorMessage.setVisible(false);
        errorMessage.getStyle().set("color", "red");

        loginButton.addClickListener(e -> attemptLogin());

        H1 title = new H1("Лабораторная №7");
        title.getStyle().set("text-align", "center");

        VerticalLayout formLayout = new VerticalLayout(usernameField, passwordField, loginButton, errorMessage);
        formLayout.setAlignItems(Alignment.STRETCH);

        add(title, formLayout);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }

    private void attemptLogin() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            showErrorMessage("Логин и пароль обязательны!");
            return;
        }

        try {
            // Создаём Basic Auth заголовок
            String credentials = username + ":" + password;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + encodedCredentials);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Проверяем доступ к защищённому ресурсу (аналог "входа")
            // ЗАМЕНИТЕ НА РЕАЛЬНЫЙ ЗАЩИЩЁННЫЙ URL BACKEND
            String protectedUrl = "http://localhost:8080/api/users";

            // Отправляем GET-запрос с Basic Auth заголовком
            ResponseEntity<String> response = restTemplate.exchange(protectedUrl, HttpMethod.GET, entity, String.class);

            int statusCode = response.getStatusCodeValue();
            if (statusCode == 200) {
                // УСПЕШНЫЙ ВХОД: пользователь аутентифицирован
                // Сохраняем логин и пароль (или токен, если бы использовался) в сессии Vaadin
                // ВАЖНО: Хранить пароль в открытом виде в сессии НЕБЕЗОПАСНО.
                // Лучше хранить только факт аутентификации или хешированный токен.
                // Для простоты в этом примере сохраним их как есть, но это НЕ РЕКОМЕНДУЕТСЯ для продакшена.
                // Вместо этого, можно использовать Spring Security Session, но тогда Vaadin должен интегрироваться с ней.
                // ВАРИАНТ: Хранить закодированные credentials в сессии и использовать их для каждого API вызова.
                com.vaadin.flow.server.VaadinSession.getCurrent().setAttribute("basic_auth_encoded", encodedCredentials);

                getUI().ifPresent(ui -> ui.navigate("")); // Переход на главную
            } else {
                showErrorMessage("Ошибка входа: сервер вернул статус " + statusCode);
            }

        } catch (RestClientException ex) {
            // Если запрос вернул 401, значит, логин/пароль неверны
            if (ex.getMessage().contains("401")) {
                showErrorMessage("Неверный логин или пароль.");
            } else {
                showErrorMessage("Ошибка входа: " + ex.getMessage());
            }
        }
    }

    private void showErrorMessage(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(true);
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        errorMessage.setVisible(false);
    }
}