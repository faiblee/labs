// src/main/java/ru/ssau/tk/faible/labs/ui/dialogs/OperationsDialog.java

package ru.ssau.tk.faible.labs.ui.dialogs;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.ssau.tk.faible.labs.ui.models.CurrentUser;
import ru.ssau.tk.faible.labs.ui.models.FunctionDTO;
import ru.ssau.tk.faible.labs.ui.models.OperationRequestDTO;
import ru.ssau.tk.faible.labs.ui.utils.ExceptionHandler;
import ru.ssau.tk.faible.labs.ui.utils.NotificationManager;

import java.util.List;

public class OperationsDialog extends Dialog {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Select<String> operationSelect = new Select<>();
    private final Select<FunctionDTO> function1Select = new Select<>();
    private final Select<FunctionDTO> function2Select = new Select<>();
    private final TextField resultNameField = new TextField("Имя результата");

    private final Button executeButton = new Button("Выполнить");
    private final Button cancelButton = new Button("Отмена");

    public OperationsDialog() {
        setWidth("60vw");
        setHeight("60vh");

        // Заголовок
        H2 title = new H2("Операции над функциями");
        title.getStyle().set("margin", "0 0 1rem 0").set("font-size", "1.5em");

        // Описание
        Paragraph description = new Paragraph("Выберите операцию и две функции.");
        description.getStyle().set("margin", "0 0 1rem 0").set("color", "var(--lumo-secondary-text-color)");

        // Настройка Select для операций
        operationSelect.setLabel("Операция");
        operationSelect.setItems("Сложение", "Вычитание", "Умножение", "Деление");
        operationSelect.setRequired(true);

        // Настройка Select для функций
        function1Select.setLabel("Функция 1 (f)");
        function2Select.setLabel("Функция 2 (g)");

        // Загружаем функции при открытии диалога
        loadFunctions();

        // Поле для имени результата
        resultNameField.setPlaceholder("Введите имя новой функции");
        resultNameField.setRequiredIndicatorVisible(true);

        // Форма
        FormLayout form = new FormLayout();
        form.add(title, description, operationSelect, function1Select, function2Select, resultNameField);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        form.setPadding(true);

        // Кнопки
        HorizontalLayout buttons = new HorizontalLayout(executeButton, cancelButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setWidthFull();

        add(form, buttons);

        // Обработчики событий
        executeButton.addClickListener(e -> performOperation());
        cancelButton.addClickListener(e -> close());
    }

    private void loadFunctions() {
        try {
            CurrentUser currentUser = VaadinSession.getCurrent().getAttribute(CurrentUser.class);
            if (currentUser == null) {
                NotificationManager.show("Пользователь не авторизован.", 3000, Notification.Position.BOTTOM_CENTER);
                close();
                return;
            }

            String url = "http://localhost:8080/api/functions?ownerId=" + currentUser.getId();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            // Парсинг JSON вручную (как в GraphsDialog)
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(response.getBody());

            List<FunctionDTO> functions = new java.util.LinkedList<>();

            if (rootNode.isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode node : rootNode) {
                    int id = node.get("id").asInt();
                    String name = node.get("name").asText();
                    String type = node.get("type").asText();
                    int ownerId = node.get("ownerId").asInt(); // или как у вас хранится

                    functions.add(new FunctionDTO(id, name, ownerId, type));
                }
            }

            function1Select.setItems(functions);
            function2Select.setItems(functions);

        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
            close(); // Закрыть диалог при ошибке загрузки
        }
    }

    private void performOperation() {
        String operation = operationSelect.getValue();
        FunctionDTO func1 = function1Select.getValue();
        FunctionDTO func2 = function2Select.getValue();
        String resultName = resultNameField.getValue();

        if (operation == null || func1 == null || func2 == null || resultName == null || resultName.trim().isEmpty()) {
            NotificationManager.show("Пожалуйста, заполните все поля.", 3000, Notification.Position.BOTTOM_CENTER);
            return;
        }

        try {
            CurrentUser currentUser = VaadinSession.getCurrent().getAttribute(CurrentUser.class);
            if (currentUser == null) {
                NotificationManager.show("Пользователь не авторизован.", 3000, Notification.Position.BOTTOM_CENTER);
                return;
            }

            // Подготовить DTO для запроса
            OperationRequestDTO requestDto = new OperationRequestDTO();
            requestDto.setOperation(operation); // "Сложение", "Вычитание" и т.д.
            requestDto.setFunction1Id(func1.getId());
            requestDto.setFunction2Id(func2.getId());
            requestDto.setResultName(resultName);

            String url = "http://localhost:8080/api/functions/operation"; // или другой путь

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
            headers.set("Content-Type", "application/json");

            HttpEntity<OperationRequestDTO> requestEntity = new HttpEntity<>(requestDto, headers);

            restTemplate.postForObject(url, requestEntity, Object.class);

            NotificationManager.show("Операция выполнена успешно! Результат: " + resultName, 3000, Notification.Position.BOTTOM_CENTER);
            close();

        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
        }
    }
}