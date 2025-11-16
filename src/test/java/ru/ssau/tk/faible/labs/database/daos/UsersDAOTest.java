package ru.ssau.tk.faible.labs.database.daos;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.ssau.tk.faible.labs.database.models.User;
import ru.ssau.tk.faible.labs.database.utils.DBConnector;
import ru.ssau.tk.faible.labs.database.utils.SqlHelper;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsersDAOTest {

    private UsersDAO usersDAO;

    @BeforeEach
    void setUp() {
        usersDAO = new UsersDAO(DBConnector.initConnect());
    }

    @AfterEach
    void close() {
        DBConnector.closeConnection(usersDAO.getConnection());
    }

    @Test
    void selectAllUsernames() {

        List<String> usernames = usersDAO.selectAllUsernames();
        assertEquals("new_user", usernames.get(0));
        assertEquals("new_admin", usernames.get(1));
    }

    @Test
    void getUserById() {
        User user = usersDAO.getUserById(1);
        assertEquals(1, user.getId());
        assertEquals("new_user", user.getUsername());
        assertEquals("123fgsd3f21", user.getPassword_hash());
        assertEquals("array", user.getFactory_type());
        assertEquals("user", user.getRole());
    }

    @Test
    void getUserByUsername() {
        User user = usersDAO.getUserByUsername("new_user");
        assertEquals(1, user.getId());
        assertEquals("new_user", user.getUsername());
        assertEquals("123fgsd3f21", user.getPassword_hash());
        assertEquals("array", user.getFactory_type());
        assertEquals("user", user.getRole());
    }

    @Test
    void deleteUserById() {
        assertEquals(1, usersDAO.deleteUserById(1));
    }

    @Test
    void insertUser() {
        assertEquals(1, usersDAO.insertUser("Tom", "123321", "array", "user"));
    }

    @Test
    void updateFactoryType() {
        assertEquals(1, usersDAO.updateFactoryType("linkedList", 1));
        User user = usersDAO.getUserById(1);
        assertEquals("linkedList", user.getFactory_type());
    }

    @Test
    void updatePassword() {
        assertEquals(1, usersDAO.updatePassword("123321", "54321", 3));
        assertTrue(SqlHelper.checkPassword("54321", usersDAO.getPasswordHashById(3)));
    }

    @Test
    void updateUsername() {
        assertEquals(1, usersDAO.updateUsername("Bob", 3));
        assertEquals("Bob", usersDAO.getUsernameById(3));
    }
}