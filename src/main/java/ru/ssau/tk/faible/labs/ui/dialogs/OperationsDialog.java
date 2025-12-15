// src/main/java/ru/ssau/tk/faible/labs/ui/dialogs/OperationsDialog.java

package ru.ssau.tk.faible.labs.ui.dialogs;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.ssau.tk.faible.labs.ui.models.CurrentUser;
import ru.ssau.tk.faible.labs.ui.models.FunctionDTO;
import ru.ssau.tk.faible.labs.ui.models.PointDTO;
import ru.ssau.tk.faible.labs.ui.utils.BrailleHelper;
import ru.ssau.tk.faible.labs.ui.utils.ExceptionHandler;
import ru.ssau.tk.faible.labs.ui.utils.NotificationManager;

import java.util.*;

public class OperationsDialog extends Dialog {

    private final RestTemplate restTemplate = new RestTemplate();
    private final CurrentUser currentUser;

    // UI components for left and center
    private Select<FunctionDTO> function1Select = new Select<>();
    private Select<FunctionDTO> function2Select = new Select<>();
    private Button loadJson1 = new Button("Загрузить из JSON");
    private Button loadJson2 = new Button("Загрузить из JSON");

    private Grid<PointDTO> pointsGrid1;
    private Grid<PointDTO> pointsGrid2;
    private List<PointDTO> points1 = new ArrayList<>();
    private List<PointDTO> points2 = new ArrayList<>();

    // Result section
    private Grid<PointDTO> resultGrid;
    private List<PointDTO> resultPoints = new ArrayList<>();

    // Operation
    private Select<String> operationSelect = new Select<>();

    public OperationsDialog() {
        this.currentUser = VaadinSession.getCurrent().getAttribute(CurrentUser.class);

        setWidth("95vw");
        setHeight("90vh");

        add(new H3(BrailleHelper.applyBrailleIfEnabled("Операции над функциями")));

        // Make grids accessible
        this.pointsGrid1 = createPointsGrid(points1, true);
        this.pointsGrid2 = createPointsGrid(points2, true);
        this.resultGrid = createPointsGrid(resultPoints, false);

        // === Левая панель: функция 1 ===
        VerticalLayout leftPanel = createFunctionPanel("Функция f(x)", function1Select, pointsGrid1, loadJson1);
        // === Центральная панель: операция + функция 2 ===
        VerticalLayout centerPanel = createCenterPanel();
        // === Правая панель: результат ===
        VerticalLayout rightPanel = createResultPanel();

        HorizontalLayout mainLayout = new HorizontalLayout(leftPanel, centerPanel, rightPanel);
        mainLayout.setSizeFull();
        mainLayout.setFlexGrow(1, leftPanel);
        mainLayout.setFlexGrow(1, centerPanel);
        mainLayout.setFlexGrow(1, rightPanel);

        Button closeButton = new Button("Закрыть", e -> close());
        closeButton.setWidth("100px");

        VerticalLayout content = new VerticalLayout(mainLayout, closeButton);
        content.setSizeFull();
        content.setAlignItems(FlexComponent.Alignment.END);
        add(content);

        loadFunctions();
    }

    private VerticalLayout createFunctionPanel(String title, Select<FunctionDTO> funcSelect, Grid<PointDTO> pointsGrid, Button loadJson) {
        funcSelect.setLabel(BrailleHelper.applyBrailleIfEnabled(title));
        funcSelect.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                loadPointsForFunction(e.getValue().getId(), points1, pointsGrid);
            }
        });

        loadJson.addClickListener(e -> Notification.show("Загрузка из JSON — позже"));

        VerticalLayout controls = new VerticalLayout(funcSelect, loadJson);
        controls.setSpacing(true);
        controls.setWidthFull();

        VerticalLayout gridWrapper = new VerticalLayout(pointsGrid);
        gridWrapper.setHeight("300px");
        gridWrapper.setWidthFull();

        VerticalLayout panel = new VerticalLayout(new H3(title), controls, gridWrapper);
        panel.setPadding(true);
        panel.setSpacing(true);
        panel.setWidthFull();
        return panel;
    }

    private VerticalLayout createCenterPanel() {
        operationSelect.setLabel("Операция");
        operationSelect.setItems("Сложение", "Вычитание", "Умножение", "Деление");
        operationSelect.addValueChangeListener(e -> updateResult());

        function2Select.setLabel("Функция g(x)");
        function2Select.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                loadPointsForFunction(e.getValue().getId(), points2, pointsGrid2);
            }
        });

        loadJson2.addClickListener(e -> Notification.show("Загрузка из JSON — позже"));

        VerticalLayout controls = new VerticalLayout(operationSelect, function2Select, loadJson2);
        controls.setSpacing(true);
        controls.setWidthFull();

        VerticalLayout gridWrapper = new VerticalLayout(pointsGrid2);
        gridWrapper.setHeight("300px");
        gridWrapper.setWidthFull();

        VerticalLayout panel = new VerticalLayout(new H3("Операция и функция g(x)"), controls, gridWrapper);
        panel.setPadding(true);
        panel.setSpacing(true);
        panel.setWidthFull();
        return panel;
    }

    private VerticalLayout createResultPanel() {
        Button saveButton = new Button("Сохранить", e -> saveResult());
        Button exportButton = new Button("Экспорт в JSON", e -> Notification.show("Экспорт — позже"));

        VerticalLayout buttons = new VerticalLayout(saveButton, exportButton);
        buttons.setSpacing(true);
        buttons.setWidthFull();

        VerticalLayout gridWrapper = new VerticalLayout(resultGrid);
        gridWrapper.setHeight("400px");
        gridWrapper.setWidthFull();

        VerticalLayout panel = new VerticalLayout(new H3("Результат"), gridWrapper, buttons);
        panel.setPadding(true);
        panel.setSpacing(true);
        panel.setWidthFull();
        return panel;
    }

    private Grid<PointDTO> createPointsGrid(List<PointDTO> points, boolean editable) {
        Grid<PointDTO> grid = new Grid<>();
        ListDataProvider<PointDTO> dataProvider = new ListDataProvider<>(points);
        grid.setDataProvider(dataProvider);

        if (editable) {
            grid.addComponentColumn(item -> createEditableTextField(item, PointDTO::getXValue, PointDTO::setXValue))
                    .setHeader("X").setAutoWidth(true);
            grid.addComponentColumn(item -> createEditableTextField(item, PointDTO::getYValue, PointDTO::setYValue))
                    .setHeader("Y").setAutoWidth(true);
        } else {
            grid.addColumn(PointDTO::getXValue).setHeader("X").setAutoWidth(true);
            grid.addColumn(PointDTO::getYValue).setHeader("Y").setAutoWidth(true);
        }

        return grid;
    }

    private TextField createEditableTextField(
            PointDTO item,
            ValueProvider<PointDTO, Double> valueProvider,
            Setter<PointDTO, Double> setter) {
        TextField field = new TextField();
        Double value = valueProvider.apply(item);
        field.setValue(value != null ? value.toString() : "");
        field.setWidth("100px");
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
                Notification.show("Введите число", 3000, Notification.Position.MIDDLE);
                field.setValue("");
                setter.accept(item, null);
            }
        });
        return field;
    }

    private void loadFunctions() {
        try {
            String url = "http://localhost:8080/api/functions?ownerId=" + currentUser.getId();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            var array = mapper.readTree(response.getBody());
            List<FunctionDTO> functions = new ArrayList<>();
            if (array.isArray()) {
                for (var node : array) {
                    functions.add(new FunctionDTO(
                            node.get("id").asInt(),
                            node.get("name").asText(),
                            node.get("ownerId").asInt(),
                            node.get("type").asText()
                    ));
                }
            }
            function1Select.setItems(functions);
            function2Select.setItems(functions);
        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
        }
    }

    private void loadPointsForFunction(int functionId, List<PointDTO> targetList, Grid<PointDTO> targetGrid) {
        try {
            String url = "http://localhost:8080/api/functions/" + functionId + "/points";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            var array = mapper.readTree(response.getBody());
            targetList.clear();
            if (array.isArray()) {
                for (var node : array) {
                    targetList.add(new PointDTO(
                            node.get("id").asInt(),
                            node.get("xvalue").asDouble(),
                            node.get("yvalue").asDouble(),
                            node.get("functionId").asInt()
                    ));
                }
            }


            targetGrid.getDataProvider().refreshAll();

            updateResult();

        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
        }
    }

    private void updateResult() {
        FunctionDTO f1 = function1Select.getValue();
        FunctionDTO f2 = function2Select.getValue();
        String op = operationSelect.getValue();

        if (f1 == null || f2 == null || op == null) {
            resultPoints.clear();
            resultGrid.getDataProvider().refreshAll();
            return;
        }

        try {
            // Build request
            Map<String, Object> request = new HashMap<>();
            request.put("function1Id", f1.getId());
            request.put("function2Id", f2.getId());
            request.put("operation", op);

            String url = "http://localhost:8080/api/functions/operation/preview";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            var root = mapper.readTree(response.getBody());
            resultPoints.clear();
            if (root.isArray()) {
                for (var node : root) {
                    resultPoints.add(new PointDTO(
                            0,
                            node.get("x").asDouble(),
                            node.get("y").asDouble(),
                            0
                    ));
                }
            }
            resultGrid.getDataProvider().refreshAll();

        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
        }
    }

    private void saveResult() {
        if (resultPoints.isEmpty()) {
            NotificationManager.show("Нет результата для сохранения", 3000, Notification.Position.BOTTOM_CENTER);
            return;
        }

        TextField nameField = new TextField("Имя новой функции");
        Button saveBtn = new Button("Сохранить", e -> {
            String name = nameField.getValue();
            if (name == null || name.trim().isEmpty()) {
                Notification.show("Введите имя");
                return;
            }

            try {
                // Collect x and y arrays
                List<Double> xVals = new ArrayList<>();
                List<Double> yVals = new ArrayList<>();
                for (PointDTO p : resultPoints) {
                    xVals.add(p.getXValue());
                    yVals.add(p.getYValue());
                }

                Map<String, Object> body = new HashMap<>();
                body.put("name", name);
                body.put("xvalues", xVals);
                body.put("yvalues", yVals);

                String url = "http://localhost:8080/api/functions/tabulated";
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);

                restTemplate.postForObject(url, req, Void.class);
                NotificationManager.show("Функция сохранена!", 3000, Notification.Position.BOTTOM_CENTER);
                close();
            } catch (Exception ex) {
                ExceptionHandler.notifyUser(ex);
            }
        });

        Dialog saveDialog = new Dialog(nameField, saveBtn);
        saveDialog.open();
    }
}