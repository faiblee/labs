package ru.ssau.tk.faible.labs.ui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import ru.ssau.tk.faible.labs.ui.dialogs.LoginDialog;
import ru.ssau.tk.faible.labs.ui.dialogs.RegisterDialog;

@Route("")
@PageTitle("Welcome")
public class WelcomeView extends VerticalLayout {

    public WelcomeView() {
        addClassName("main-view");
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        add(new H1("Добро пожаловать!"));
        Button registerButton = new Button("Зарегистрироваться", e -> {
            RegisterDialog dialog = new RegisterDialog();
            dialog.setWidth("400px");  // Установить ширину
            dialog.setHeight("400px"); // Установить высоту
            dialog.open();
        });

        Button loginButton = new Button("Войти", e -> {
            LoginDialog dialog = new LoginDialog();
            dialog.setWidth("400px");  // Установить ширину
            dialog.setHeight("400px"); // Установить высоту
            dialog.open();
        });

        add(registerButton, loginButton);
    }
}
