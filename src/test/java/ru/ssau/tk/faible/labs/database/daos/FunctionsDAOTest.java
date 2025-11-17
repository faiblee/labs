package ru.ssau.tk.faible.labs.database.daos;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk.faible.labs.database.models.Function;
import ru.ssau.tk.faible.labs.database.models.User;
import ru.ssau.tk.faible.labs.database.utils.DBConnector;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FunctionsDAOTest {
    private FunctionsDAO functionsDAO;
    List<Integer> ids = new LinkedList<>();
    int firstFunctionId;
    int secondFunctionId;
    int thirdFunctionId;
    @BeforeEach
    void setUp() {
        functionsDAO = new FunctionsDAO(DBConnector.initConnect());
        functionsDAO.deleteAllFunctions();
        UsersDAO usersDAO = new UsersDAO(DBConnector.initConnect());
        List<User> users = usersDAO.selectAllUsers();
        ids.add(users.get(0).getId());
        ids.add(users.get(1).getId());
        ids.add(users.get(2).getId());
        firstFunctionId = functionsDAO.insertFunction("y=1", ids.get(0), "constant");
        secondFunctionId = functionsDAO.insertFunction("y=x+1", ids.get(1), "tabulated");
        thirdFunctionId = functionsDAO.insertFunction("y=x^2", ids.get(2), "sqr");
    }

    @AfterEach
    void close() {
        DBConnector.closeConnection(functionsDAO.getConnection());
    }

    @Test
    void insertFunction() {
        int functionId = functionsDAO.insertFunction("y=2", ids.getFirst(), "constant");
        Function function = functionsDAO.getFunctionById(functionId);
        assertEquals("y=2", function.getName());
    }

    @Test
    void getFunctionById() {
        Function function = functionsDAO.getFunctionById(firstFunctionId);
        assertEquals(firstFunctionId, function.getId());
        assertEquals("y=1", function.getName());
        assertEquals(ids.getFirst(), function.getOwner_id());
    }

    @Test
    void deleteFunctionById() {
        assertEquals(1, functionsDAO.deleteFunctionById(firstFunctionId));
    }

    @Test
    void updateName() {
        functionsDAO.updateName("y=3", firstFunctionId);
        Function function = functionsDAO.getFunctionById(firstFunctionId);
        assertEquals(ids.getFirst(), function.getOwner_id());
        assertEquals("constant", function.getType());
    }

    @Test
    void updateType() {
        functionsDAO.updateName("y=x+3", firstFunctionId);
        functionsDAO.updateType("tabulated", firstFunctionId);
        Function function = functionsDAO.getFunctionById(firstFunctionId);
        assertEquals(ids.getFirst(), function.getOwner_id());
        assertEquals("tabulated", function.getType());
    }

    @Test
    void getAllFunctions() {
        List<Function> functions = functionsDAO.getAllFunctions();
        assertEquals(firstFunctionId, functions.getFirst().getId());
        assertEquals(secondFunctionId, functions.get(1).getId());
        assertEquals(thirdFunctionId, functions.get(2).getId());
    }
}