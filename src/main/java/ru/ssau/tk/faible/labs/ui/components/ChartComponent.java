// src/main/java/ru/ssau/tk/faible/labs/ui/components/ChartComponent.java

package ru.ssau.tk.faible.labs.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Element;

@Tag("div")
@NpmPackage(value = "chart.js", version = "4.4.0")
public class ChartComponent extends Div {

    private Element canvas;

    public ChartComponent() {
        canvas = new Element("canvas");
        String canvasId = "chart-canvas-" + System.currentTimeMillis(); // Уникальный ID
        canvas.setAttribute("id", canvasId);
        getElement().appendChild(canvas);
    }

    public void setChartData(double[] xValues, double[] yValues) {
        String canvasId = canvas.getAttribute("id");
        if (canvasId == null) {
            throw new IllegalStateException("Canvas ID is not set!");
        }

        // Генерируем JS-код для построения графика
        String jsCode = """
            const ctx = document.getElementById('%s').getContext('2d');
            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: %s,
                    datasets: [{
                        label: 'График функции',
                        data: %s,
                        borderColor: 'rgb(75, 192, 192)',
                        tension: 0.1
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            display: false
                        }
                    }
                }
            });
            """.formatted(
                canvasId, // <-- Используем полученный ID
                java.util.Arrays.toString(xValues), // метки (X)
                java.util.Arrays.toString(yValues) // данные (Y)
        );

        // Выполняем JS-код
        getElement().executeJs(jsCode);
    }
}