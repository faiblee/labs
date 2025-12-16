// src/main/java/ru/ssau/tk/faible/labs/ui/dialogs/OperationsDialog.java

package ru.ssau.tk.faible.labs.ui.dialogs;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
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

    private final Button loadJson1 = new Button(BrailleHelper.applyBrailleIfEnabled("Загрузить из JSON"));
    private final Button loadJson2 = new Button(BrailleHelper.applyBrailleIfEnabled("Загрузить из JSON"));
    private final Button calculateButton = new Button(BrailleHelper.applyBrailleIfEnabled("Вычислить"));

    // Result section
    private final Grid<PointDTO> resultGrid;
    private final List<PointDTO> resultPoints = new ArrayList<>();

    // Operation
    private final Select<String> operationSelect = new Select<>();


    public OperationsDialog() {
        this.currentUser = VaadinSession.getCurrent().getAttribute(CurrentUser.class);

        setWidth("98vw");
        setHeight("95vh");

        // === Заголовок ===
        H2 title = new H2(BrailleHelper.applyBrailleIfEnabled("Операции над функциями"));
        title.getStyle().set("margin", "0 0 1rem 0").set("font-size", "1.5em");

        // === Выбор операции (вверху) ===
        operationSelect.setLabel(BrailleHelper.applyBrailleIfEnabled("Выберите операцию"));
        operationSelect.setItems("сложение", "вычитание", "умножение", "деление");
        operationSelect.setWidth("200px");

        HorizontalLayout operationLayout = new HorizontalLayout(new Span(BrailleHelper.applyBrailleIfEnabled("Операция:")), operationSelect, calculateButton);
        operationLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        operationLayout.setSpacing(true);


        // Таблицы
        this.pointsGrid1 = createPointsGrid(points1, true);
        this.pointsGrid2 = createPointsGrid(points2, true);
        this.resultGrid = createPointsGrid(resultPoints, false);

        calculateButton.addClickListener(e -> updateResult());

        // Панели
        VerticalLayout leftPanel = createFunctionPanel(BrailleHelper.applyBrailleIfEnabled("Функция f(x)"), function1Select, pointsGrid1, loadJson1);
        VerticalLayout centerPanel = createFunctionPanel(BrailleHelper.applyBrailleIfEnabled("Функция g(x)"), function2Select, pointsGrid2, loadJson2);
        VerticalLayout rightPanel = createResultPanel();

        loadJson1.addClickListener(e -> openJsonUploadDialog(points1, pointsGrid1));
        loadJson2.addClickListener(e -> openJsonUploadDialog(points2, pointsGrid2));

        // Левая панель — для function1Select → points1
        function1Select.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                loadPointsForFunction(e.getValue().getId(), points1, pointsGrid1);
            }
        });
        loadJson1.addClickListener(e -> openJsonUploadDialog(points1, pointsGrid1));

        // Центральная панель — для function2Select → points2
        function2Select.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                loadPointsForFunction(e.getValue().getId(), points2, pointsGrid2);
            }
        });
        loadJson2.addClickListener(e -> openJsonUploadDialog(points2, pointsGrid2));

        // Основной макет
        HorizontalLayout mainLayout = new HorizontalLayout(leftPanel, centerPanel, rightPanel);
        mainLayout.setSizeFull();
        mainLayout.setFlexGrow(1, leftPanel);
        mainLayout.setFlexGrow(1, centerPanel);
        mainLayout.setFlexGrow(1, rightPanel);

        // Обертка для заголовка и операции — по центру
        VerticalLayout vert = new VerticalLayout(title, operationLayout);
        vert.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        HorizontalLayout headerSection = new HorizontalLayout(vert);
        headerSection.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // ← Центрирование
        headerSection.setSpacing(true);
        headerSection.setWidthFull();

        // Основной контент
        VerticalLayout content = new VerticalLayout(headerSection, mainLayout);
        content.setSizeFull();
        content.setSpacing(true);
        content.setPadding(true);

        Button closeButton = new Button(BrailleHelper.applyBrailleIfEnabled("Закрыть"), e -> close());
        closeButton.setWidth("100px");

        // Добавляем кнопку "Закрыть" вниз, по правому краю
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.add(closeButton);

        content.add(footer);

        add(content);
        loadFunctions();
    }

    private VerticalLayout createFunctionPanel(String title, Select<FunctionDTO> funcSelect, Grid<PointDTO> grid, Button loadJsonButton) {
        H3 panelTitle = new H3(title);
        panelTitle.getStyle().set("margin", "0 0 0.5rem 0");

        funcSelect.setLabel(BrailleHelper.applyBrailleIfEnabled("Выберите функцию"));
        funcSelect.setWidthFull();

        grid.setHeightFull();
        VerticalLayout gridWrapper = new VerticalLayout(grid);
        gridWrapper.setSizeFull();

        loadJsonButton.setWidthFull();

        VerticalLayout panel = new VerticalLayout(panelTitle, funcSelect, gridWrapper, loadJsonButton);
        panel.setPadding(true);
        panel.setSpacing(true);
        panel.setSizeFull();
        panel.setFlexGrow(1, gridWrapper);
        return panel;
    }

    private VerticalLayout createResultPanel() {
        H3 panelTitle = new H3(BrailleHelper.applyBrailleIfEnabled("Результат"));
        panelTitle.getStyle().set("margin", "0 0 0.5rem 0");

        resultGrid.setHeightFull();
        VerticalLayout gridWrapper = new VerticalLayout(resultGrid);
        gridWrapper.setPadding(false);
        gridWrapper.setSpacing(false);
        gridWrapper.setSizeFull();

        Button saveButton = new Button(BrailleHelper.applyBrailleIfEnabled("Сохранить"), e -> saveResult());
        Button exportButton = new Button(BrailleHelper.applyBrailleIfEnabled("Экспорт в JSON"), e -> exportResultToJson());
        exportButton.setWidthFull();
        saveButton.setWidthFull();

        VerticalLayout buttonLayout = new VerticalLayout(saveButton, exportButton);
        buttonLayout.setSpacing(true);
        buttonLayout.setWidthFull();

        VerticalLayout panel = new VerticalLayout(panelTitle, gridWrapper, buttonLayout);
        panel.setPadding(true);
        panel.setSpacing(true);
        panel.setSizeFull();
        panel.setFlexGrow(1, gridWrapper);
        return panel;
    }

    private void openJsonUploadDialog(List<PointDTO> targetList, Grid<PointDTO> targetGrid) {
        UploadHandler uploadHandler = UploadHandler.inMemory((metadata, data) -> {
            String fileName = metadata.fileName();
            if (!fileName.toLowerCase().endsWith(".json")) {
                Notification.show(BrailleHelper.applyBrailleIfEnabled("Файл должен иметь расширение .json"), 4000, Notification.Position.MIDDLE);
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

                    Notification.show(BrailleHelper.applyBrailleIfEnabled("Функция загружена из " + fileName), 3000, Notification.Position.BOTTOM_CENTER);
                }
            } catch (Exception ex) {
                Notification.show(BrailleHelper.applyBrailleIfEnabled("Ошибка: " + ex.getMessage()), 5000, Notification.Position.MIDDLE);
            }
        });

        Upload upload = new Upload(uploadHandler);
        upload.setMaxFiles(1);
        upload.setAcceptedFileTypes(".json");

        Button cancelButton = new Button(BrailleHelper.applyBrailleIfEnabled("Отмена"), e -> close());
        Button uploadButton = new Button(BrailleHelper.applyBrailleIfEnabled("Загрузить"));
        uploadButton.setEnabled(false); // Upload сам управляет активностью

        // Интегрируем Upload в кнопку-обёртку (чтобы не показывать стандартный стиль Upload)
        // Но проще — использовать Upload как есть
        Dialog jsonDialog = new Dialog();
        jsonDialog.setHeaderTitle(BrailleHelper.applyBrailleIfEnabled("Загрузить функцию из JSON"));
        jsonDialog.add(new VerticalLayout(new Span(BrailleHelper.applyBrailleIfEnabled("Выберите JSON-файл:")), upload));
        jsonDialog.setWidth("500px");
        jsonDialog.open();
    }

    private void exportResultToJson() {
        if (resultPoints.isEmpty()) {
            Notification.show(BrailleHelper.applyBrailleIfEnabled("Нет данных для экспорта"));
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
                Notification.show(BrailleHelper.applyBrailleIfEnabled("Введите число"), 3000, Notification.Position.MIDDLE);
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
            NotificationManager.show(BrailleHelper.applyBrailleIfEnabled("Нет результата для сохранения"), 3000, Notification.Position.BOTTOM_CENTER);
            return;
        }

        TextField nameField = new TextField(BrailleHelper.applyBrailleIfEnabled("Имя новой функции"));
        Button saveBtn = new Button(BrailleHelper.applyBrailleIfEnabled("Сохранить"), e -> {
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
                NotificationManager.show(BrailleHelper.applyBrailleIfEnabled("Функция сохранена!"), 3000, Notification.Position.BOTTOM_CENTER);
                close();
            } catch (Exception ex) {
                ExceptionHandler.notifyUser(ex);
            }
        });

        Dialog saveDialog = new Dialog(nameField, saveBtn);
        saveDialog.open();
    }
}