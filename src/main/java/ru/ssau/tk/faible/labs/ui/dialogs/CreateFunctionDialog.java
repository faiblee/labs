// src/main/java/ru/ssau/tk/faible/labs/ui/dialogs/CreateFunctionDialog.java

package ru.ssau.tk.faible.labs.ui.dialogs;

import com.vaadin.flow.component.UI;
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
import org.springframework.web.client.RestTemplate;
import ru.ssau.tk.faible.labs.ui.utils.ExceptionHandler;
import ru.ssau.tk.faible.labs.ui.utils.NotificationManager;

public class CreateFunctionDialog extends Dialog {

    private final Select<String> typeSelect = new Select<>();
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
        setHeight("60vh");

        // Заголовок
        H2 title = new H2("Создание новой функции");
        title.getStyle().set("margin", "0 0 1rem 0").set("font-size", "1.5em");

        // Описание
        Paragraph description = new Paragraph("Выберите тип функции и заполните поля.");
        description.getStyle().set("margin", "0 0 1rem 0").set("color", "var(--lumo-secondary-text-color)");

        // Настройка Select
        typeSelect.setLabel("Тип функции");
        typeSelect.setItems(
                "ZeroFunction",
                "UnitFunction",
                "SqrFunction",
                "IdentityFunction",
                "ConstantFunction",
                "CompositeFunction",
                "TabulatedFunction"
        );
        // --- УБРАНО ---
        // typeSelect.setValue("ZeroFunction"); // НЕ устанавливаем значение по умолчанию
        // ----------------

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
            boolean isConstantFunction = "ConstantFunction".equals(selectedType);
            constantField.setVisible(isConstantFunction);
            if (isConstantFunction) {
                constantField.focus();
            } else {
                constantField.setVisible(false); // Скрываем, если не ConstantFunction
            }

            // Проверяем, нужно ли показать поля XFrom, XTo, Count
            // Это нужно для всех функций, кроме ConstantFunction и TabulatedFunction
            boolean needsRangeAndCount = selectedType != null && // <-- Добавлена проверка на null
                    !selectedType.equals("ConstantFunction") &&
                    !selectedType.equals("TabulatedFunction");

            xFromField.setVisible(needsRangeAndCount);
            xToField.setVisible(needsRangeAndCount);
            countField.setVisible(needsRangeAndCount);

            if (needsRangeAndCount) {
                xFromField.focus(); // (опционально) перевести фокус на первое поле
            }
        });

        // Форма
        FormLayout form = new FormLayout();
        form.add(title, description, typeSelect, constantField, xFromField, xToField, countField);
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

        if (selectedType == null || selectedType.trim().isEmpty()) {
            NotificationManager.show("Пожалуйста, выберите тип функции!", 3000, Notification.Position.BOTTOM_CENTER);
            return;
        }

        // --- Проверки и сбор данных для ConstantFunction ---
        String constantValue = null;
        if ("ConstantFunction".equals(selectedType)) {
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

        if (!"ConstantFunction".equals(selectedType) && !"TabulatedFunction".equals(selectedType)) {
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
        }

        // --- Логика отправки запроса ---
        try {
            // Здесь вы можете сформировать DTO, например:
            // FunctionDTO dto = new FunctionDTO(selectedType, xFrom, xTo, count, constantValue);
            // restTemplate.postForObject("http://localhost:8080/api/functions", dto, Object.class);

            // Пока просто показываем уведомление
            if ("ConstantFunction".equals(selectedType)) {
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