package ru.ssau.tk.faible.labs.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class MainView extends VerticalLayout {
    public MainView() {
        add(new com.vaadin.flow.component.html.H1("Добро пожаловать в интерфейс"));
        add(new com.vaadin.flow.component.html.Paragraph("Здесь будет UI для работы с функциями."));
    }
}
