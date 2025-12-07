package ru.ssau.tk.faible.labs.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinService;

public class LogoutButton extends Button {

    public LogoutButton() {
        setText("Выйти");
        setIcon(VaadinIcon.SIGN_OUT.create());
        addClickListener(e -> {
            // Уничтожаем сессию
            VaadinService.getCurrentRequest().getWrappedSession().invalidate();
            // Перенаправляем на главную (она перенаправит на /login)
            getUI().ifPresent(ui -> ui.navigate(""));
        });
    }
}
