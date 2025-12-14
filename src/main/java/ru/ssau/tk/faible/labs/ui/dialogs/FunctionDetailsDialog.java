// ui/dialogs/FunctionDetailsDialog.java
package ru.ssau.tk.faible.labs.ui.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import ru.ssau.tk.faible.labs.ui.models.FunctionDTO;
import ru.ssau.tk.faible.labs.ui.utils.BrailleHelper;

public class FunctionDetailsDialog extends Dialog {

    private final FunctionDTO function;

    public FunctionDetailsDialog(FunctionDTO function) {
        this.function = function;
        setWidth("700px");
        setHeight("500px");

        add(new H2("Функция: " + BrailleHelper.applyBrailleIfEnabled(function.getName())));
        add(new com.vaadin.flow.component.html.Paragraph("Тип: " + function.getType()));

        HorizontalLayout actions = new HorizontalLayout();
        actions.add(
                new Button(BrailleHelper.applyBrailleIfEnabled("📊 График"), e -> {}),
                new Button(BrailleHelper.applyBrailleIfEnabled("✏️ Редактировать"), e -> {}),
                new Button(BrailleHelper.applyBrailleIfEnabled("➕ Точка"), e -> {}),
                new Button(BrailleHelper.applyBrailleIfEnabled("🗑 Удалить"), e -> {})
        );
        add(actions);
    }

}