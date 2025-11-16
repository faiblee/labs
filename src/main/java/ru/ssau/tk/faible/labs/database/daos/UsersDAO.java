package ru.ssau.tk.faible.labs.database.daos;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk.faible.labs.database.models.User;
import ru.ssau.tk.faible.labs.database.utils.SqlHelper;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class UsersDAO {
    private static final Logger log = LoggerFactory.getLogger(UsersDAO.class);
    private final Connection connection;

    public UsersDAO(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public List<String> selectAllUsernames() {
        List<String> usernames = new LinkedList<>();
        log.info("Пытаемся получить все username в таблице users");
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SqlHelper.loadSqlFromFile("scripts/users/select_all_usernames.sql"));
            log.info("ResultSet успешно получен");
            while (resultSet.next()) {
                usernames.add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            log.error("Ошибка при получении usernames");
            e.printStackTrace();
        }
        log.info("Все usernames успешно получены");
        return usernames;
    }

    public User getUserById(int id) {
        User user;
        log.info("Пытаемся получить user по id = {}", id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/users/find_user_by_id.sql"))) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            user = new User();
            resultSet.next();
            user.setId(resultSet.getInt("id"));
            user.setUsername(resultSet.getString("username"));
            user.setPassword_hash(resultSet.getString("password_hash"));
            user.setFactory_type(resultSet.getString("factory_type"));
            user.setRole(resultSet.getString("role"));
            log.info("Успешно получен User с id = {}", id);
            return user;
        } catch (SQLException e) {
            log.error("Ошибка при получении user по id = {}", id);
            throw new RuntimeException(e);
        }
    }

    public User getUserByUsername(String username) {
        User user;
        log.info("Пытаемся получить user по username = {}", username);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/users/find_user_by_username.sql"))) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            user = new User();
            resultSet.next();
            user.setId(resultSet.getInt("id"));
            user.setUsername(resultSet.getString("username"));
            user.setPassword_hash(resultSet.getString("password_hash"));
            user.setFactory_type(resultSet.getString("factory_type"));
            user.setRole(resultSet.getString("role"));
            log.info("Успешно получен User с username = {}", username);
            return user;
        } catch (SQLException e) {
            log.error("Ошибка при получении user по username = {}", username);
            throw new RuntimeException(e);
        }
    }

    public int deleteUserById(int id) {
        log.info("Пытаемся удалить user по id = {}", id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/users/delete_user_by_id.sql"))) {
            preparedStatement.setInt(1, id);
            int changedRows = preparedStatement.executeUpdate();
            log.info("user с id = {} успешно удален", id);
            return changedRows;
        } catch (SQLException e) {
            log.error("Ошибка при удалении user по id = {}", id);
            throw new RuntimeException(e);
        }
    }

    public int insertUser(String username, String password, String factory_type, String role) {
        log.info("Пытаемся добавить пользователя");
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/users/insert_user.sql"))) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, SqlHelper.hashPassword(password));
            preparedStatement.setString(3, factory_type);
            preparedStatement.setString(4, role);
            int changedRows = preparedStatement.executeUpdate();
            log.info("user успешно добавлен");
            return changedRows;
        } catch (SQLException e) {
            log.error("Ошибка при добавлении user");
            throw new RuntimeException(e);
        }
    }

    public int updateFactoryType(String factory_type, int id) {
        log.info("Пытаемся обновить factory_type у пользователя с id = {}", id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/users/update_user_factory_type.sql"))) {
            preparedStatement.setString(1, factory_type);
            preparedStatement.setInt(2, id);

            int changedRows = preparedStatement.executeUpdate();
            log.info("Успешно обновлен factory_type у user с id = {}", id);
            return changedRows;
        } catch (SQLException e) {
            log.error("Ошибка при изменении factory_type у user с id = {}", id);
            throw new RuntimeException(e);
        }
    }

    public String getPasswordHashById(int id) {
        log.info("Пытаемся получить password_hash по id = {}", id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/users/get_password_hash_by_id.sql"))) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String password_hash = resultSet.getString("password_hash");
            log.info("Успешно получен password у user с id = {}", id);
            return password_hash;
        } catch (SQLException e) {
            log.error("Ошибка при получении password у user с id = {}", id);
            throw new RuntimeException(e);
        }
    }

    public String getUsernameById(int id) {
        log.info("Пытаемся получить username по id = {}", id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/users/get_username_by_id.sql"))) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String username = resultSet.getString("username");
            log.info("Успешно получен username у user с id = {}", id);
            return username;
        } catch (SQLException e) {
            log.error("Ошибка при получении username у user с id = {}", id);
            throw new RuntimeException(e);
        }
    }

    public int updatePassword(String old_password, String new_password, int id) {
        log.info("Пытаемся обновить password у пользователя с id = {}", id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/users/update_user_password.sql"))) {

            String old_hash_password = getPasswordHashById(id);

            if (!BCrypt.checkpw(old_password, old_hash_password)) { // если введен неверный старый пароль
                log.error("Введен неверный старый пароль");
                throw new IllegalArgumentException("Введен неверный старый пароль");
            }
            preparedStatement.setString(1, SqlHelper.hashPassword(new_password));
            preparedStatement.setInt(2, id);
            int changedRows = preparedStatement.executeUpdate();
            log.info("Успешно обновлен password у user с id = {}", id);
            return changedRows;
        } catch (SQLException e) {
            log.error("Ошибка при изменении password у user с id = {}", id);
            throw new RuntimeException(e);
        }
    }

    public int updateUsername(String username, int id) {
        log.info("Пытаемся обновить username у пользователя с id = {}", id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/users/update_user_username.sql"))) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, id);

            int changedRows = preparedStatement.executeUpdate();
            log.info("Успешно обновлен username у user с id = {}", id);
            return changedRows;
        } catch (SQLException e) {
            log.error("Ошибка при изменении username у user с id = {}", id);
            throw new RuntimeException(e);
        }
    }

}
