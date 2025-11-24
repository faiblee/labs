package ru.ssau.tk.faible.labs.database.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class DBConnector {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DBConnector.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                log.error("Файл application.properties не найден в resources");
                throw new RuntimeException();
            }
            properties.load(input);
            log.info("Конфигурация загружена из application.properties");
        } catch (IOException e) {
            log.error("Ошибка при загрузке конфигурации");
            throw new RuntimeException(e);
        }
    }

    private static final String URL = properties.getProperty("db.url");
    private static final String USER = properties.getProperty("db.user");
    private static final String PASSWORD = properties.getProperty("db.password");

    public static Connection initConnect() {
        Connection connection;
        try {
            log.info("Пытаемся подключиться к БД");
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            log.error("Подключение к БД не удалось");
            throw new RuntimeException(e);
        }
        log.info("Подключение к БД успешно установлено");
        return connection;
    }

    public static void closeConnection(Connection connection) {
        log.info("Закрываем подключение к БД");
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error("Ошибка при закрытии подключения");
            throw new RuntimeException(e);
        }
        log.info("Подключение к БД успешно закрыто");
    }
}