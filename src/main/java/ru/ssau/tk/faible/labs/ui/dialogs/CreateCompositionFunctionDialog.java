// src/main/java/ru/ssau/tk/faible/labs/ui/dialogs/CreateCompositionFunctionDialog.java

package ru.ssau.tk.faible.labs.ui.dialogs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.ssau.tk.faible.labs.ui.models.CurrentUser;
import ru.ssau.tk.faible.labs.ui.models.FunctionDTO;
import ru.ssau.tk.faible.labs.ui.utils.BrailleHelper;
import ru.ssau.tk.faible.labs.ui.utils.ExceptionHandler;
import ru.ssau.tk.faible.labs.ui.utils.NotificationManager;

import java.util.LinkedList;
import java.util.List;

public class CreateCompositionFunctionDialog extends Dialog {

    private final RestTemplate restTemplate = new RestTemplate();
    private final CurrentUser currentUser;

    private final ComboBox<FunctionDTO> outerFunctionField = new ComboBox<>(BrailleHelper.applyBrailleIfEnabled("Внешняя функция f(x)"));
    private final ComboBox<FunctionDTO> innerFunctionField = new ComboBox<>(BrailleHelper.applyBrailleIfEnabled("Внутренняя функция g(x)"));
    private final TextField nameField = new TextField(BrailleHelper.applyBrailleIfEnabled("Имя новой функции"));

    private final Button createButton = new Button(BrailleHelper.applyBrailleIfEnabled("Создать"));
    private final Button cancelButton = new Button(BrailleHelper.applyBrailleIfEnabled("Отмена"));

    public CreateCompositionFunctionDialog() {
        currentUser = VaadinSession.getCurrent().getAttribute(CurrentUser.class);

        setWidth("60vw");
        setHeight("60vh");

        H2 title = new H2(BrailleHelper.applyBrailleIfEnabled("Создание композиции функций"));
        title.getStyle().set("margin", "0 0 1rem 0").set("font-size", "1.5em");

        Paragraph description = new Paragraph(BrailleHelper.applyBrailleIfEnabled("Выберите внешнюю и внутреннюю функции для создания f(g(x))."));
        description.getStyle().set("margin", "0 0 1rem 0").set("color", "var(--lumo-secondary-text-color)");

        // Загрузка функций
        loadFunctions();

        FormLayout form = new FormLayout();
        form.add(title, description, nameField, outerFunctionField, innerFunctionField);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        form.addClassName("custom-padding"); // ✅ Добавляем свой класс

        HorizontalLayout buttons = new HorizontalLayout(createButton, cancelButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setWidthFull();

        add(form, buttons);

        createButton.addClickListener(e -> createCompositionFunction());
        cancelButton.addClickListener(e -> close());
    }

    private void loadFunctions() {
        try {
            int currentUserId = currentUser.getId();
            String url = "http://localhost:8080/api/functions?ownerId=" + currentUserId;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());

            List<FunctionDTO> functions = new LinkedList<>();

            if (rootNode.isArray()) {
                for (JsonNode functionNode : rootNode) {
                    int id = functionNode.get("id").asInt();
                    String name = functionNode.get("name").asText();
                    int ownerId = functionNode.get("ownerId").asInt();
                    String type = functionNode.get("type").asText();

                    functions.add(new FunctionDTO(id, name, ownerId, type));
                }
            }

            outerFunctionField.setItems(functions);
            innerFunctionField.setItems(functions);

        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
        }
    }

    private void createCompositionFunction() {
        String name = nameField.getValue();
        FunctionDTO outerFunc = outerFunctionField.getValue();
        FunctionDTO innerFunc = innerFunctionField.getValue();

        if (name == null || name.trim().isEmpty()) {
            NotificationManager.show(BrailleHelper.applyBrailleIfEnabled("Пожалуйста, введите имя функции!"), 3000, Notification.Position.BOTTOM_CENTER);
            return;
        }
        if (outerFunc == null || innerFunc == null) {
            NotificationManager.show(BrailleHelper.applyBrailleIfEnabled("Пожалуйста, выберите обе функции!"), 3000, Notification.Position.BOTTOM_CENTER);
            return;
        }

        // Подготовка DTO для отправки
        // Предположим, что на бэкенде есть DTO CompositionFunctionDTO
        // public class CompositionFunctionDTO {
        //     private String name;
        //     private int outerFunctionId;
        //     private int innerFunctionId;
        //     // геттеры/сеттеры
        // }

        // Используем Map для передачи данных, чтобы не создавать лишний DTO, если не планируется
        java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("name", name);
        requestBody.put("outerFunctionId", outerFunc.getId());
        requestBody.put("innerFunctionId", innerFunc.getId());

        try {
            String url = "http://localhost:8080/api/functions/composition"; // Новый эндпоинт

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
            headers.set("Content-Type", "application/json");
            HttpEntity<java.util.Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Отправляем POST-запрос на создание композиции
            restTemplate.postForObject(url, request, Void.class);

            NotificationManager.show(BrailleHelper.applyBrailleIfEnabled("Композиция функций '" + name + "' = f(g(x)) создана!"), 3000, Notification.Position.BOTTOM_CENTER);
            close();

        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
        }
    }
}