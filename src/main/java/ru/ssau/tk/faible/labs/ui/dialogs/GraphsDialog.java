// src/main/java/ru/ssau/tk/faible/labs/ui/dialogs/GraphsDialog.java

package ru.ssau.tk.faible.labs.ui.dialogs;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.springframework.web.client.RestTemplate;
import ru.ssau.tk.faible.labs.ui.components.ChartComponent; // ← ваш компонент
import ru.ssau.tk.faible.labs.ui.models.FunctionDTO;
import ru.ssau.tk.faible.labs.ui.models.PointDTO;
import ru.ssau.tk.faible.labs.ui.utils.ExceptionHandler;
import ru.ssau.tk.faible.labs.ui.utils.NotificationManager;

import java.util.List;

public class GraphsDialog extends Dialog {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Grid<FunctionDTO> functionGrid = new Grid<>();
    private final ChartComponent chartComponent = new ChartComponent(); // ← используем ChartComponent

    public GraphsDialog() {
        setWidth("90vw");
        setHeight("90vh");

        // Заголовок
        H2 title = new H2("Ваши функции");
        title.getStyle().set("margin", "0 0 1rem 0").set("font-size", "1.5em");

        // Описание
        Paragraph description = new Paragraph("Выберите функцию для построения графика.");
        description.getStyle().set("margin", "0 0 1rem 0").set("color", "var(--lumo-secondary-text-color)");

        // Настройка Grid
        functionGrid.addColumn(FunctionDTO::getName).setHeader("Имя");
        functionGrid.addColumn(FunctionDTO::getType).setHeader("Тип");
        functionGrid.addColumn(FunctionDTO::getId).setHeader("ID");

        functionGrid.addSelectionListener(event -> {
            if (!event.getFirstSelectedItem().isPresent()) return;

            FunctionDTO selectedFunction = event.getFirstSelectedItem().get();

            // Загрузка точек функции
            try {
                String url = "http://localhost:8080/api/functions/" + selectedFunction.getId() + "/points";
                // Предполагаем, что API возвращает List<PointDTO>
                List<PointDTO> points = restTemplate.getForObject(url, List.class);

                if (points == null || points.isEmpty()) {
                    NotificationManager.show("У функции '" + selectedFunction.getName() + "' нет точек для отображения.", 3000, Notification.Position.BOTTOM_CENTER);
                    return;
                }

                // Преобразуем точки в массивы
                double[] xValues = points.stream().mapToDouble(PointDTO::getXValue).toArray();
                double[] yValues = points.stream().mapToDouble(PointDTO::getYValue).toArray();

                // Построение графика
                chartComponent.setChartData(xValues, yValues);

            } catch (Exception ex) {
                ExceptionHandler.notifyUser(ex);
            }
        });

        // Кнопка закрытия
        Button closeButton = new Button("Закрыть", e -> close());

        // Макет
        VerticalLayout chartLayout = new VerticalLayout(chartComponent);
        chartLayout.setPadding(true);
        chartLayout.setSizeFull();

        HorizontalLayout buttons = new HorizontalLayout(closeButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setWidthFull();

        VerticalLayout layout = new VerticalLayout(title, description, functionGrid, chartLayout, buttons);
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.setSizeFull();

        add(layout);

        // Загрузка функций при открытии
        loadFunctions();
    }

    private void loadFunctions() {
        try {
            // Получаем текущего пользователя (например, из сессии)
            Long currentUserId = getCurrentUserId();

            String url = "http://localhost:8080/api/functions?ownerId=" + currentUserId;
            // Предполагаем, что API возвращает List<FunctionDTO>
            List<FunctionDTO> functions = restTemplate.getForObject(url, List.class);

            if (functions != null) {
                functionGrid.setItems(functions);
            } else {
                NotificationManager.show("Не удалось загрузить функции.", 3000, Notification.Position.BOTTOM_CENTER);
            }

        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
        }
    }

    private Long getCurrentUserId() {
        // Пример: извлекаем ID из сессии (если вы храните его там)
        // В реальном проекте это должно быть получено через SecurityService
        Object userIdObj = UI.getCurrent().getSession().getAttribute("current_user_id");
        if (userIdObj != null && userIdObj instanceof Long) {
            return (Long) userIdObj;
        }
        return 1L; // Для теста — замените на реальный способ получения ID
    }
}