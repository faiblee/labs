package ru.ssau.tk.faible.labs.database.benchmark;

import lombok.Getter;
import ru.ssau.tk.faible.labs.database.daos.FunctionsDAO;
import ru.ssau.tk.faible.labs.database.daos.PointsDAO;
import ru.ssau.tk.faible.labs.database.daos.UsersDAO;
import ru.ssau.tk.faible.labs.database.models.Function;
import ru.ssau.tk.faible.labs.database.models.Point;
import ru.ssau.tk.faible.labs.database.models.User;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

@Getter
public class QueryBenchmark {
    private final Connection connection;
    private List<BenchmarkResult> results;

    public QueryBenchmark(Connection connection) {
        this.connection = connection;
        results = new LinkedList<>();
    }

    // вставляет определенное число User в таблицу Users
    public int insertInUsersTable(int records_count) {
        long startTime = System.currentTimeMillis();
        int user_id = 0;
        UsersDAO usersDAO = new UsersDAO(connection);
        for (int i = 1; i < records_count; i++) {
            String username = "user" + i;
            String password = "12345" + i;
            String factory_type = "array";
            String role = "user";
            user_id = usersDAO.insertUser(username, password, factory_type, role);
        }
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        BenchmarkResult result = new BenchmarkResult(duration, "insertInUserTable", records_count);
        results.add(result);
        return user_id;
    }

    public int insertInFunctionsTable(int records_count, int user_id) {
        long startTime = System.currentTimeMillis();
        List<Integer> function_ids = new LinkedList<>();
        FunctionsDAO functionsDAO = new FunctionsDAO(connection);
        for (int i = 1; i < records_count; i++) {
            String name = "function_" + i;
            String type = "array";
            function_ids.add(functionsDAO.insertFunction(name, user_id, type));
        }
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        BenchmarkResult result = new BenchmarkResult(duration, "insertInFunctionsTable", records_count);
        results.add(result);
        return function_ids.getFirst();
    }

    public BenchmarkResult findUserById(int id) {
        long startTime = System.currentTimeMillis();
        UsersDAO usersDAO = new UsersDAO(connection);
        User user = usersDAO.getUserById(id);
        long entTime = System.currentTimeMillis();

        long duration = entTime - startTime;
        BenchmarkResult result = new BenchmarkResult(duration, "findUserById", 1);
        results.add(result);
        return result;
    }

    public BenchmarkResult deleteUserById(int id) {
        long startTime = System.currentTimeMillis();
        UsersDAO usersDAO = new UsersDAO(connection);
        usersDAO.deleteUserById(id);
        long entTime = System.currentTimeMillis();

        long duration = entTime - startTime;
        BenchmarkResult result = new BenchmarkResult(duration, "deleteUserById", 1);
        results.add(result);
        return result;
    }

    public BenchmarkResult updateUserUsernameById(String username, int id) {
        long startTime = System.currentTimeMillis();
        UsersDAO usersDAO = new UsersDAO(connection);
        usersDAO.updateUsername(username, id);
        long entTime = System.currentTimeMillis();

        long duration = entTime - startTime;
        BenchmarkResult result = new BenchmarkResult(duration, "updateUsernameById", 1);
        results.add(result);
        return result;
    }

    public BenchmarkResult findFunctionsByUserId(int user_id) {
        long startTime = System.currentTimeMillis();
        UsersDAO usersDAO = new UsersDAO(connection);
        List<Function> functions = usersDAO.getAllFunctionsById(user_id);
        long entTime = System.currentTimeMillis();

        long duration = entTime - startTime;
        BenchmarkResult result = new BenchmarkResult(duration, "findFunctionsByUserId", functions.size());
        results.add(result);
        return result;
    }

    public BenchmarkResult findPointsByFunctionId(int function_id) {
        long startTime = System.currentTimeMillis();
        FunctionsDAO functionsDAO = new FunctionsDAO(connection);
        List<Point> points = functionsDAO.getPointsById(function_id);
        long entTime = System.currentTimeMillis();

        long duration = entTime - startTime;
        BenchmarkResult result = new BenchmarkResult(duration, "findPointsByFunctionId", points.size());
        results.add(result);
        return result;
    }

    public int insertInPointsTable(int records_count, int function_id, double x_value, double y_value) {
        long startTime = System.currentTimeMillis();
        int point_id = 0;
        PointsDAO pointsDAO = new PointsDAO(connection);
        for (int i = 1; i < records_count; i++) {
            point_id = pointsDAO.insertPoint(x_value, y_value, function_id);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        BenchmarkResult result = new BenchmarkResult(duration, "insertInPointsTable", records_count);
        results.add(result);
        return point_id;
    }

    public BenchmarkResult findPointById(int id) {
        long startTime = System.currentTimeMillis();
        PointsDAO pointsDAO = new PointsDAO(connection);
        Point point = pointsDAO.getPointById(id);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        BenchmarkResult result = new BenchmarkResult(duration, "findUserById", 1);
        results.add(result);
        return result;
    }

    public void saveResultsToExcel() {
        ExcelWriter.saveToExcel(results, "benchmark_results.xlsx");
    }
}
