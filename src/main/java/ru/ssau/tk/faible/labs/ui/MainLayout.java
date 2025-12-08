package ru.ssau.tk.faible.labs.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@Route("main")
public class MainLayout extends AppLayout {

    public MainLayout() {
        addToNavbar(true, new DrawerToggle(), new H1("Лабораторная №7"));

        Scroller scroller = new Scroller(new VerticalLayout(
                new RouterLink("Функции", MainView.class),
                new RouterLink("Операции", MainView.class),
                new RouterLink("Настройки", MainView.class),
                new RouterLink("Профиль", MainView.class)
        ));
        addToDrawer(scroller);

        // Добавляем кнопку "Выйти"
        addToNavbar(false, new LogoutButton());
    }
}
