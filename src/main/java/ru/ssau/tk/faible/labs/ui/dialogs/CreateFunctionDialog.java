// src/main/java/ru/ssau/tk/faible/labs/ui/dialogs/CreateFunctionDialog.java

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
import ru.ssau.tk.faible.labs.ui.models.CurrentUser;
import ru.ssau.tk.faible.labs.ui.models.CreateFunctionDTO;
import ru.ssau.tk.faible.labs.ui.utils.ExceptionHandler;
import ru.ssau.tk.faible.labs.ui.utils.NotificationManager;

public class CreateFunctionDialog extends Dialog {

    private final Select<String> typeSelect = new Select<>();
    private final TextField nameField = new TextField("Имя функции");
    private final TextField constantField = new TextField("Введите константу");
    private final TextField xFromField = new TextField("X начальное");
    private final TextField xToField = new TextField("X конечное");
    private final IntegerField countField = new IntegerField("Количество точек");

    private final Button createButton = new Button("Создать");
    private final Button cancelButton = new Button("Отмена");

    private final RestTemplate restTemplate = new RestTemplate();

    public CreateFunctionDialog() {
        // Устанавливаем размер: 60% ширины и высоты экрана (уменьшено)
        setWidth("60vw");
        setHeight("70vh");

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
                "TabulatedFunction"
        );

        // Настройка поля ввода константы
        constantField.setVisible(false);

        // Настройка полей XFrom, XTo, Count
        xFromField.setVisible(false);
        xToField.setVisible(false);
        countField.setVisible(false);

        // --- Реакция на изменение типа функции ---
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

            // Проверяем, нужно ли показать поля XFrom, XTo, Count
            // Это нужно для всех функций, кроме ConstantFunction и TabulatedFunction
            boolean needsRangeAndCount = selectedType != null && !selectedType.equals("TabulatedFunction");

            xFromField.setVisible(needsRangeAndCount);
            xToField.setVisible(needsRangeAndCount);
            countField.setVisible(needsRangeAndCount);

            if (needsRangeAndCount) {
                xFromField.focus(); // (опционально) перевести фокус на первое поле
            }
        });

        // Форма
        FormLayout form = new FormLayout();
        form.add(title, description, nameField, typeSelect, constantField, xFromField, xToField, countField);
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

        // --- Проверки и сбор данных для ConstantFunction ---
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

        // --- Проверки и сбор данных для базовых функций (кроме Constant и Tabulated) ---
        Double xFrom = null;
        Double xTo = null;
        Integer count = null;

        if (!"TabulatedFunction".equals(selectedType)) {
            String xFromStr = xFromField.getValue();
            String xToStr = xToField.getValue();
            Integer countVal = countField.getValue(); // IntegerField возвращает Integer или null

            if (xFromStr == null || xFromStr.trim().isEmpty()) {
                NotificationManager.show("Пожалуйста, введите начальное значение X!", 3000, Notification.Position.BOTTOM_CENTER);
                return;
            }
            if (xToStr == null || xToStr.trim().isEmpty()) {
                NotificationManager.show("Пожалуйста, введите конечное значение X!", 3000, Notification.Position.BOTTOM_CENTER);
                return;
            }
            if (countVal == null) { // IntegerField может вернуть null, если поле пустое
                NotificationManager.show("Пожалуйста, введите количество точек!", 3000, Notification.Position.BOTTOM_CENTER);
                return;
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

            if (xFrom > xTo) {
                NotificationManager.show("X начальное не может быть больше X конечного!", 3000, Notification.Position.BOTTOM_CENTER);
                return;
            }

            count = countVal;
        }

        // --- Логика отправки запроса ---
        try {
            CurrentUser currentUser = VaadinSession.getCurrent().getAttribute(CurrentUser.class);

            int owner_id = currentUser.getId();
            String factory_type = currentUser.getFactory_type();
            CreateFunctionDTO functionDTO = new CreateFunctionDTO();

            functionDTO.setName(name);
            functionDTO.setType(selectedType);
            functionDTO.setOwnerId(owner_id);
            functionDTO.setFactory_type(factory_type);

            if ("TabulatedFunction".equals(selectedType)) {
                // pass
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

            // Пока просто показываем уведомление
            if ("Константная функция".equals(selectedType)) {
                NotificationManager.show("Функция типа '" + selectedType + "' с константой '" + constantValue + "' создана!", 3000, Notification.Position.BOTTOM_CENTER);
            } else if ("TabulatedFunction".equals(selectedType)) {
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