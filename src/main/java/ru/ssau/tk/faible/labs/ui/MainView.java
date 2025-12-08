package ru.ssau.tk.faible.labs.ui;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed; // Пока разрешаем, чтобы проверить сессию
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Route("") // Маршрут по умолчанию
@PageTitle("Главная")
@AnonymousAllowed // Разрешаем, чтобы можно было проверить сессию
public class MainView extends VerticalLayout {

    private final RestTemplate restTemplate = new RestTemplate();

    public MainView() {
        // Проверяем, аутентифицирован ли пользователь
        if (!isUserAuthenticated()) {
            // Если нет, перенаправляем на login
            getUI().ifPresent(ui -> ui.navigate("login"));
            return; // Прекращаем выполнение конструктора, если не авторизован
        }

        // Если авторизован, добавляем основной контент
        add(new H2("Добро пожаловать в Лабораторную №7!"));
        // Добавьте остальные компоненты главной страницы
    }

    private boolean isUserAuthenticated() {
        // Получаем закодированные credentials из сессии
        String encodedCredentials = (String) com.vaadin.flow.server.VaadinSession.getCurrent().getAttribute("basic_auth_encoded");

        if (encodedCredentials == null) {
            // Если в сессии нет данных, пользователь не аутентифицирован
            return false;
        }

        try {
            // Формируем заголовок
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + encodedCredentials);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Отправляем GET-запрос с Basic Auth заголовком
            // ЗАМЕНИТЕ НА РЕАЛЬНЫЙ ЗАЩИЩЁННЫЙ URL BACKEND
            String protectedUrl = "http://localhost:8080/api/users";
            ResponseEntity<String> response = restTemplate.exchange(protectedUrl, HttpMethod.GET, entity, String.class);

            // Если запрос успешен (200 OK), значит, пользователь аутентифицирован
            return response.getStatusCodeValue() == 200;

        } catch (RestClientException e) {
            // Если запрос вернул 401 или другую ошибку, пользователь не аутентифицирован
            // Удаляем невалидные credentials из сессии
            com.vaadin.flow.server.VaadinSession.getCurrent().setAttribute("basic_auth_encoded", null);
            return false;
        }
    }
}