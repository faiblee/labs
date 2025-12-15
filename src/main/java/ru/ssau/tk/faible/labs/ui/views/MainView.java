// src/main/java/ru/ssau/tk/faible/labs/ui/views/MainView.java

package ru.ssau.tk.faible.labs.ui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import ru.ssau.tk.faible.labs.ui.components.LogoutButton;
import ru.ssau.tk.faible.labs.ui.dialogs.*;
import ru.ssau.tk.faible.labs.ui.utils.BrailleHelper;

@Route("/main")
@PageTitle("Главная")
@AnonymousAllowed
public class MainView extends VerticalLayout {

    public MainView() {
        addClassName("main-view");
        setSizeFull();
        setSpacing(false);
        setPadding(false);

        // === Верхняя панель ===
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setClassName("main-header");
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        // Кнопка "Режим Брайля"
        Button brailleButton = new Button(VaadinIcon.ACCESSIBILITY.create(), e -> {
            boolean isEnabled = BrailleHelper.isBrailleModeEnabled();
            BrailleHelper.setBrailleModeEnabled(!isEnabled);
            Notification.show("Режим Брайля " + (!isEnabled ? "включён" : "выключен"));
            UI.getCurrent().getPage().reload();
        });
        brailleButton.addClassName("braille-toggle-button");

        // Заголовок
        H3 title = new H3(BrailleHelper.applyBrailleIfEnabled("Функциональный калькулятор"));
        title.getStyle().set("margin", "0").set("font-size", "1.4em").set("color", "#2c3e50");

        // Кнопка выхода
        LogoutButton logoutButton = new LogoutButton();

        header.add(brailleButton, title, logoutButton);
        add(header);

        // === Центральная область: сетка 2x3 ===
        Div gridContainer = new Div();
        gridContainer.addClassName("grid-container");

        gridContainer.add(
                createCardWithIcon("📝 Создание функций", "Создавайте новые функции по имени и типу.", "Создать",
                        () -> {
                            CreateFunctionDialog dialog = new CreateFunctionDialog();
                            dialog.open();
                        }),
                createCardWithIcon("🧮 Операции", "Выполняйте операции над функциями: сложение, умножение, дифференцирование.", "Открыть",
                        () -> {
                            OperationsDialog dialog = new OperationsDialog();
                            dialog.open();
                        }),
                createCardWithIcon("📊 Графики", "Стройте графики для визуализации ваших функций.", "Построить",
                        () -> {
                            GraphsDialog dialog = new GraphsDialog();
                            dialog.open();
                        }),
                createCardWithIcon("⚙️ Настройки", "Изменяйте профиль (логин, пароль) и выбирайте фабрику.", "Открыть настройки",
                        () -> {
                            Dialog dialog = new SettingsDialog();
                            dialog.open();
                        }),
                createCardWithIcon("🔄 Комплексные функции", "Работайте с составными функциями.", "Открыть",
                        () -> {
                            CreateCompositionFunctionDialog dialog = new CreateCompositionFunctionDialog();
                            dialog.open();
                        }),
                createCardWithIcon("📈 Мои функции", "Все ваши функции.", "Открыть",
                        () -> {
                            FunctionsListDialog dialog = new FunctionsListDialog();
                            dialog.open();
                        })
        );

        add(gridContainer);
        setHorizontalComponentAlignment(Alignment.CENTER, gridContainer);
    }

    private Div createCardWithIcon(String title, String description, String buttonText, Runnable onClick) {
        VerticalLayout cardContent = new VerticalLayout();
        cardContent.setSpacing(true);
        cardContent.setPadding(true);
        cardContent.setWidth("100%");
        cardContent.addClassName("card");

        // --- Иконка и заголовок ---
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        titleLayout.setSpacing(true);

        Icon icon = getIconForTitle(title);
        H3 cardTitle = new H3(BrailleHelper.applyBrailleIfEnabled(title));
        cardTitle.getStyle().set("margin", "0").set("font-size", "1.3rem").set("color", "#2c3e50");

        titleLayout.add(icon, cardTitle);

        // --- Описание ---
        Paragraph desc = new Paragraph(BrailleHelper.applyBrailleIfEnabled(description));
        desc.getStyle()
                .set("margin", "0.75rem 0 0 0")
                .set("font-size", "0.95rem")
                .set("color", "#7f8c8d")
                .set("line-height", "1.4");

        // --- Кнопка ---
        Button openButton = new Button(BrailleHelper.applyBrailleIfEnabled(buttonText), e -> onClick.run());
        openButton.setWidth("100%");
        openButton.addClassName("card-button");

        cardContent.add(titleLayout, desc, openButton);

        Div card = new Div(cardContent);
        card.addClassName("card-wrapper");

        return card;
    }

    // Метод для получения иконки по заголовку
    private Icon getIconForTitle(String title) {
        if (title.contains("📝")) {
            return VaadinIcon.FILE.create();
        } else if (title.contains("🧮")) {
            return VaadinIcon.CALC.create();
        } else if (title.contains("📊")) {
            return VaadinIcon.CHART.create();
        } else if (title.contains("⚙️")) {
            return VaadinIcon.COG.create();
        } else if (title.contains("🔄")) {
            return VaadinIcon.LINK.create();
        } else if (title.contains("📈")) {
            return VaadinIcon.FOLDER.create();
        }
        // Иконка по умолчанию
        return VaadinIcon.INFO_CIRCLE.create();
    }
}