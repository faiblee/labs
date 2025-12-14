package ru.ssau.tk.faible.labs.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import ru.ssau.tk.faible.labs.ui.utils.NotificationManager;

import java.util.ArrayList;
import java.util.List;

public class TabulatedInputBuilder extends Div {

    private final IntegerField pointCountField = new IntegerField("Количество точек");
    private final Button buildButton = new Button("Создать таблицу");
    private final VerticalLayout pointsLayout = new VerticalLayout();
    private final List<PointField> pointFields = new ArrayList<>();

    public static class PointField {
        public final TextField xField;
        public final TextField yField;

        public PointField() {
            this.xField = createDoubleField("X");
            this.yField = createDoubleField("Y");
        }

        public boolean isValid() {
            return isValidDouble(xField.getValue()) && isValidDouble(yField.getValue());
        }

        public double getX() {
            return Double.parseDouble(xField.getValue().trim());
        }

        public double getY() {
            return Double.parseDouble(yField.getValue().trim());
        }
    }

    public TabulatedInputBuilder() {
        pointCountField.setMin(2);
        pointCountField.setStepButtonsVisible(true);
        pointCountField.setValue(2);

        buildButton.addClickListener(e -> buildTable());

        pointsLayout.setVisible(false);
        pointsLayout.setSpacing(true);
        pointsLayout.setPadding(false);

        // Собираем UI
        add(new H3("Табулированная функция"));
        add(new HorizontalLayout(pointCountField, buildButton));
        add(pointsLayout);
    }

    private void buildTable() {
        Integer count = pointCountField.getValue();
        if (count == null || count < 2) {
            NotificationManager.show("Укажите количество точек (минимум 2)", 3000, Notification.Position.BOTTOM_CENTER);
            return;
        }

        // Очищаем старые поля
        pointFields.clear();
        pointsLayout.removeAll();

        // Создаём новые строки
        for (int i = 0; i < count; i++) {
            PointField pf = new PointField();
            pointFields.add(pf);

            HorizontalLayout row = new HorizontalLayout();
            row.setSpacing(true);
            row.add(pf.xField, pf.yField);
            row.setWidthFull();
            pf.xField.setWidth("150px");
            pf.yField.setWidth("150px");

            pointsLayout.add(row);
        }

        pointsLayout.setVisible(true);
    }

    private static TextField createDoubleField(String label) {
        TextField field = new TextField(label);
        field.setValueChangeMode(ValueChangeMode.EAGER); // мгновенная проверка
        field.addValueChangeListener(e -> {
            String val = e.getValue();
            if (!val.isEmpty() && !isValidDouble(val)) {
                field.setInvalid(true);
                field.setTitle("Введите корректное число");
            } else {
                field.setInvalid(false);
                field.setTitle("");
            }
        });
        return field;
    }

    private static boolean isValidDouble(String s) {
        if (s == null || s.trim().isEmpty()) return true; // пусто = допустимо (пока)
        try {
            Double.parseDouble(s.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Метод для получения валидных данных
    public List<double[]> getPointsAsArray() {
        List<double[]> points = new ArrayList<>();
        for (PointField pf : pointFields) {
            if (!pf.isValid()) {
                throw new IllegalStateException("Некорректные значения в поле X или Y");
            }
            points.add(new double[]{pf.getX(), pf.getY()});
        }
        return points;
    }

    // Или как списки Double:
    public List<Double> getXValues() {
        List<Double> x = new ArrayList<>();
        for (PointField pf : pointFields) {
            if (!isValidDouble(pf.xField.getValue())) {
                throw new IllegalStateException("Некорректное значение X");
            }
            x.add(pf.getX());
        }
        return x;
    }

    public List<Double> getYValues() {
        List<Double> y = new ArrayList<>();
        for (PointField pf : pointFields) {
            if (!isValidDouble(pf.yField.getValue())) {
                throw new IllegalStateException("Некорректное значение Y");
            }
            y.add(pf.getY());
        }
        return y;
    }
}