// src/main/java/ru/ssau/tk/faible/labs/ui/components/ChartComponent.java

package ru.ssau.tk.faible.labs.ui.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Element;

@Tag("div")
@NpmPackage(value = "chart.js", version = "4.4.0")
public class ChartComponent extends Div {

    private final Element canvas;
    private final String canvasId;

    public ChartComponent() {
        this.canvasId = "chart-canvas-" + System.currentTimeMillis();
        this.canvas = new Element("canvas");
        canvas.setAttribute("id", canvasId);
        getElement().appendChild(canvas);
    }

    public void setChartData(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException("Arrays must have the same length");
        }

        // Формируем массив данных: [{x: x0, y: y0}, {x: x1, y: y1}, ...]
        StringBuilder dataBuilder = new StringBuilder("[");
        for (int i = 0; i < xValues.length; i++) {
            if (i > 0) dataBuilder.append(", ");
            dataBuilder.append("{x: ").append(xValues[i]).append(", y: ").append(yValues[i]).append("}");
        }
        dataBuilder.append("]");
        String dataPoints = dataBuilder.toString();

        // Форматирование чисел: до 2 знаков, без лишних нулей
        String formatFunction = """
            (value) => {
                if (typeof value !== 'number') return value;
                return parseFloat(value.toFixed(2)).toString();
            }
            """;

        String js = """
            (function() {
                const canvas = document.getElementById('%s');
                if (!canvas) return;
                const ctx = canvas.getContext('2d');

                const existingChart = Chart.getChart('%s');
                if (existingChart) existingChart.destroy();

                const formatNumber = %s;

                new Chart(ctx, {
                    type: 'line',
                    data: {
                        datasets: [{
                            label: 'Значения функции',
                            data: %s,
                            borderColor: 'rgb(54, 162, 235)',
                            backgroundColor: 'rgba(54, 162, 235, 0.1)',
                            borderWidth: 2,
                            tension: 0.3,
                            fill: false,
                            pointRadius: 2
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                display: true,
                                position: 'top'
                            },
                            tooltip: {
                                callbacks: {
                                    label: (context) => {
                                        return 'y = ' + formatNumber(context.parsed.y);
                                    },
                                    title: (tooltipItems) => {
                                        const x = tooltipItems[0].parsed.x;
                                        return 'x = ' + formatVectorNumber(x);
                                    }
                                }
                            }
                        },
                        scales: {
                            x: {
                                type: 'linear',
                                position: 'bottom',
                                title: { display: true, text: 'X' },
                                ticks: {
                                    callback: formatNumber
                                }
                            },
                            y: {
                                type: 'linear',
                                title: { display: true, text: 'Y' },
                                ticks: {
                                    callback: formatNumber
                                }
                            }
                        }
                    }
                });

                // Вспомогательная функция для форматирования в заголовке тултипа
                function formatVectorNumber(value) {
                    return parseFloat(value.toFixed(2)).toString();
                }
            })();
            """.formatted(canvasId, canvasId, formatFunction, dataPoints);

        getElement().executeJs(js);
    }

    public void clearChart() {
        getElement().executeJs("Chart.getChart('" + canvasId + "')?.destroy();");
    }
}