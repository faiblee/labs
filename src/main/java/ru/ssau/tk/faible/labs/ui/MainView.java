package ru.ssau.tk.faible.labs.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Главная")
public class MainView extends VerticalLayout {

    public MainView() {
        addClassName("main-view");
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        add(new H1("Добро пожаловать!"));
        Button registerButton = new Button("Зарегистрироваться", e -> {
            UI.getCurrent().navigate("register"); // Переход на /register
        });

        Button loginButton = new Button("Войти", e -> {
            UI.getCurrent().navigate("login"); // Переход на /login
        });

        add(registerButton, loginButton);
    }
}
