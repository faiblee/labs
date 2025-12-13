// ui/dialogs/FunctionDetailsDialog.java
package ru.ssau.tk.faible.labs.ui.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import ru.ssau.tk.faible.labs.ui.models.FunctionDTO;

public class FunctionDetailsDialog extends Dialog {

    private final FunctionDTO function;

    public FunctionDetailsDialog(FunctionDTO function) {
        this.function = function;
        setWidth("700px");
        setHeight("500px");

        add(new H2("Функция: " + function.getName()));
        add(new com.vaadin.flow.component.html.Paragraph("Тип: " + function.getType()));

        HorizontalLayout actions = new HorizontalLayout();
        actions.add(
                new Button("📊 График", e -> {}),
                new Button("✏️ Редактировать", e -> {}),
                new Button("➕ Точка", e -> {}),
                new Button("🗑 Удалить", e -> {})
        );
        add(actions);
    }

//    private void showGraph() {
//        new GraphDialog(function, apiService).open();
//    }
//
//    private void editFunction() {
//        new EditFunctionDialog(function, apiService, this::close).open();
//    }
//
//    private void addPoint() {
//        new AddPointDialog(function, apiService).open();
//    }
//
//    private void deleteFunction() {
//        if (apiService.deleteFunction(function.getId())) {
//            close(); // Закрываем диалог после удаления
//        } else {
//            ErrorNotifier.notifyUser(new RuntimeException("Не удалось удалить функцию"));
//        }
//    }
}