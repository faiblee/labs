package ru.ssau.tk.faible.labs.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.VaadinService;

public class LogoutButton extends Button {

    public LogoutButton() {
        setText("Выйти");
        setIcon(VaadinIcon.SIGN_OUT.create());
        addClickListener(e -> {
            // Удаляем Basic Auth credentials из сессии
            com.vaadin.flow.server.VaadinSession.getCurrent().setAttribute("basic_auth_encoded", null);

            // Опционально: инвалидировать саму Vaadin сессию
            // VaadinService.getCurrentRequest().getWrappedSession().invalidate();

            // Перенаправить на login
            getUI().ifPresent(ui -> ui.navigate(""));
        });
    }
}