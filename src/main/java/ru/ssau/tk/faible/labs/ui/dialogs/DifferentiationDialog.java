// src/main/java/ru/ssau/tk/faible/labs/ui/dialogs/DifferentiationDialog.java

package ru.ssau.tk.faible.labs.ui.dialogs;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
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
import ru.ssau.tk.faible.labs.functions.MathFunction;
import ru.ssau.tk.faible.labs.functions.TabulatedFunction;
import ru.ssau.tk.faible.labs.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.operations.LeftSteppingDifferentialOperator;
import ru.ssau.tk.faible.labs.ui.models.CurrentUser;
import ru.ssau.tk.faible.labs.ui.models.FunctionDTO;
import ru.ssau.tk.faible.labs.ui.models.PointDTO;
import ru.ssau.tk.faible.labs.ui.utils.BrailleHelper;
import ru.ssau.tk.faible.labs.ui.utils.ExceptionHandler;
import ru.ssau.tk.faible.labs.ui.utils.NotificationManager;

import java.util.*;

public class DifferentiationDialog extends Dialog {

    private final RestTemplate restTemplate = new RestTemplate();
    private final CurrentUser currentUser;

    private final Select<FunctionDTO> functionSelect = new Select<>();
    private final Grid<PointDTO> inputGrid;
    private final Grid<PointDTO> resultGrid;

    private final List<PointDTO> inputPoints = new ArrayList<>();
    private final List<PointDTO> resultPoints = new ArrayList<>();

    private final Button differentiateButton = new Button(BrailleHelper.applyBrailleIfEnabled("Продифференцировать"));

    public DifferentiationDialog() {
        this.currentUser = VaadinSession.getCurrent().getAttribute(CurrentUser.class);

        setWidth("95vw");
        setHeight("85vh");
        add(new H3(BrailleHelper.applyBrailleIfEnabled("Дифференцирование функции")));

        this.inputGrid = createPointsGrid(inputPoints, true);
        this.resultGrid = createPointsGrid(resultPoints, false);

        VerticalLayout leftPanel = createInputPanel();
        VerticalLayout rightPanel = createResultPanel();

        HorizontalLayout mainLayout = new HorizontalLayout(leftPanel, rightPanel);
        mainLayout.setSizeFull();
        mainLayout.setFlexGrow(1, leftPanel);
        mainLayout.setFlexGrow(1, rightPanel);

        Button closeButton = new Button(BrailleHelper.applyBrailleIfEnabled("Закрыть"), e -> close());
        closeButton.setWidth("100px");

        VerticalLayout content = new VerticalLayout(mainLayout, closeButton);
        content.setSizeFull();
        content.setAlignItems(FlexComponent.Alignment.END);
        add(content);

        loadFunctions();
    }

    private VerticalLayout createInputPanel() {
        functionSelect.setLabel(BrailleHelper.applyBrailleIfEnabled("Выберите функцию"));
        functionSelect.setWidthFull();
        functionSelect.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                loadPointsForFunction(e.getValue().getId(), inputPoints, inputGrid);
            }
        });

        inputGrid.setHeightFull();
        VerticalLayout gridWrapper = new VerticalLayout(inputGrid);
        gridWrapper.setSizeFull();

        VerticalLayout panel = new VerticalLayout(
                new H3(BrailleHelper.applyBrailleIfEnabled("Исходная функция")),
                functionSelect,
                gridWrapper
        );
        panel.setPadding(true);
        panel.setSpacing(true);
        panel.setSizeFull();
        panel.setFlexGrow(1, gridWrapper);
        return panel;
    }

    private VerticalLayout createResultPanel() {
        differentiateButton.addClickListener(e -> performDifferentiation());

        resultGrid.setHeightFull();
        VerticalLayout gridWrapper = new VerticalLayout(resultGrid);
        gridWrapper.setSizeFull();

        VerticalLayout panel = new VerticalLayout(
                new H3(BrailleHelper.applyBrailleIfEnabled("Производная")),
                gridWrapper,
                differentiateButton
        );
        panel.setPadding(true);
        panel.setSpacing(true);
        panel.setSizeFull();
        panel.setFlexGrow(1, gridWrapper);
        return panel;
    }

    private Grid<PointDTO> createPointsGrid(List<PointDTO> points, boolean editable) {
        Grid<PointDTO> grid = new Grid<>();
        grid.setDataProvider(new ListDataProvider<>(points));

        if (editable) {
            grid.addComponentColumn(item -> createEditableTextField(item, PointDTO::getXValue, PointDTO::setXValue))
                    .setHeader(BrailleHelper.applyBrailleIfEnabled("X")).setAutoWidth(true);
            grid.addComponentColumn(item -> createEditableTextField(item, PointDTO::getYValue, PointDTO::setYValue))
                    .setHeader(BrailleHelper.applyBrailleIfEnabled("Y")).setAutoWidth(true);
        } else {
            grid.addColumn(PointDTO::getXValue).setHeader(BrailleHelper.applyBrailleIfEnabled("X")).setAutoWidth(true);
            grid.addColumn(PointDTO::getYValue).setHeader(BrailleHelper.applyBrailleIfEnabled("Y")).setAutoWidth(true);
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

            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
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
            functionSelect.setItems(functions);
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
        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
        }
    }

    private void performDifferentiation() {
        if (inputPoints.isEmpty()) {
            NotificationManager.show(BrailleHelper.applyBrailleIfEnabled("Выберите функцию для дифференцирования"), 3000, Notification.Position.BOTTOM_CENTER);
            return;
        }

        try {
            // Подготавливаем x и y массивы
            double[] xValues = inputPoints.stream().mapToDouble(PointDTO::getXValue).toArray();
            double[] yValues = inputPoints.stream().mapToDouble(PointDTO::getYValue).toArray();

            // Создаём TabulatedFunction
            TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
            TabulatedFunction original = factory.create(xValues, yValues);

            // Применяем дифференцирование
            LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.1);
            MathFunction derivative = operator.derive(original);

            // Собираем точки производной
            resultPoints.clear();
            for (int i = 0; i < original.getCount(); i++) {
                double x = original.getX(i);
                double y = derivative.apply(x); // производная в точке x
                resultPoints.add(new PointDTO(0, x, y, 0));
            }

            resultGrid.getDataProvider().refreshAll();

        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
        }
    }
}