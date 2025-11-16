package ru.ssau.tk.faible.labs.database.utils;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;

public final class SqlHelper {

    private static final Logger log = LoggerFactory.getLogger(SqlHelper.class);

    private SqlHelper() {}

    // статический метод для конвертирования .sql файла из папки resources в строку
    public static String loadSqlFromFile(String filePath){
        log.info("Пытаемся спарсить sql-запрос");
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(filePath)) {
            if (inputStream == null) throw new IOException();
            String sql = new String(inputStream.readAllBytes());
            log.info("Парсинг прошел успешно");
            return sql;
        } catch (IOException e) {
            log.error("Во время парсинга произошла ошибка");
            throw new RuntimeException(e);
        }
    }

    // Хеширование пароля
    public static String hashPassword(String plainPassword) {
        log.info("Хэшируем пароль");
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // проверка пароля
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        log.info("Проверяем пароль");
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
