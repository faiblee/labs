package ru.ssau.tk.faible.labs.database.daos;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk.faible.labs.database.models.User;
import ru.ssau.tk.faible.labs.database.utils.DBConnector;
import ru.ssau.tk.faible.labs.database.utils.SqlHelper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsersDAOTest {

    private UsersDAO usersDAO;
    int TomId;
    int BobId;
    int SteveId;
    @BeforeEach
    void setUp() {
        usersDAO = new UsersDAO(DBConnector.initConnect());
        usersDAO.deleteAllUsers();
        TomId = usersDAO.insertUser("Tom", "123321", "array", "user");
        BobId = usersDAO.insertUser("Bob", "qwerty", "linkedList", "user");
        SteveId = usersDAO.insertUser("Steve", "012362", "array", "admin");
    }

    @AfterEach
    void close() {
        DBConnector.closeConnection(usersDAO.getConnection());
    }

    @Test
    void selectAllUsernames() {
        List<String> usernames = usersDAO.selectAllUsernames();
        assertEquals("Tom", usernames.get(0));
        assertEquals("Bob", usernames.get(1));
        assertEquals("Steve", usernames.get(2));
    }

    @Test
    void getUserById() {
        User user = usersDAO.getUserById(TomId);
        assertEquals(TomId, user.getId());
        assertEquals("Tom", user.getUsername());
        assertTrue(SqlHelper.checkPassword("123321", user.getPassword_hash()));
        assertEquals("array", user.getFactory_type());
        assertEquals("user", user.getRole());
    }

    @Test
    void getUserByUsername() {
        User user = usersDAO.getUserByUsername("Bob");
        assertEquals(BobId, user.getId());
        assertEquals("Bob", user.getUsername());
        assertTrue(SqlHelper.checkPassword("qwerty", user.getPassword_hash()));
        assertEquals("linkedList", user.getFactory_type());
        assertEquals("user", user.getRole());
    }

    @Test
    void deleteUserById() {
        assertEquals(1, usersDAO.deleteUserById(SteveId));
    }

    @Test
    void insertUser() {
        assertEquals(SteveId+1, usersDAO.insertUser("Jake", "wertyu", "array", "admin"));
    }

    @Test
    void updateFactoryType() {
        assertEquals(1, usersDAO.updateFactoryType("linkedList", TomId));
        User user = usersDAO.getUserById(TomId);
        assertEquals("linkedList", user.getFactory_type());
    }

    @Test
    void updatePassword() {
        assertEquals(1, usersDAO.updatePassword("123321", "54321", TomId));
        assertTrue(SqlHelper.checkPassword("54321", usersDAO.getPasswordHashById(TomId)));
    }

    @Test
    void updateUsername() {
        assertEquals(1, usersDAO.updateUsername("John", TomId));
        assertEquals("John", usersDAO.getUsernameById(TomId));
    }

    @Test
    void selectAllUsers() {
        List<User> users = usersDAO.selectAllUsers();
        assertEquals("Tom", users.get(0).getUsername());
        assertEquals("Bob", users.get(1).getUsername());
        assertEquals("Steve", users.get(2).getUsername());
    }
}