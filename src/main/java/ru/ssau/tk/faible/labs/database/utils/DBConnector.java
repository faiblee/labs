package ru.ssau.tk.faible.labs.database.utils;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class DBConnector {

    private static final String url = "jdbc:postgresql://localhost:5432/functions_db";
    private static final String user = "postgres";
    private static final String password = "ghjvgtym";

    public static Connection initConnect() {
        Connection connection;
        try {
            log.info("Пытаемся подключиться к БД");
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            log.error("Подключиться не удалось");
            throw new RuntimeException(e);
        }
        log.info("Подключение прошло успешно");
        return connection;
    }

    public static void closeConnection(Connection connection) {
        log.info("Закрываем подключение к БД");
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("Ошибка при закрытии подключения");
            throw new RuntimeException(e);
        }
        log.info("Подключение успешно закрыто");
    }
}
