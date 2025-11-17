package ru.ssau.tk.faible.labs.database.daos;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.ssau.tk.faible.labs.database.models.Point;
import ru.ssau.tk.faible.labs.database.utils.SqlHelper;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Getter
public class PointsDAO {
    private final Connection connection;

    public PointsDAO(Connection connection) {
        this.connection = connection;
    }

    public int insertPoint(double x_value, double y_value, int function_id) {
        log.info("Пытаемся добавить новую point в таблицу points");
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                SqlHelper.loadSqlFromFile("scripts/points/insert_point.sql"),
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setDouble(1, x_value);
            preparedStatement.setDouble(2, y_value);
            preparedStatement.setInt(3, function_id);

            int changedRows = preparedStatement.executeUpdate();
            if (changedRows > 0) {
                log.info("Point успешно добавлена");
            } else {
                log.warn("Point не была добавлена");
            }
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int pointId = generatedKeys.getInt(1);
                    log.info("Сгенерированный id успешно получен");
                    return pointId;
                }
            }
            log.warn("Сгенерированный id получить не удалось");
            return -1;
        } catch (SQLException e) {
            log.error("Произошла ошибка при добавлении функции в таблицу functions");
            throw new RuntimeException(e);
        }
    }

    public int deleteAllPoints() {
        log.info("Пытаемся удалить все записи в таблице points");
        try (Statement statement = connection.createStatement()) {
            int changedRows = statement.executeUpdate(SqlHelper.loadSqlFromFile("scripts/points/delete_all_points.sql"));
            if (changedRows > 0) {
                log.info("Все записи в таблице points успешно удалены");
            } else {
                log.warn("Записи в таблице points не были удалены");
            }
            return changedRows;
        } catch (SQLException e) {
            log.error("Ошибка при удалении всех записей в таблице points");
            throw new RuntimeException(e);
        }
    }

    public int deletePointById(int id) {
        log.info("Пытаемся удалить point по id = {}", id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/points/delete_point_by_id.sql"))) {
            preparedStatement.setInt(1, id);
            int changedRows = preparedStatement.executeUpdate();
            if (changedRows > 0) {
                log.info("point с id = {} была успешно удалена", id);
            } else {
                log.warn("point с id = {} не была удалена", id);
            }
            return changedRows;
        } catch (SQLException e) {
            log.info("При удалении point с id {} возникла ошибка", id);
            throw new RuntimeException(e);
        }
    }

    public List<Point> getPointsByFunctionIdAndBetweenXValue(int function_id, double xStart, double xEnd) {
        List<Point> points = new LinkedList<>();
        log.info("Пытаемся получить все points по function_id = {} и x от {} до {}", function_id, xStart, xEnd);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/points/find_point_by_function_id_and_between_x_value.sql"))) {
            preparedStatement.setInt(1, function_id);
            preparedStatement.setDouble(2, xStart);
            preparedStatement.setDouble(3, xEnd);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    Point point = new Point();
                    point.setId(resultSet.getInt("id"));
                    point.setX_value(resultSet.getDouble("x_value"));
                    point.setY_value(resultSet.getDouble("y_value"));
                    point.setFunction_id(resultSet.getInt("function_id"));
                    points.add(point);
                }
                log.info("Успешно получены все points по function_id = {} и x от {} до {}", function_id, xStart, xEnd);
                return points;
            }
        } catch (SQLException e) {
            log.error("Ошибка при получении всех points по function_id = {} и x от {} до {}", function_id, xStart, xEnd);
            throw new RuntimeException(e);
        }
    }

    public Point getPointById(int id) {
        Point point;
        log.info("Пытаемся получить point по id = {}", id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/points/find_point_by_id.sql"))) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                point = new Point();
                resultSet.next();
                point.setId(resultSet.getInt("id"));
                point.setX_value(resultSet.getDouble("x_value"));
                point.setY_value(resultSet.getDouble("y_value"));
                point.setFunction_id(resultSet.getInt("function_id"));
                log.info("Успешно получена Point с id = {}", id);
                return point;
            }
        } catch (SQLException e) {
            log.error("Ошибка при получении point по id = {}", id);
            throw new RuntimeException(e);
        }
    }

    public int updatePointById(int id, double x_value, double y_value) {
        log.info("Пытаемся обновить x_value и y_value у point с id = {}", id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/points/update_point_by_id.sql"))) {
            preparedStatement.setDouble(1, x_value);
            preparedStatement.setDouble(2, y_value);
            preparedStatement.setInt(3, id);
            int changedRows = preparedStatement.executeUpdate();
            if (changedRows > 0) {
                log.info("Успешно обновлен x_value и y_value у point с id = {}", id);
            } else {
                log.warn("x_value и y_value не были обновлены у point с id = {}", id);
            }
            return changedRows;
        } catch (SQLException e) {
            log.error("Ошибка при изменении x_value и y_value у point с id = {}", id);
            throw new RuntimeException(e);
        }
    }

}
