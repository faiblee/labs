package ru.ssau.tk.faible.labs.ui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.card.CardVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import ru.ssau.tk.faible.labs.ui.components.LogoutButton;
import ru.ssau.tk.faible.labs.ui.dialogs.*;
import com.vaadin.flow.component.AttachEvent;
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
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        H3 title = new H3(BrailleHelper.applyBrailleIfEnabled("Функциональный калькулятор"));
        Div spacer = new Div();
        LogoutButton logoutButton = new LogoutButton();

        header.add(spacer, title, logoutButton);
        add(header);

        // === Центральная область: сетка 2x3 ===
        Div gridContainer = new Div();
        gridContainer.addClassName("grid-container");
        gridContainer.add(
                createCard("📝 Создание функций", "Создавайте новые функции по имени и типу.", "Создать",
                        () -> {
                            CreateFunctionDialog dialog = new CreateFunctionDialog();
                            dialog.open();
                        }),
                createCard("🧮 Операции", "Выполняйте операции над функциями: сложение, умножение, дифференцирование.", "Открыть",
                        () -> {
                            OperationsDialog dialog = new OperationsDialog();
                            dialog.open();
                        }),
                createCard("📊 Графики", "Стройте графики для визуализации ваших функций.", "Построить",
                        () -> {
                            GraphsDialog dialog = new GraphsDialog();
                            dialog.open();
                        }),
                createCard("⚙️ Настройки", "Изменяйте профиль (логин, пароль) и выбирайте фабрику.", "Открыть настройки",
                        () -> {
                    Dialog dialog = new SettingsDialog();
                    dialog.open();
                }),
                createCard("🔄 Комплексные функции", "Работайте с составными функциями.", "Открыть",
                        () -> {
                            CreateCompositionFunctionDialog dialog = new CreateCompositionFunctionDialog();
                            dialog.open();
                        }),
                createCard("📈 Мои функции", "Все ваши функции.", "Открыть",
                        () -> {
                            FunctionsListDialog dialog = new FunctionsListDialog();
                            dialog.open();
                        }),
                createCard("🔤 Режим Брайля", "Включить/отключить отображение всего текста в символах Брайля.", "Переключить",
                        () -> {
                            boolean isEnabled = BrailleHelper.isBrailleModeEnabled();
                            BrailleHelper.setBrailleModeEnabled(!isEnabled);
                            Notification.show("Режим Брайля " + (!isEnabled ? "включён" : "выключен"));
                            // Перезагрузка страницы для обновления всех текстов
                            UI.getCurrent().getPage().reload();
                        })
        );

        add(gridContainer);
        setHorizontalComponentAlignment(Alignment.CENTER, gridContainer);
    }


    private Div createCard(String title, String description, String buttonText, Runnable onClick) {
        VerticalLayout cardContent = new VerticalLayout();
        cardContent.setSpacing(true);
        cardContent.setPadding(true);
        cardContent.setWidth("100%");
        cardContent.addClassName("card");

        H3 cardTitle = new H3(BrailleHelper.applyBrailleIfEnabled(title));
        cardTitle.getStyle().set("margin", "0").set("font-size", "1.3em");

        Paragraph desc = new Paragraph(BrailleHelper.applyBrailleIfEnabled(description));
        desc.getStyle().set("margin", "0").set("font-size", "0.95em").set("color", "var(--lumo-secondary-text-color)");

        Button openButton = new Button(BrailleHelper.applyBrailleIfEnabled(buttonText), e -> onClick.run());
        openButton.setWidth("100%");
        openButton.addClassName("card-button");

        cardContent.add(cardTitle, desc, openButton);

        Div card = new Div(cardContent);

        return card;
    }
}