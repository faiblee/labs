package ru.ssau.tk.faible.labs.ui.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

public class SettingsDialog extends Dialog {

    public SettingsDialog() {
        setWidth("500px");

        add(new H3("Настройки профиля"));

        TextField usernameField = new TextField("Имя пользователя");
        usernameField.setValue("admin"); // (загрузите из API)

        Select<String> factorySelect = new Select<>();
        factorySelect.setLabel("Фабрика функций");
        factorySelect.setItems("Array Factory", "Linked List Factory");
        factorySelect.setValue("Array Factory");

        PasswordField newPasswordField = new PasswordField("Новый пароль");
        PasswordField confirmField = new PasswordField("Подтвердите пароль");

        VerticalLayout form = new VerticalLayout(
                usernameField, factorySelect, newPasswordField, confirmField
        );
        form.setSpacing(true);
        form.setPadding(true);

        HorizontalLayout buttons = new HorizontalLayout();
        Button saveBtn = new Button("Сохранить", e -> {
            // здесь вызов API через RestTemplate
            close();
        });
        Button cancelBtn = new Button("Отмена", e -> close());
        buttons.add(saveBtn, cancelBtn);

        add(form, buttons);
    }
}