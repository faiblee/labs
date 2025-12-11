package ru.ssau.tk.faible.labs;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme("default")
public class ApplicationFrontend implements AppShellConfigurator {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationFrontend.class, args);
    }
}
