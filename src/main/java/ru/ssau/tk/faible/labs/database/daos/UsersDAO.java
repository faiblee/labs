package ru.ssau.tk.faible.labs.database.daos;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import ru.ssau.tk.faible.labs.database.models.User;
import ru.ssau.tk.faible.labs.database.utils.SqlHelper;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@Getter
@Slf4j
public class UsersDAO {
    private final Connection connection;

    public UsersDAO(Connection connection) {
        this.connection = connection;
    }

    public List<String> selectAllUsernames() {
        List<String> usernames = new LinkedList<>();
        log.info("Пытаемся получить все username в таблице users");
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SqlHelper.loadSqlFromFile("scripts/users/select_all_usernames.sql"))
            ) {
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
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                user = new User();
                if (resultSet.next()) {
                    user.setId(resultSet.getInt("id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword_hash(resultSet.getString("password_hash"));
                    user.setFactoryType(resultSet.getString("factory_type"));
                    user.setRole(resultSet.getString("role"));
                    log.info("Успешно получен User с id = {}", id);
                    return user;
                } else {
                    log.warn("User с id = {} не был найден", id);
                    return null;
                }
            }
        } catch (SQLException e) {
            log.error("Ошибка при получении user по id = {}", id);
            throw new RuntimeException(e);
        }
    }

    public List<User> selectAllUsers() {
        List<User> users = new LinkedList<>();
        log.info("Пытаемся получить все записи в таблице users");
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SqlHelper.loadSqlFromFile("scripts/users/get_all_users.sql"))
        ) {
            log.info("ResultSet для users успешно получен");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password_hash = resultSet.getString("password_hash");
                String factory_type = resultSet.getString("factory_type");
                String role = resultSet.getString("role");
                users.add(new User(id, username, password_hash, factory_type, role));
            }
        } catch (SQLException e) {
            log.error("Ошибка при получении всех записей в users");
            e.printStackTrace();
        }
        log.info("Все записи в users успешно получены");
        return users;
    }

    public List<User> getUsersByRole(String role) {
        List<User> users = new LinkedList<>();
        log.info("Пытаемся получить users по role = {}", role);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/users/find_users_by_role.sql"))) {
            preparedStatement.setString(1, role);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword_hash(resultSet.getString("password_hash"));
                    user.setFactoryType(resultSet.getString("factory_type"));
                    user.setRole(resultSet.getString("role"));
                    users.add(user);
                }
                log.info("Успешно получены все User с role = {}", role);
                return users;
            }
        } catch (SQLException e) {
            log.error("Ошибка при получении user по role = {}", role);
            throw new RuntimeException(e);
        }
    }

    public String getPasswordHashById(int id) {
        log.info("Пытаемся получить password_hash по id = {}", id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/users/get_password_hash_by_id.sql"))) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                String password_hash = resultSet.getString("password_hash");
                log.info("Успешно получен password у user с id = {}", id);
                return password_hash;
            }
        } catch (SQLException e) {
            log.error("Ошибка при получении password у user с id = {}", id);
            throw new RuntimeException(e);
        }
    }

    public String getUsernameById(int id) {
        log.info("Пытаемся получить username по id = {}", id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/users/get_username_by_id.sql"))) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                String username = resultSet.getString("username");
                log.info("Успешно получен username у user с id = {}", id);
                return username;
            }
        } catch (SQLException e) {
            log.error("Ошибка при получении username у user с id = {}", id);
            throw new RuntimeException(e);
        }
    }

    public User getUserByUsername(String username) {
        User user;
        log.info("Пытаемся получить user по username = {}", username);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/users/find_user_by_username.sql"))) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                user = new User();
                resultSet.next();
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword_hash(resultSet.getString("password_hash"));
                user.setFactoryType(resultSet.getString("factory_type"));
                user.setRole(resultSet.getString("role"));
                log.info("Успешно получен User с username = {}", username);
                return user;
            }
        } catch (SQLException e) {
            log.error("Ошибка при получении user по username = {}", username);
            throw new RuntimeException(e);
        }
    }

    public int insertUser(String username, String password, String factory_type, String role) {
        log.info("Пытаемся добавить пользователя");
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                SqlHelper.loadSqlFromFile("scripts/users/insert_user.sql"),
                Statement.RETURN_GENERATED_KEYS)
            ) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, SqlHelper.hashPassword(password));
            preparedStatement.setString(3, factory_type);
            preparedStatement.setString(4, role);
            int changedRows = preparedStatement.executeUpdate();
            log.info("user успешно добавлен");

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                generatedKeys.next();
                int generatedId = generatedKeys.getInt(1);
                log.info("Сгенерированный id успешно получен");
                return generatedId;
            }
        } catch (SQLException e) {
            log.error("Ошибка при добавлении user");
            throw new RuntimeException(e);
        }
    }

    public int insertUser(User user) {
        log.info("Пытаемся добавить пользователя User");
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                SqlHelper.loadSqlFromFile("scripts/users/insert_user.sql"),
                Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword_hash());
            preparedStatement.setString(3, user.getFactoryType());
            preparedStatement.setString(4, user.getRole());
            int changedRows = preparedStatement.executeUpdate();
            log.info("User успешно добавлен");

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                generatedKeys.next();
                int generatedId = generatedKeys.getInt(1);
                log.info("Сгенерированный Id успешно получен");
                return generatedId;
            }
        } catch (SQLException e) {
            log.error("Ошибка при добавлении User");
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

    public int updateRole(String role, int id) {
        log.info("Пытаемся обновить role у пользователя с id = {}", id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlHelper.loadSqlFromFile("scripts/users/update_role_by_id.sql"))) {
            preparedStatement.setString(1, role);
            preparedStatement.setInt(2, id);

            int changedRows = preparedStatement.executeUpdate();
            log.info("Успешно обновлен role у user с id = {}", id);
            return changedRows;
        } catch (SQLException e) {
            log.error("Ошибка при изменении role у user с id = {}", id);
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

    public int deleteAllUsers() {
        log.info("Пытаемся удалить все записи в таблице users");
        try (Statement statement = connection.createStatement()) {
            int changedRows = statement.executeUpdate(SqlHelper.loadSqlFromFile("scripts/users/delete_all_users.sql"));
            log.info("Все записи в таблице users успешно удалены");
            return changedRows;
        } catch (SQLException e) {
            log.error("Ошибка при удалении всех записей в таблице users");
            throw new RuntimeException(e);
        }
    }
}
