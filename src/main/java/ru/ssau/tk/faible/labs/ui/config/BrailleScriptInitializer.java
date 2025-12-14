// src/main/java/ru/ssau/tk/faible/labs/ui/config/BrailleScriptInitializer.java

package ru.ssau.tk.faible.labs.ui.config;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.ServiceInitEvent;
import org.springframework.stereotype.Component;

@Component
public class BrailleScriptInitializer implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        // Отладка: проверим, вызывается ли этот метод
        System.out.println("✅ BrailleScriptInitializer.serviceInit() вызван");

        event.getSource().addUIInitListener(uiInitEvent -> {
            UI ui = uiInitEvent.getUI();
            // Отладка: проверим, вызывается ли attach
            System.out.println("✅ UI attached, injecting Braille script");

            // Внедряем скрипт при подключении UI
            ui.getPage().executeJs(getBrailleScript());
        });
    }

    private String getBrailleScript() {
        return """
            if (typeof window.brailleModeActive === 'undefined') {
                (function() {
                    const brailleMap = {
                        'а': '⠁', 'б': '⠃', 'в': '⠺', 'г': '⠛', 'д': '⠙', 'е': '⠑', 'ё': '⠑', 'ж': '⠚', 'з': '⠵', 'и': '⠊',
                        'й': '⠊', 'к': '⠅', 'л': '⠇', 'м': '⠍', 'н': '⠝', 'о': '⠕', 'п': '⠏', 'р': '⠗', 'с': '⠎', 'т': '⠞',
                        'у': '⠥', 'ф': '⠋', 'х': '⠓', 'ц': '⠉', 'ч': '⠟', 'ш': '⠱', 'щ': '⠱', 'ъ': '⠈', 'ы': '⠽', 'ь': '⠮',
                        'э': '⠿', 'ю': '⠳', 'я': '⠳',
                        'A': '⠁', 'B': '⠃', 'C': '⠉', 'D': '⠙', 'E': '⠑', 'F': '⠋', 'G': '⠛', 'H': '⠓', 'I': '⠊', 'J': '⠚',
                        'K': '⠅', 'L': '⠇', 'M': '⠍', 'N': '⠝', 'O': '⠕', 'P': '⠏', 'Q': '⠟', 'R': '⠗', 'S': '⠎', 'T': '⠞',
                        'U': '⠥', 'V': '⠧', 'W': '⠺', 'X': '⠭', 'Y': '⠽', 'Z': '⠵',
                        '1': '⠁', '2': '⠃', '3': '⠉', '4': '⠙', '5': '⠑', '6': '⠋', '7': '⠛', '8': '⠓', '9': '⠊', '0': '⠚',
                        ' ': ' ', '.': '.', ',': ',', '!': '!', '?': '?', '-': '⠤', '(': '⠣', ')': '⠜', '[': '⠨', ']': '⠨', '"': '"', "'": "'", ':': ':', ';': ';'
                    };

                    function convertToBraille(text) {
                        let result = '';
                        for (let char of text) {
                            result += brailleMap[char] || char;
                        }
                        return result;
                    }

                    function restoreOriginalText(node) {
                             if (node.nodeType === Node.TEXT_NODE) {
                                 // Сначала пытаемся восстановить из родительского элемента
                                 let originalText = node.parentElement?.getAttribute('data-original-text');
                                 if (originalText) {
                                     node.nodeValue = originalText;
                                     node.parentElement.removeAttribute('data-original-text');
                                 } else {
                                     // Если не нашли в родителе, проверяем сам текстовый узел
                                     if (node.hasAttribute('data-original-text-node')) {
                                         node.nodeValue = node.getAttribute('data-original-text-node');
                                         node.removeAttribute('data-original-text-node');
                                     }
                                     // Если атрибута нет — значит, это был текст без сохранённого оригинала (например, пробелы)
                                     // Оставляем как есть
                                 }
                             } else if (node.nodeType === Node.ELEMENT_NODE) {
                                 // Рекурсивно проходим по дочерним узлам
                                 for (let child of node.childNodes) {
                                     restoreOriginalText(child);
                                 }
                             }
                         }

                    function replaceTextNodes(node) {
                        if (node.nodeType === Node.TEXT_NODE) {
                            let originalText = node.nodeValue;
                            if (originalText && originalText.trim() !== '') {
                                let parentElement = node.parentElement;
                                if (parentElement && !parentElement.hasAttribute('data-original-text')) {
                                    parentElement.setAttribute('data-original-text', originalText);
                                } else if (!node.hasAttribute('data-original-text-node')) {
                                    node.setAttribute('data-original-text-node', originalText);
                                }
                                let brailleText = convertToBraille(originalText);
                                node.nodeValue = brailleText;
                            }
                        } else if (node.nodeType === Node.ELEMENT_NODE) {
                            for (let child of node.childNodes) {
                                replaceTextNodes(child);
                            }
                        }
                    }

                    window.activateBrailleMode = function() {
                        if (window.brailleModeActive) {
                            console.log("Режим Брайля уже активен.");
                            return;
                        }

                        replaceTextNodes(document.body);

                        window.brailleObserver = new MutationObserver(function(mutationsList) {
                            for (let mutation of mutationsList) {
                                if (mutation.type === 'childList') {
                                    for (let node of mutation.addedNodes) {
                                        if (node.nodeType === Node.ELEMENT_NODE) {
                                            replaceTextNodes(node);
                                        } else if (node.nodeType === Node.TEXT_NODE) {
                                            let originalText = node.nodeValue;
                                            if (originalText && originalText.trim() !== '') {
                                                node.setAttribute('data-original-text-node', originalText);
                                                let brailleText = convertToBraille(originalText);
                                                node.nodeValue = brailleText;
                                            }
                                        }
                                    }
                                }
                            }
                        });

                        window.brailleObserver.observe(document.body, {
                            childList: true,
                            subtree: true
                        });

                        window.brailleModeActive = true;

                        if (!window.brailleStyleElement) {
                            window.brailleStyleElement = document.createElement('style');
                            window.brailleStyleElement.textContent = `* { font-family: monospace !important; }`;
                            document.head.appendChild(window.brailleStyleElement);
                        }

                        console.log("Режим Брайля включён.");
                    };

                    window.deactivateBrailleMode = function() {
                        if (!window.brailleModeActive) {
                            console.log("Режим Брайля уже выключен.");
                            return;
                        }

                        if (window.brailleObserver) {
                            window.brailleObserver.disconnect();
                            window.brailleObserver = null;
                        }

                        restoreOriginalText(document.body);

                        if (window.brailleStyleElement) {
                            window.brailleStyleElement.remove();
                            window.brailleStyleElement = null;
                        }

                        window.brailleModeActive = false;

                        console.log("Режим Брайля выключен.");
                    };
                })();
            }
            """;
    }
}