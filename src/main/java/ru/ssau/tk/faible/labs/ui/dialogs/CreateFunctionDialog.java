package ru.ssau.tk.faible.labs.ui.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField; // Используем IntegerField для количества точек
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import ru.ssau.tk.faible.labs.ui.components.TabulatedInputBuilder;
import ru.ssau.tk.faible.labs.ui.models.CurrentUser;
import ru.ssau.tk.faible.labs.ui.models.CreateFunctionDTO;
import ru.ssau.tk.faible.labs.ui.models.Point;
import ru.ssau.tk.faible.labs.ui.utils.ExceptionHandler;
import ru.ssau.tk.faible.labs.ui.utils.NotificationManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CreateFunctionDialog extends Dialog {

    private final Select<String> typeSelect = new Select<>();
    private final TextField nameField = new TextField("Имя функции");
    private final TextField constantField = new TextField("Введите константу");
    private final TextField xFromField = new TextField("X начальное");
    private final TextField xToField = new TextField("X конечное");
    private final IntegerField countField = new IntegerField("Количество точек");
    private final TabulatedInputBuilder tabulatedInputBuilder = new TabulatedInputBuilder();

    private final Button createButton = new Button("Создать");
    private final Button cancelButton = new Button("Отмена");

    private final RestTemplate restTemplate = new RestTemplate();

    public CreateFunctionDialog() {
        setWidth("60vw");
        setHeight("70vh");

        countField.setMin(2);
        countField.setMax(10000);

        // Заголовок
        H2 title = new H2("Создание новой функции");
        title.getStyle().set("margin", "0 0 1rem 0").set("font-size", "1.5em");

        // Описание
        Paragraph description = new Paragraph("Выберите тип функции и заполните поля.");
        description.getStyle().set("margin", "0 0 1rem 0").set("color", "var(--lumo-secondary-text-color)");

        // Настройка Select
        typeSelect.setLabel("Тип функции");
        typeSelect.setItems(
                "Функция с константой 0",
                "Функция с константой 1",
                "Квадратичная функция",
                "Тождественная функция",
                "Константная функция",
                "Табулированная функция"
        );

        constantField.setVisible(false);
        xFromField.setVisible(false);
        xToField.setVisible(false);
        countField.setVisible(false);
        tabulatedInputBuilder.setVisible(false);

        typeSelect.addValueChangeListener(event -> {
            String selectedType = event.getValue();

            // Проверяем, нужно ли показать поле константы
            boolean isConstantFunction = "Константная функция".equals(selectedType);
            constantField.setVisible(isConstantFunction);
            if (isConstantFunction) {
                constantField.focus();
            } else {
                constantField.setVisible(false); // Скрываем, если не ConstantFunction
            }

            boolean isTabulated = "Табулированная функция".equals(selectedType);

            tabulatedInputBuilder.setVisible(isTabulated);


            boolean needsRangeAndCount = selectedType != null && !selectedType.equals("Табулированная функция");

            xFromField.setVisible(needsRangeAndCount);
            xToField.setVisible(needsRangeAndCount);
            countField.setVisible(needsRangeAndCount);

            if (needsRangeAndCount) {
                xFromField.focus();
            }
        });
        // Форма
        FormLayout form = new FormLayout();
        form.add(title, description, nameField, typeSelect, tabulatedInputBuilder, constantField, xFromField, xToField, countField);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1)); // Настройка адаптивности
        form.addClassName("spacing-medium");

        // Кнопки
        HorizontalLayout buttons = new HorizontalLayout(createButton, cancelButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setWidthFull();

        add(form, buttons);

        // Обработчики событий
        createButton.addClickListener(e -> createFunction());
        cancelButton.addClickListener(e -> close());
    }

    private void createFunction() {
        String selectedType = typeSelect.getValue();
        String name = nameField.getValue();
        if (selectedType == null || selectedType.trim().isEmpty()) {
            NotificationManager.show("Пожалуйста, выберите тип функции!", 3000, Notification.Position.BOTTOM_CENTER);
            return;
        }


        String constantValue = null;
        if ("Константная функция".equals(selectedType)) {
            constantValue = constantField.getValue();
            if (constantValue == null || constantValue.trim().isEmpty()) {
                NotificationManager.show("Пожалуйста, введите константу!", 3000, Notification.Position.BOTTOM_CENTER);
                return;
            }
            try {
                Double.parseDouble(constantValue);
            } catch (NumberFormatException e) {
                NotificationManager.show("Константа должна быть числом!", 3000, Notification.Position.BOTTOM_CENTER);
                return;
            }
        }


        Double xFrom = null;
        Double xTo = null;
        Integer count = null;
        List<Point> tabulatedPoints = new LinkedList<>();

        if (!"Табулированная функция".equals(selectedType)) {
            String xFromStr = xFromField.getValue();
            String xToStr = xToField.getValue();
            Integer countVal = countField.getValue();

            if (xFromStr == null || xFromStr.trim().isEmpty()) {
                NotificationManager.show("Пожалуйста, введите начальное значение X!", 3000, Notification.Position.BOTTOM_CENTER);
                return;
            }
            if (xToStr == null || xToStr.trim().isEmpty()) {
                NotificationManager.show("Пожалуйста, введите конечное значение X!", 3000, Notification.Position.BOTTOM_CENTER);
                return;
            }
            if (countVal == null) {
                NotificationManager.show("Пожалуйста, введите количество точек!", 3000, Notification.Position.BOTTOM_CENTER);
            }

            try {
                xFrom = Double.parseDouble(xFromStr);
                xTo = Double.parseDouble(xToStr);
            } catch (NumberFormatException e) {
                NotificationManager.show("X начальное и X конечное должны быть числами!", 3000, Notification.Position.BOTTOM_CENTER);
                return;
            }

            if (countVal < 2) {
                NotificationManager.show("Количество точек должно быть не менее 2!", 3000, Notification.Position.BOTTOM_CENTER);
                return;
            }

            if (countVal > 10000) {
                NotificationManager.show("Количество точек должно быть не более 10000!", 3000, Notification.Position.BOTTOM_CENTER);
                return;
            }

            if (xFrom > xTo) {
                NotificationManager.show("X начальное не может быть больше X конечного!", 3000, Notification.Position.BOTTOM_CENTER);
                return;
            }

            if (Math.abs(xFrom) > 1e6) {
                NotificationManager.show("X начальное не может быть по модулю больше 1.000.000!", 3000, Notification.Position.BOTTOM_CENTER);
                return;
            }

            if (Math.abs(xTo) > 1e6) {
                NotificationManager.show("X конечное не может быть по модулю больше 1.000.000!", 3000, Notification.Position.BOTTOM_CENTER);
                return;
            }

            count = countVal;
        } else {
            // если Tabulated
            tabulatedPoints = tabulatedInputBuilder.getPointsAsArray();
            if (tabulatedPoints == null || tabulatedPoints.isEmpty()) {
                NotificationManager.show("Добавьте хотя бы одну точку!", 3000, Notification.Position.BOTTOM_CENTER);
                return;
            }

            // Проверка: все x и y заданы
            for (Point p : tabulatedPoints) {
                if (p.x == null || p.y == null) {
                    NotificationManager.show("Все значения X и Y должны быть заполнены!", 3000, Notification.Position.BOTTOM_CENTER);
                    return;
                }
            }

            // Проверка: минимум 2 точки
            if (tabulatedPoints.size() < 2) {
                NotificationManager.show("Табулированная функция должна содержать минимум 2 точки!", 3000, Notification.Position.BOTTOM_CENTER);
                return;
            }

            // Проверка: x строго возрастают
            for (int i = 1; i < tabulatedPoints.size(); i++) {
                if (tabulatedPoints.get(i).x <= tabulatedPoints.get(i - 1).x) {
                    NotificationManager.show("Значения X должны быть строго возрастающими!", 3000, Notification.Position.BOTTOM_CENTER);
                    return;
                }
            }
        }

        try {
            CurrentUser currentUser = VaadinSession.getCurrent().getAttribute(CurrentUser.class);

            int owner_id = currentUser.getId();
            String factory_type = currentUser.getFactory_type();
            CreateFunctionDTO functionDTO = new CreateFunctionDTO();

            functionDTO.setName(name);
            functionDTO.setType(selectedType);
            functionDTO.setOwnerId(owner_id);
            functionDTO.setFactory_type(factory_type);

            if ("Табулированная функция".equals(selectedType)) {
                List<Double> xList = new ArrayList<>();
                List<Double> yList = new ArrayList<>();
                for (Point p : tabulatedPoints) {
                    xList.add(p.x);
                    yList.add(p.y);
                }

                double[] xArray = xList.stream().mapToDouble(Double::doubleValue).toArray();
                double[] yArray = yList.stream().mapToDouble(Double::doubleValue).toArray();

                functionDTO.setXvalues(xArray);
                functionDTO.setYvalues(yArray);
            } else {
                functionDTO.setXFrom(xFrom);
                functionDTO.setXTo(xTo);
                functionDTO.setCount(count);
            }
            if ("Константная функция".equals(selectedType)) {
                functionDTO.setConstant(Double.parseDouble(constantValue));
            }

            String url = "http://localhost:8080/api/functions";

            HttpHeaders headers = new HttpHeaders();

            headers.set("Authorization", "Basic " + currentUser.getEncodedCredentials());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CreateFunctionDTO> requestEntity = new HttpEntity<>(functionDTO, headers);

            try {
                restTemplate.postForObject(url, requestEntity, Object.class);
                close();
            } catch (Exception ex) {
                ExceptionHandler.notifyUser(ex);
            }


            if ("Константная функция".equals(selectedType)) {
                NotificationManager.show("Функция типа '" + selectedType + "' с константой '" + constantValue + "' создана!", 3000, Notification.Position.BOTTOM_CENTER);
            } else if ("Табулированная функция".equals(selectedType)) {
                NotificationManager.show("Пустая табулированная функция типа '" + selectedType + "' создана!", 3000, Notification.Position.BOTTOM_CENTER);
            } else {
                NotificationManager.show("Функция типа '" + selectedType + "' с параметрами X=[" + xFrom + ", " + xTo + "], точек: " + count + " создана!", 3000, Notification.Position.BOTTOM_CENTER);
            }
            close();
        } catch (Exception ex) {
            ExceptionHandler.notifyUser(ex);
        }
    }
}