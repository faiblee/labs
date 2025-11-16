package ru.ssau.tk.faible.labs.database.daos;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.ssau.tk.faible.labs.database.models.Function;
import ru.ssau.tk.faible.labs.database.utils.SqlHelper;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@Getter
@Slf4j
public class FunctionsDAO {
    private final Connection connection;

    public FunctionsDAO(Connection connection) {
        this.connection = connection;
    }

    public Function getFunctionById(int id) {
        Function function;
        log.info("Пытаемся получить function по id = {}", id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/functions/get_function_by_id.sql"))) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                function = new Function();
                resultSet.next();
                function.setId(resultSet.getInt("id"));
                function.setName(resultSet.getString("name"));
                function.setOwner_id(resultSet.getInt("owner_id"));
                function.setType(resultSet.getString("type"));
                log.info("Успешно получена function с id = {}", id);
                return function;
            }
        } catch (SQLException e) {
            log.error("Ошибка при получении function по id = {}", id);
            throw new RuntimeException(e);
        }
    }

    public List<Function> getFunctionsByOwnerId(int owner_id) {
        List<Function> functions = new LinkedList<>();
        log.info("Пытаемся получить все function по owner_id = {}", owner_id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/functions/get_functions_by_owner_id.sql"))) {
            preparedStatement.setInt(1, owner_id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    Function function = new Function();
                    function.setId(resultSet.getInt("id"));
                    function.setName(resultSet.getString("name"));
                    function.setOwner_id(resultSet.getInt("owner_id"));
                    function.setType(resultSet.getString("type"));
                    functions.add(function);
                }
                log.info("Успешно получены все function с owner_id = {}", owner_id);
                return functions;
            }
        } catch (SQLException e) {
            log.error("Ошибка при получении всех function по owner_id = {}", owner_id);
            throw new RuntimeException(e);
        }
    }

    public int insertFunction(String name, int owner_id, String type) {
        log.info("Пытаемся добавить новую функцию в таблицу functions");
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                SqlHelper.loadSqlFromFile("scripts/functions/insert_function.sql"),
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, owner_id);
            preparedStatement.setString(3, type);

            int changedRows = preparedStatement.executeUpdate();
            log.info("Функция успешно добавлена");
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int functionId = generatedKeys.getInt(1);
                    log.info("Сгенерированный id успешно получен");
                    return functionId;
                }
            }
            log.info("Сгенерированный id получить не удалось");
            return -1;
        } catch (SQLException e) {
            log.error("Произошла ошибка при добавлении функции в таблицу functions");
            throw new RuntimeException(e);
        }
    }

    public int deleteAllFunctions() {
        log.info("Пытаемся удалить все записи в таблице functions");
        try (Statement statement = connection.createStatement()) {
            int changedRows = statement.executeUpdate(SqlHelper.loadSqlFromFile("scripts/functions/delete_all_functions.sql"));
            log.info("Все записи в таблице functions успешно удалены");
            return changedRows;
        } catch (SQLException e) {
            log.error("Ошибка при удалении всех записей в таблице functions");
            throw new RuntimeException(e);
        }
    }

    public int deleteFunctionById(int id) {
        log.info("Пытаемся удалить function по id = {}", id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/functions/delete_function_by_id.sql"))) {
            preparedStatement.setInt(1, id);
            int changedRows = preparedStatement.executeUpdate();
            if (changedRows > 0) {
                log.info("Function с id = {} была успешно удалена", id);
            } else {
                log.warn("Function с id = {} не была удалена", id);
            }
            return changedRows;
        } catch (SQLException e) {
            log.info("При удалении function с id {} возникла ошибка", id);
            throw new RuntimeException(e);
        }
    }

    public int updateName(String username, int id) {
        log.info("Пытаемся обновить username у function с id = {}", id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/functions/update_function_name_by_id.sql"))) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, id);
            int changedRows = preparedStatement.executeUpdate();
            if (changedRows > 0) {
                log.info("Успешно обновлен username у function с id = {}", id);
            } else {
                log.warn("Username не был обновлен у function с id = {}", id);
            }
            return changedRows;
        } catch (SQLException e) {
            log.error("Ошибка при изменении username у function с id = {}", id);
            throw new RuntimeException(e);
        }
    }

    public int updateType(String type, int id) {
        log.info("Пытаемся обновить type у function с id = {}", id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/functions/update_function_type_by_id.sql"))) {
            preparedStatement.setString(1, type);
            preparedStatement.setInt(2, id);
            int changedRows = preparedStatement.executeUpdate();
            if (changedRows > 0) {
                log.info("Успешно обновлен type у function с id = {}", id);
            } else {
                log.warn("type не был обновлен у функции с id = {}", id);
            }
            return changedRows;
        } catch (SQLException e) {
            log.error("Ошибка при изменении type у function с id = {}", id);
            throw new RuntimeException(e);
        }
    }
}
