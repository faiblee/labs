package ru.ssau.tk.faible.labs.ui.views; // или ваш пакет

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("function-options") // маршрут для новой страницы
@PageTitle("Функции")
public class FunctionChoiceView extends VerticalLayout {

    public FunctionChoiceView() {
        addClassName("function-choice-view");
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        add(new H2("Выберите действие с функциями:"));

        Button myFunctionsButton = new Button("Мои функции", e -> {
            // TODO: Перейти на страницу "мои функции" (если есть)
            // UI.getCurrent().navigate("my-functions");
            UI.getCurrent().navigate(WelcomeView.class); // временно на главную
        });

        Button createFunctionButton = new Button("Создать функцию", e -> {
            // TODO: Перейти на страницу создания функции (если есть)
            // UI.getCurrent().navigate("create-function");
            UI.getCurrent().navigate(WelcomeView.class); // временно на главную
        });

        Button backButton = new Button("Назад", e -> {
            // Вернуться на предыдущую страницу (обычно MainView)
            UI.getCurrent().navigate(MainView.class);
        });

        add(myFunctionsButton, createFunctionButton, backButton);
    }
}