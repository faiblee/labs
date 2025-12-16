// src/main/java/ru/ssau/tk/faible/labs/ui/dialogs/GraphsDialog.java

package ru.ssau.tk.faible.labs.ui.dialogs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.ssau.tk.faible.labs.ui.components.ChartComponent;
import ru.ssau.tk.faible.labs.ui.models.CurrentUser;
import ru.ssau.tk.faible.labs.ui.models.FunctionDTO;
import ru.ssau.tk.faible.labs.ui.models.PointDTO;
import ru.ssau.tk.faible.labs.ui.utils.BrailleHelper;
import ru.ssau.tk.faible.labs.ui.utils.ExceptionHandler;
import ru.ssau.tk.faible.labs.ui.utils.NotificationManager;

import java.util.LinkedList;
import java.util.List;

public class GraphsDialog extends Dialog {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Grid<FunctionDTO> functionGrid = new Grid<>();
    private final ChartComponent chartComponent = new ChartComponent();
    private final CurrentUser currentUser;
    private final H3 chartTitle = new H3(BrailleHelper.applyBrailleIfEnabled("Выберите функцию для отображения графика"));

    public GraphsDialog() {
        setWidth("95vw");
        setHeight("90vh");

        currentUser = VaadinSession.getCurrent().getAttribute(CurrentUser.class);

        // === Левая панель: список функций ===
        VerticalLayout leftPanel = createLeftPanel();

        // === Правая панель: график ===
        VerticalLayout rightPanel = createRightPanel();

        // === Основной макет: 2 колонки ===
        HorizontalLayout mainLayout = new HorizontalLayout(leftPanel, rightPanel);
        mainLayout.setSizeFull();
        mainLayout.setFlexGrow(0, leftPanel);   // левая панель — фиксированная ширина
        mainLayout.setFlexGrow(1, rightPanel);  // правая — растягивается

        // === Кнопка закрытия (внизу, по центру) ===
        Button closeButton = new Button(BrailleHelper.applyBrailleIfEnabled("Закрыть"), e -> close());
        closeButton.addClassName("graph-dialog-close-button");

        VerticalLayout dialogContent = new VerticalLayout(mainLayout, closeButton);
        dialogContent.setSizeFull();
        dialogContent.setSpacing(true);
        dialogContent.setPadding(true);
        dialogContent.setAlignItems(FlexComponent.Alignment.CENTER);

        add(dialogContent);

        loadFunctions();
    }

    private VerticalLayout createLeftPanel() {
        H2 title = new H2(BrailleHelper.applyBrailleIfEnabled("Ваши функции"));
        title.getStyle()
                .set("margin", "0 0 0.5rem 0")
                .set("font-size", "1.3em");

        Paragraph description = new Paragraph(BrailleHelper.applyBrailleIfEnabled("Выберите функцию для построения графика."));
        description.getStyle().set("margin", "0 0 1rem 0").set("color", "var(--lumo-secondary-text-color)");

        functionGrid.addColumn(FunctionDTO::getName).setHeader(BrailleHelper.applyBrailleIfEnabled("Имя")).setAutoWidth(true);
        functionGrid.setHeight("60vh"); // фиксированная высота, чтобы не растягивалась
        functionGrid.addClassName("graph-function-grid");

        VerticalLayout leftPanel = new VerticalLayout(title, description, functionGrid);
        leftPanel.setSpacing(true);
        leftPanel.setPadding(true);
        leftPanel.setWidth("300px"); // фиксированная ширина
        leftPanel.getStyle().set("border-right", "1px solid var(--lumo-contrast-10pct)");

        functionGrid.addSelectionListener(event -> {
            if (event.getFirstSelectedItem().isEmpty()) {
                showPlaceholder();
                return;
            }
            loadAndDisplayChart(event.getFirstSelectedItem().get());
        });

        return leftPanel;
    }

    private VerticalLayout createRightPanel() {
        chartTitle.getStyle().set("margin", "0 0 1rem 0").set("text-align", "center");

        chartComponent.setWidth("100%");
        chartComponent.setHeight("70vh"); // занимает большую часть

        VerticalLayout chartWrapper = new VerticalLayout(chartTitle, chartComponent);
        chartWrapper.setSpacing(true);
        chartWrapper.setPadding(true);
        chartWrapper.setSizeFull();
        chartWrapper.setAlignItems(FlexComponent.Alignment.CENTER);
        chartWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        return chartWrapper;
    }

    private void showPlaceholder() {
        chartTitle.setText(BrailleHelper.applyBrailleIfEnabled("Выберите функцию для отображения графика"));
        // Очищаем график (удаляем canvas и создаём новый)
        chartComponent.clearChart();
    }

    private void loadAndDisplayChart(FunctionDTO selectedFunction) {
        try {
            chartTitle.setText(BrailleHelper.applyBrailleIfEnabled("График: " + selectedFunction.getName()));

            String url = "http://localhost:8080/api/functions/" + selectedFunction.getId() + "/points";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());
            List<PointDTO> points = new LinkedList<>();

            if (rootNode.isArray()) {
                for (JsonNode pointNode : rootNode) {
                    double x = pointNode.get("xvalue").asDouble();
                    double y = pointNode.get("yvalue").asDouble();
                    points.add(new PointDTO(0, x, y, 0)); // id и functionId не нужны для графика
                }
            }

            if (points.isEmpty()) {
                NotificationManager.show(BrailleHelper.applyBrailleIfEnabled("У функции нет точек для отображения."), 3000, Notification.Position.BOTTOM_CENTER);
                showPlaceholder();
                return;
            }

            double[] xValues = points.stream().mapToDouble(PointDTO::getXValue).toArray();
            double[] yValues = points.stream().mapToDouble(PointDTO::getYValue).toArray();

            chartComponent.setChartData(xValues, yValues);

        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
            showPlaceholder();
        }
    }

    private void loadFunctions() {
        try {
            String url = "http://localhost:8080/api/functions?ownerId=" + currentUser.getId();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());
            List<FunctionDTO> functions = new LinkedList<>();

            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    functions.add(new FunctionDTO(
                            node.get("id").asInt(),
                            node.get("name").asText(),
                            node.get("ownerId").asInt(),
                            node.get("type").asText()
                    ));
                }
            }

            functionGrid.setItems(functions);

        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
        }
    }
}