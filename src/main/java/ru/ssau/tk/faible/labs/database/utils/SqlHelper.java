package ru.ssau.tk.faible.labs.database.utils;

import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class SqlHelper {

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
