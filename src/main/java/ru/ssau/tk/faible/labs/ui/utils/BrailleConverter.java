// src/main/java/ru/ssau/tk/faible/labs/utils/BrailleConverter.java

package ru.ssau.tk.faible.labs.ui.utils;

import java.util.HashMap;
import java.util.Map;

public class BrailleConverter {

    private static final Map<Character, String> BRAILLE_MAP = new HashMap<>();

    static {
        // Русские буквы
        BRAILLE_MAP.put('а', "⠁"); BRAILLE_MAP.put('б', "⠃"); BRAILLE_MAP.put('в', "⠺"); BRAILLE_MAP.put('г', "⠛");
        BRAILLE_MAP.put('д', "⠙"); BRAILLE_MAP.put('е', "⠑"); BRAILLE_MAP.put('ё', "⠑"); BRAILLE_MAP.put('ж', "⠚");
        BRAILLE_MAP.put('з', "⠵"); BRAILLE_MAP.put('и', "⠊"); BRAILLE_MAP.put('й', "⠊"); BRAILLE_MAP.put('к', "⠅");
        BRAILLE_MAP.put('л', "⠇"); BRAILLE_MAP.put('м', "⠍"); BRAILLE_MAP.put('н', "⠝"); BRAILLE_MAP.put('о', "⠕");
        BRAILLE_MAP.put('п', "⠏"); BRAILLE_MAP.put('р', "⠗"); BRAILLE_MAP.put('с', "⠎"); BRAILLE_MAP.put('т', "⠞");
        BRAILLE_MAP.put('у', "⠥"); BRAILLE_MAP.put('ф', "⠋"); BRAILLE_MAP.put('х', "⠓"); BRAILLE_MAP.put('ц', "⠉");
        BRAILLE_MAP.put('ч', "⠟"); BRAILLE_MAP.put('ш', "⠱"); BRAILLE_MAP.put('щ', "⠱"); BRAILLE_MAP.put('ъ', "⠈");
        BRAILLE_MAP.put('ы', "⠽"); BRAILLE_MAP.put('ь', "⠮"); BRAILLE_MAP.put('э', "⠿"); BRAILLE_MAP.put('ю', "⠳");
        BRAILLE_MAP.put('я', "⠳"); BRAILLE_MAP.put('А', "⠁"); BRAILLE_MAP.put('Б', "⠃"); BRAILLE_MAP.put('В', "⠺");
        BRAILLE_MAP.put('Г', "⠛"); BRAILLE_MAP.put('Д', "⠙"); BRAILLE_MAP.put('Е', "⠑"); BRAILLE_MAP.put('Ё', "⠑");
        BRAILLE_MAP.put('Ж', "⠚"); BRAILLE_MAP.put('З', "⠵"); BRAILLE_MAP.put('И', "⠊"); BRAILLE_MAP.put('Й', "⠊");
        BRAILLE_MAP.put('К', "⠅"); BRAILLE_MAP.put('Л', "⠇"); BRAILLE_MAP.put('М', "⠍"); BRAILLE_MAP.put('Н', "⠝");
        BRAILLE_MAP.put('О', "⠕"); BRAILLE_MAP.put('П', "⠏"); BRAILLE_MAP.put('Р', "⠗"); BRAILLE_MAP.put('С', "⠎");
        BRAILLE_MAP.put('Т', "⠞"); BRAILLE_MAP.put('У', "⠥"); BRAILLE_MAP.put('Ф', "⠋"); BRAILLE_MAP.put('Х', "⠓");
        BRAILLE_MAP.put('Ц', "⠉"); BRAILLE_MAP.put('Ч', "⠟"); BRAILLE_MAP.put('Ш', "⠱"); BRAILLE_MAP.put('Щ', "⠱");
        BRAILLE_MAP.put('Ъ', "⠈"); BRAILLE_MAP.put('Ы', "⠽"); BRAILLE_MAP.put('Ь', "⠮"); BRAILLE_MAP.put('Э', "⠿");
        BRAILLE_MAP.put('Ю', "⠳"); BRAILLE_MAP.put('Я', "⠳"); // 'Я' → как 'Ю

        //Латинские буквы
        BRAILLE_MAP.put('A', "⠁"); BRAILLE_MAP.put('B', "⠃"); BRAILLE_MAP.put('C', "⠉"); BRAILLE_MAP.put('D', "⠙");
        BRAILLE_MAP.put('E', "⠑"); BRAILLE_MAP.put('F', "⠋"); BRAILLE_MAP.put('G', "⠛"); BRAILLE_MAP.put('H', "⠓");
        BRAILLE_MAP.put('I', "⠊"); BRAILLE_MAP.put('J', "⠚"); BRAILLE_MAP.put('K', "⠅"); BRAILLE_MAP.put('L', "⠇");
        BRAILLE_MAP.put('M', "⠍"); BRAILLE_MAP.put('N', "⠝"); BRAILLE_MAP.put('O', "⠕"); BRAILLE_MAP.put('P', "⠏");
        BRAILLE_MAP.put('Q', "⠟"); BRAILLE_MAP.put('R', "⠗"); BRAILLE_MAP.put('S', "⠎"); BRAILLE_MAP.put('T', "⠞");
        BRAILLE_MAP.put('U', "⠥"); BRAILLE_MAP.put('V', "⠧"); BRAILLE_MAP.put('W', "⠺"); BRAILLE_MAP.put('X', "⠭");
        BRAILLE_MAP.put('Y', "⠽"); BRAILLE_MAP.put('Z', "⠵"); BRAILLE_MAP.put('A', "⠁"); BRAILLE_MAP.put('B', "⠃");
        BRAILLE_MAP.put('C', "⠉"); BRAILLE_MAP.put('D', "⠙"); BRAILLE_MAP.put('E', "⠑"); BRAILLE_MAP.put('F', "⠋");
        BRAILLE_MAP.put('G', "⠛"); BRAILLE_MAP.put('H', "⠓"); BRAILLE_MAP.put('I', "⠊"); BRAILLE_MAP.put('J', "⠚");
        BRAILLE_MAP.put('K', "⠅"); BRAILLE_MAP.put('L', "⠇"); BRAILLE_MAP.put('M', "⠍"); BRAILLE_MAP.put('N', "⠝");
        BRAILLE_MAP.put('O', "⠕"); BRAILLE_MAP.put('P', "⠏"); BRAILLE_MAP.put('Q', "⠟"); BRAILLE_MAP.put('R', "⠗");
        BRAILLE_MAP.put('S', "⠎"); BRAILLE_MAP.put('T', "⠞"); BRAILLE_MAP.put('U', "⠥"); BRAILLE_MAP.put('V', "⠧");
        BRAILLE_MAP.put('W', "⠺"); BRAILLE_MAP.put('X', "⠭"); BRAILLE_MAP.put('Y', "⠽"); BRAILLE_MAP.put('Z', "⠵");

        // Цифры
        BRAILLE_MAP.put('1', "⠁"); BRAILLE_MAP.put('2', "⠃"); BRAILLE_MAP.put('3', "⠉"); BRAILLE_MAP.put('4', "⠙");
        BRAILLE_MAP.put('5', "⠑"); BRAILLE_MAP.put('6', "⠋"); BRAILLE_MAP.put('7', "⠛"); BRAILLE_MAP.put('8', "⠓");
        BRAILLE_MAP.put('9', "⠊"); BRAILLE_MAP.put('0', "⠚");

        // Знаки препинания и спецсимволы
        BRAILLE_MAP.put(' ', " "); BRAILLE_MAP.put('.', "."); BRAILLE_MAP.put(',', ","); BRAILLE_MAP.put('!', "!");
        BRAILLE_MAP.put('?', "?"); BRAILLE_MAP.put('-', "⠤"); BRAILLE_MAP.put('(', "⠣"); BRAILLE_MAP.put(')', "⠜");
        BRAILLE_MAP.put('[', "⠨"); BRAILLE_MAP.put(']', "⠨"); BRAILLE_MAP.put('"', "\""); BRAILLE_MAP.put('\'', "'");
        BRAILLE_MAP.put(':', ":"); BRAILLE_MAP.put(';', ";");
    }

    // Преобразует строку в шрифт Брайля
    public static String convert(String input) {
        if (input == null) return null;

        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            String brailleChar = BRAILLE_MAP.get(c);
            result.append(brailleChar != null ? brailleChar : c);
        }
        return result.toString();
    }
}