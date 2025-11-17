package ru.ssau.tk.faible.labs.database.benchmark;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk.faible.labs.database.daos.FunctionsDAO;
import ru.ssau.tk.faible.labs.database.daos.PointsDAO;
import ru.ssau.tk.faible.labs.database.daos.UsersDAO;
import ru.ssau.tk.faible.labs.database.utils.DBConnector;

class QueryBenchmarkTest {

    QueryBenchmark queryBenchmark;
    UsersDAO usersDAO;
    FunctionsDAO functionsDAO;
    PointsDAO pointsDAO;

    @BeforeEach
    void setUp() {
        queryBenchmark = new QueryBenchmark(DBConnector.initConnect());
        usersDAO = new UsersDAO(queryBenchmark.getConnection());
        functionsDAO = new FunctionsDAO(queryBenchmark.getConnection());
        pointsDAO = new PointsDAO(queryBenchmark.getConnection());

    }

    @AfterEach
    void close() {
        DBConnector.closeConnection(queryBenchmark.getConnection());
    }

    @Test
    void writeResults() {
        pointsDAO.deleteAllPoints();
        functionsDAO.deleteAllFunctions();
        usersDAO.deleteAllUsers();
        int user_id = queryBenchmark.insertInUsersTable(10000);
        int function_id = queryBenchmark.insertInFunctionsTable(10000, user_id);
        int point_id = queryBenchmark.insertInPointsTable(10000, function_id, 1.0, 1.0);
        queryBenchmark.findUserById(user_id);
        queryBenchmark.findFunctionsByUserId(user_id);
        queryBenchmark.updateUserUsernameById("new_user", user_id);
        queryBenchmark.findPointsByFunctionId(function_id);
        queryBenchmark.findPointById(point_id);
        queryBenchmark.deleteUserById(user_id-1);
        queryBenchmark.saveResultsToExcel();
    }

}