package ru.ssau.tk.faible.labs.ui.dialogs;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.server.VaadinSession;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;
import ru.ssau.tk.faible.labs.ui.models.CurrentUser;
import ru.ssau.tk.faible.labs.ui.utils.ExceptionHandler;
import ru.ssau.tk.faible.labs.ui.utils.NotificationManager;

import java.util.Base64;

@Slf4j
public class LoginDialog extends Dialog {

    private final TextField usernameField = new TextField("Логин");
    private final PasswordField passwordField = new PasswordField("Пароль");
    private final Button loginButton = new Button("Войти");

    private final RestTemplate restTemplate = new RestTemplate();

    public LoginDialog() {
        addClassName("login-dialog");
        setSizeFull();

        usernameField.setRequired(true);
        passwordField.setRequired(true);


        loginButton.addClickListener(e -> {
            login();
        } );

        H1 title = new H1("Вход");
        title.getStyle().set("text-align", "center");

        FormLayout form = new FormLayout();
        form.add(title, usernameField, passwordField);
        add(form);

        HorizontalLayout buttons = new HorizontalLayout();
        Button cancelButton = new Button("Отмена", e -> close());

        buttons.add(loginButton, cancelButton);
        add(buttons);
    }

    private void login() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            NotificationManager.show("Логин и пароль обязательны!", 3000, Notification.Position.BOTTOM_CENTER);
            return;
        }

        // Формируем заголовок Basic Auth
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        // Создаём HttpEntity с заголовком Authorization
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedCredentials);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Используем getForObject с HttpEntity
            restTemplate.execute("http://localhost:8080/api/auth/login", HttpMethod.GET,
                    request -> request.getHeaders().addAll(entity.getHeaders()),
                    ClientHttpResponse::getStatusCode);
            // Успешный вход — показываем уведомление и переходим на главную
            NotificationManager.show("Успешный вход!", 3000, Notification.Position.BOTTOM_CENTER);
            try {
                ResponseEntity<CurrentUser> response = restTemplate.exchange(
                        "http://localhost:8080/api/auth/login",
                        HttpMethod.GET,
                        entity,
                        CurrentUser.class);
                CurrentUser currentUser = response.getBody();
                if (currentUser != null) {
                    currentUser.setEncodedCredentials(encodedCredentials);
                }
                VaadinSession.getCurrent().setAttribute(CurrentUser.class, currentUser);
                UI.getCurrent().getPage().setLocation("main");
            } catch (Exception ex) {
                ExceptionHandler.notifyUser(ex);
            }
            close();
        } catch (Exception ex) {
            NotificationManager.show("Ошибка: Неверный логин или пароль", 5000, Notification.Position.BOTTOM_CENTER);
        }
    }
}
