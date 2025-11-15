package ru.ssau.tk.faible.labs.database;

import java.io.IOException;
import java.io.InputStream;

public final class SqlHelper {

    private SqlHelper() {}

    // статический метод для конвертирования .sql файла из папки resources в строку
    public static String loadSqlFromFile(String filePath) throws IOException {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(filePath)) {
            if (inputStream == null) throw new IOException();
            return new String(inputStream.readAllBytes());
        }
    }
}
