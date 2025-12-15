// src/main/java/ru/ssau/tk/faible/labs/ui/dialogs/FunctionsListDialog.java

package ru.ssau.tk.faible.labs.ui.dialogs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.ssau.tk.faible.labs.ui.models.CurrentUser;
import ru.ssau.tk.faible.labs.ui.models.FunctionDTO;
import ru.ssau.tk.faible.labs.ui.models.PointDTO;
import ru.ssau.tk.faible.labs.ui.utils.ExceptionHandler;
import ru.ssau.tk.faible.labs.ui.utils.NotificationManager;

import java.util.*;

public class FunctionsListDialog extends Dialog {

    private final RestTemplate restTemplate = new RestTemplate();
    private final CurrentUser currentUser;
    private final Grid<FunctionDTO> functionGrid = new Grid<>(FunctionDTO.class);
    private final VerticalLayout detailPanel = new VerticalLayout();
    private final Span applyResult = new Span();
    private FunctionDTO selectedFunction = null;
    private List<PointDTO> currentPoints = new ArrayList<>();

    public FunctionsListDialog() {
        this.currentUser = VaadinSession.getCurrent().getAttribute(CurrentUser.class);

        setWidth("95vw");
        setHeight("85vh");

        add(new H3("Управление функциями"));

        // === Левая панель: список функций ===
        VerticalLayout leftPanel = createLeftPanel();

        // === Правая панель: детали ===
        detailPanel.setSizeFull();
        detailPanel.setPadding(true);
        detailPanel.setSpacing(true);
        detailPanel.add(new H3("Выберите функцию"));

        // === Основной макет ===
        HorizontalLayout mainLayout = new HorizontalLayout(leftPanel, detailPanel);
        mainLayout.setSizeFull();
        mainLayout.setFlexGrow(0, leftPanel);
        mainLayout.setFlexGrow(1, detailPanel);

        Button closeButton = new Button("Закрыть", e -> close());
        closeButton.setWidth("100px");

        VerticalLayout content = new VerticalLayout(mainLayout, closeButton);
        content.setSizeFull();
        content.setAlignItems(FlexComponent.Alignment.END);
        add(content);

        loadFunctions();
    }

    private VerticalLayout createLeftPanel() {
        functionGrid.setColumns("name", "type");
        functionGrid.getColumnByKey("name").setHeader("Имя");
        functionGrid.getColumnByKey("type").setHeader("Тип");
        functionGrid.setHeight("70vh");
        functionGrid.setWidth("300px");

        functionGrid.addItemClickListener(event -> {
            selectedFunction = event.getItem();
            loadFunctionDetails(selectedFunction);
        });

        VerticalLayout leftPanel = new VerticalLayout(new H3("Функции"), functionGrid);
        leftPanel.setPadding(true);
        leftPanel.setWidth("320px");
        return leftPanel;
    }

    private void loadFunctionDetails(FunctionDTO func) {
        detailPanel.removeAll();

        // Заголовок
        H3 title = new H3("Функция: " + func.getName());
        detailPanel.add(title);

        // Загружаем точки
        try {
            String url = "http://localhost:8080/api/functions/" + func.getId() + "/points";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode array = mapper.readTree(response.getBody());
            currentPoints = new ArrayList<>();
            if (array.isArray()) {
                for (JsonNode node : array) {
                    currentPoints.add(new PointDTO(
                            node.get("id").asInt(),
                            node.get("xvalue").asDouble(),
                            node.get("yvalue").asDouble(),
                            node.get("functionId").asInt()
                    ));
                }
            }

            // Таблица точек
            Grid<PointDTO> pointsGrid = createPointsGrid(currentPoints);
            pointsGrid.setHeight("400px");
            detailPanel.add(new H3("Точки функции"), pointsGrid);

            // Кнопки
            Button saveButton = new Button("Сохранить изменения", e -> savePoints(func.getId()));
            Button deleteButton = new Button("Удалить функцию", e -> deleteFunction(func.getId()));
            deleteButton.getStyle().set("background-color", "var(--lumo-error-color)").set("color", "white");

            HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, deleteButton);
            buttonLayout.setSpacing(true);
            detailPanel.add(buttonLayout);

            // Вычисление f(x)
            TextField xInput = new TextField("Введите x");
            Button applyButton = new Button("Вычислить f(x)", e -> applyFunction(func.getId(), xInput.getValue()));
            applyResult.setText("");
            applyResult.getStyle().set("margin-left", "10px");

            HorizontalLayout applyLayout = new HorizontalLayout(xInput, applyButton, applyResult);
            applyLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
            applyLayout.setSpacing(true);
            VerticalLayout applyVertical = new VerticalLayout(new H3("Вычислить значение"), applyLayout);
//            detailPanel.add(, applyLayout);

            // === Добавление новой точки ===
            TextField addXField = new TextField("X");
            TextField addYField = new TextField("Y");
            Button addButton = new Button("Добавить точку", e ->
                    addPoint(func.getId(), addXField.getValue(), addYField.getValue())
            );

            HorizontalLayout addPointLayout = new HorizontalLayout(addXField, addYField, addButton);
            addPointLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
            addPointLayout.setSpacing(true);
            VerticalLayout addPointVertical = new VerticalLayout(new H3("Добавить точку"), addPointLayout);
//            detailPanel.add(new H3("Добавить точку"), addPointLayout);

            HorizontalLayout bottomLayout = new HorizontalLayout(applyVertical, addPointVertical);

            detailPanel.add(bottomLayout);
        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
        }
    }

    private Grid<PointDTO> createPointsGrid(List<PointDTO> points) {
        Grid<PointDTO> grid = new Grid<>();
        grid.setItems(points);

        // Редактируемый столбец X
        grid.addComponentColumn(item -> createEditableTextField(
                item,
                PointDTO::getXValue,
                PointDTO::setXValue
        )).setHeader("X").setAutoWidth(true);

        // Редактируемый столбец Y
        grid.addComponentColumn(item -> createEditableTextField(
                item,
                PointDTO::getYValue,
                PointDTO::setYValue
        )).setHeader("Y").setAutoWidth(true);

        return grid;
    }

    private TextField createEditableTextField(
            PointDTO item,
            ValueProvider<PointDTO, Double> valueProvider,
            Setter<PointDTO, Double> setter) {

        TextField field = new TextField();
        Double value = valueProvider.apply(item);
        field.setValue(value != null ? value.toString() : "");
        field.setWidth("120px"); // фиксированная ширина для компактности

        field.addValueChangeListener(e -> {
            String val = e.getValue();
            try {
                if (val == null || val.trim().isEmpty()) {
                    setter.accept(item, null);
                } else {
                    Double parsed = Double.parseDouble(val);
                    setter.accept(item, parsed);
                }
            } catch (NumberFormatException ex) {
                Notification.show("Введите корректное число", 3000, Notification.Position.MIDDLE);
                field.setValue("");
                setter.accept(item, null);
            }
        });

        return field;
    }

    private void addPoint(int functionId, String xStr, String yStr) {
        if (xStr == null || xStr.trim().isEmpty() || yStr == null || yStr.trim().isEmpty()) {
            NotificationManager.show("Введите оба значения x и y", 3000, Notification.Position.BOTTOM_CENTER);
            return;
        }
        try {
            double x = Double.parseDouble(xStr);
            double y = Double.parseDouble(yStr);

            // Формируем тело запроса
            Map<String, Object> pointData = new HashMap<>();
            pointData.put("xvalue", x);
            pointData.put("yvalue", y);

            String url = "http://localhost:8080/api/functions/" + functionId + "/points";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(pointData, headers);
            restTemplate.postForObject(url, request, Void.class);

            NotificationManager.show("Точка добавлена!", 3000, Notification.Position.BOTTOM_CENTER);

            // Перезагрузить точки, чтобы новая появилась в таблице
            loadFunctionDetails(selectedFunction);

        } catch (NumberFormatException e) {
            NotificationManager.show("Некорректные значения x или y", 3000, Notification.Position.BOTTOM_CENTER);
        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
        }
    }

    private void savePoints(int functionId) {
        try {
            String url = "http://localhost:8080/api/points/";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Подготавливаем данные
            for (PointDTO p : currentPoints) {
                Map<String, Object> point = new HashMap<>();
                point.put("xvalue", p.getXValue());
                point.put("yvalue", p.getYValue());
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(point, headers);

                restTemplate.put(url + p.getId(), request);
            }
            NotificationManager.show("Изменения сохранены!", 3000, Notification.Position.BOTTOM_CENTER);
        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
        }
    }

    private void deleteFunction(int functionId) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Подтверждение удаления");
        confirm.setText("Вы действительно хотите удалить функцию?");
        confirm.setCancelable(true);
        confirm.setConfirmText("Удалить");
        confirm.setRejectText("Отмена");
        confirm.addConfirmListener(e -> {
            try {
                String url = "http://localhost:8080/api/functions/" + functionId;
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
                restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
                NotificationManager.show("Функция удалена", 3000, Notification.Position.BOTTOM_CENTER);
                loadFunctions(); // Перезагрузить список
                detailPanel.removeAll();
                detailPanel.add(new H3("Выберите функцию"));
            } catch (Exception ex) {
                ExceptionHandler.notifyUser(ex);
            }
        });
        confirm.open();
    }

    private void applyFunction(int functionId, String xStr) {
        if (xStr == null || xStr.trim().isEmpty()) {
            NotificationManager.show("Введите значение x", 3000, Notification.Position.BOTTOM_CENTER);
            return;
        }
        try {
            double x = Double.parseDouble(xStr);
            String url = "http://localhost:8080/api/functions/" + functionId + "/apply?x=" + x;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            double yValue = root.get("y").asDouble();

            applyResult.setText("f(" + x + ") = " + String.format("%.4f", yValue));
        } catch (NumberFormatException e) {
            NotificationManager.show("Некорректное значение x", 3000, Notification.Position.BOTTOM_CENTER);
        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
        }
    }

    private void loadFunctions() {
        try {
            String url = "http://localhost:8080/api/functions?ownerId=" + currentUser.getId();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode array = mapper.readTree(response.getBody());
            List<FunctionDTO> functions = new ArrayList<>();
            if (array.isArray()) {
                for (JsonNode node : array) {
                    functions.add(new FunctionDTO(
                            node.get("id").asInt(),
                            node.get("name").asText(),
                            node.get("ownerId").asInt(),
                            node.get("type").asText()
                    ));
                }
            }
            functionGrid.setItems(functions);
        } catch (Exception e) {
            ExceptionHandler.notifyUser(e);
        }
    }
}