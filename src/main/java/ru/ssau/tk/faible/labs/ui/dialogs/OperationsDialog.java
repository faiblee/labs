// src/main/java/ru/ssau/tk/faible/labs/ui/dialogs/OperationsDialog.java

package ru.ssau.tk.faible.labs.ui.dialogs;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.streams.UploadHandler;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.ssau.tk.faible.labs.ui.models.*;
import ru.ssau.tk.faible.labs.ui.utils.BrailleHelper;
import ru.ssau.tk.faible.labs.ui.utils.ExceptionHandler;
import ru.ssau.tk.faible.labs.ui.utils.JsonFileHandler;
import ru.ssau.tk.faible.labs.ui.utils.NotificationManager;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class OperationsDialog extends Dialog {

    private final RestTemplate restTemplate = new RestTemplate();
    private final CurrentUser currentUser;

    // UI components for left and center
    private final Select<FunctionDTO> function1Select = new Select<>();
    private final Select<FunctionDTO> function2Select = new Select<>();

    private Grid<PointDTO> pointsGrid1;
    private Grid<PointDTO> pointsGrid2;
    private final List<PointDTO> points1 = new ArrayList<>();
    private final List<PointDTO> points2 = new ArrayList<>();

    private final Button loadJson1 = new Button("Загрузить из JSON");
    private final Button loadJson2 = new Button("Загрузить из JSON");

    // Result section
    private final Grid<PointDTO> resultGrid;
    private final List<PointDTO> resultPoints = new ArrayList<>();

    // Operation
    private final Select<String> operationSelect = new Select<>();

    private void openJsonUploadDialog(List<PointDTO> targetList, Grid<PointDTO> targetGrid) {
        UploadHandler uploadHandler = UploadHandler.inMemory((metadata, data) -> {
            String fileName = metadata.fileName();
            if (!fileName.toLowerCase().endsWith(".json")) {
                Notification.show("Файл должен иметь расширение .json", 4000, Notification.Position.MIDDLE);
                return;
            }

            try {
                // Десериализуем из byte[]
                try (var reader = new InputStreamReader(new ByteArrayInputStream(data), StandardCharsets.UTF_8)) {
                    FunctionJsonDTO dto = JsonFileHandler.deserializeFunction(reader); // ← нужно обновить JsonFileHandler!

                    targetList.clear();
                    if (dto.getXValues() != null && dto.getYValues() != null &&
                            dto.getXValues().size() == dto.getYValues().size()) {

                        for (int i = 0; i < dto.getXValues().size(); i++) {
                            targetList.add(new PointDTO(0, dto.getXValues().get(i), dto.getYValues().get(i), 0));
                        }
                    }

                    targetGrid.getDataProvider().refreshAll();
                    updateResult();

                    Notification.show("Функция загружена из " + fileName, 3000, Notification.Position.BOTTOM_CENTER);
                }
            } catch (Exception ex) {
                Notification.show("Ошибка: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });

        Upload upload = new Upload(uploadHandler);
        upload.setMaxFiles(1);
        upload.setAcceptedFileTypes(".json");

        Button cancelButton = new Button("Отмена", e -> close());
        Button uploadButton = new Button("Загрузить");
        uploadButton.setEnabled(false); // Upload сам управляет активностью

        // Интегрируем Upload в кнопку-обёртку (чтобы не показывать стандартный стиль Upload)
        // Но проще — использовать Upload как есть
        Dialog jsonDialog = new Dialog();
        jsonDialog.setHeaderTitle("Загрузить функцию из JSON");
        jsonDialog.add(new VerticalLayout(new Span("Выберите JSON-файл:"), upload));
        jsonDialog.setWidth("500px");
        jsonDialog.open();
    }

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

        loadJson1.addClickListener(e -> openJsonUploadDialog(points1, pointsGrid1));
        loadJson2.addClickListener(e -> openJsonUploadDialog(points2, pointsGrid2));

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

        loadJson.addClickListener(e -> openJsonUploadDialog(points1, pointsGrid1)); // для левой панели

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
        operationSelect.setItems("сложение", "вычитание", "умножение", "деление");
        operationSelect.addValueChangeListener(e -> updateResult());

        function2Select.setLabel("Функция g(x)");
        function2Select.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                loadPointsForFunction(e.getValue().getId(), points2, pointsGrid2);
            }
        });

        loadJson2.addClickListener(e -> openJsonUploadDialog(points2, pointsGrid2)); // для центральной

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

    private void exportResultToJson() {
        if (resultPoints.isEmpty()) {
            Notification.show("Нет данных для экспорта");
            return;
        }

        try {
            String json = JsonFileHandler.serializeFunction(
                    "Результат операции",
                    "Табулированная функция",
                    resultPoints
            );

            // Кодируем в base64
            String base64 = java.util.Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
            String dataUrl = "data:application/json;charset=utf-8;base64," + base64;
            String fileName = "result_function.json";

            // Генерируем и выполняем JS для скачивания
            String script = """
            const a = document.createElement('a');
            a.href = '%s';
            a.download = '%s';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
        """.formatted(dataUrl, fileName);

            getUI().ifPresent(ui -> ui.getPage().executeJs(script));

        } catch (Exception ex) {
            Notification.show("Ошибка экспорта: " + ex.getMessage());
        }
    }

    private VerticalLayout createResultPanel() {
        Button saveButton = new Button("Сохранить", e -> saveResult());
        Button exportButton = new Button("Экспорт в JSON", e -> exportResultToJson());

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
            Map<String, Object> request = new HashMap<>();
            request.put("function1Id", f1.getId());
            request.put("function2Id", f2.getId());
            request.put("operation", op);

            String url = "http://localhost:8080/api/functions/operation";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            // 👇 Используем OperationResultDTO
            ResponseEntity<OperationResponseDTO> response = restTemplate.postForEntity(url, entity, OperationResponseDTO.class);

            OperationResponseDTO result = response.getBody();
            resultPoints.clear();

            if (result != null && result.getXvalues() != null && result.getYvalues() != null) {
                double[] xVals = result.getXvalues();
                double[] yVals = result.getYvalues();

                if (xVals.length == yVals.length) {
                    for (int i = 0; i < xVals.length; i++) {
                        resultPoints.add(new PointDTO(0, xVals[i], yVals[i], 0));
                    }
                }
            }

            resultGrid.getDataProvider().refreshAll();

        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
            resultPoints.clear();
            resultGrid.getDataProvider().refreshAll();
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

                double[] xValues = xVals.stream().mapToDouble(Double::doubleValue).toArray();
                double[] yValues = yVals.stream().mapToDouble(Double::doubleValue).toArray();

                CreateFunctionDTO createFunctionDTO = new CreateFunctionDTO();

                createFunctionDTO.setXvalues(xValues);
                createFunctionDTO.setYvalues(yValues);
                createFunctionDTO.setName(name);
                createFunctionDTO.setOwnerId(currentUser.getId());
                createFunctionDTO.setType("Табулированная функция");
                createFunctionDTO.setFactory_type(currentUser.getFactory_type());

                String url = "http://localhost:8080/api/functions";
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<CreateFunctionDTO> requestEntity = new HttpEntity<>(createFunctionDTO, headers);
                try {
                    restTemplate.postForObject(url, requestEntity, Object.class);
                    close();
                } catch (Exception ex) {
                    ExceptionHandler.notifyUser(ex);
                }
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