package ru.ssau.tk.faible.labs.ui.dialogs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.ssau.tk.faible.labs.ui.models.CurrentUser;
import ru.ssau.tk.faible.labs.ui.models.FunctionDTO;
import ru.ssau.tk.faible.labs.ui.utils.ExceptionHandler;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class FunctionsListDialog extends Dialog {

    private final Grid<FunctionDTO> grid = new Grid<>(FunctionDTO.class);
    private final CurrentUser currentUser;

    public FunctionsListDialog() {
        currentUser = VaadinSession.getCurrent().getAttribute(CurrentUser.class);

        setWidth("900px");
        setHeight("600px");

        add(new H3("Мои функции"));

        Grid<FunctionDTO> grid = new Grid<>(FunctionDTO.class);
        grid.setColumns("name", "type");
        grid.getColumnByKey("name").setHeader("Имя");
        grid.getColumnByKey("type").setHeader("Тип");

        // Обработка клика по строке
        grid.addItemClickListener(event -> {
            FunctionDTO selected = event.getItem();
            FunctionDetailsDialog dialog = new FunctionDetailsDialog(selected);
            dialog.open();
        });

        loadFunctions();

        Button closeButton = new Button("Закрыть", e -> close());
        add(new VerticalLayout(grid, closeButton));
    }


    private void loadFunctions() {
        try {
            String url = "http://localhost:8080/api/functions?ownerId=" + currentUser.getId();

            // Создаём RestTemplate
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
            HttpEntity<Void> request = new HttpEntity<>(headers);
            // Выполняем GET-запрос и получаем ответ как массив JSON-объектов
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            // Парсим JSON вручную через Jackson
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());

            List<FunctionDTO> functions = new LinkedList<>();

            // Обрабатываем список функций
            if (rootNode.isArray()) {
                for (JsonNode functionNode : rootNode) {
                    int id = functionNode.get("id").asInt();
                    String name = functionNode.get("name").asText();
                    int ownerId = functionNode.get("ownerId").asInt();
                    String type = functionNode.get("type").asText();

                    functions.add(new FunctionDTO(id, name, ownerId, type));
                }
            }

            grid.setItems(functions);
        } catch (Exception e) {
            ExceptionHandler.notifyUser(e);
        }
    }
}