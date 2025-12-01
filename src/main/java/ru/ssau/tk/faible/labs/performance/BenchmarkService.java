package ru.ssau.tk.faible.labs.performance;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ssau.tk.faible.labs.entity.FunctionEntity;
import ru.ssau.tk.faible.labs.entity.PointEntity;
import ru.ssau.tk.faible.labs.entity.User;
import ru.ssau.tk.faible.labs.repository.FunctionRepository;
import ru.ssau.tk.faible.labs.repository.PointRepository;
import ru.ssau.tk.faible.labs.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class BenchmarkService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private ExcelWriter excelWriter;

    private static final int TOTAL_RECORDS = 10000;
    private final Random random = new Random();

    public void runPerformanceBenchmark() {
        List<BenchmarkResult> results = new ArrayList<>();

        try {
            // Пользователи
            userRepository.deleteAll();
            insertInUsersTable(userRepository, results);
            deleteUserById(userRepository, results);
            updateUsernameById(userRepository, results);

            // Тестовые данные
            User testUser = createTestUser();
            FunctionEntity testFunction = createTestFunction(testUser);

            // Точки
            pointRepository.deleteAll();
            insertInPointsTable(pointRepository, testFunction.getId(), results);
            testFindPointById(pointRepository, results);
            testFindPointsByFunction(pointRepository, results, testFunction);

            // Функции
            functionRepository.deleteAll();
            insertInFunctionsTable(functionRepository, testUser, results);
            testFindFunctionById(functionRepository, results);

        } finally {
            // Очистка после тестов
            pointRepository.deleteAll();
            functionRepository.deleteAll();
            userRepository.deleteAll();
        }

        // Сохранение результатов
        String filename = "output/benchmark_results.xlsx";
        excelWriter.saveToExcel(results, filename);
    }

    // --- Пользователи ---
    private void insertInUsersTable(UserRepository repository, List<BenchmarkResult> results) {
        long startTime = System.currentTimeMillis();
        List<User> users = generateTestUsers(TOTAL_RECORDS);
        repository.saveAll(users);
        repository.flush();
        long endTime = System.currentTimeMillis();
        results.add(new BenchmarkResult(endTime - startTime, "insertInUsersTable", TOTAL_RECORDS));
    }

    private void deleteUserById(UserRepository repository, List<BenchmarkResult> results) {
        int usersToDeleteCount = 1;
        List<User> allUsers = repository.findAll();
        if (allUsers.size() < usersToDeleteCount) return;

        List<User> usersToDelete = allUsers.subList(0, usersToDeleteCount);
        long startTime = System.currentTimeMillis();
        repository.deleteAll(usersToDelete);
        repository.flush();
        long endTime = System.currentTimeMillis();
        results.add(new BenchmarkResult(endTime - startTime, "deleteUserById", usersToDeleteCount));
    }

    private void updateUsernameById(UserRepository repository, List<BenchmarkResult> results) {
        int usersToUpdateCount = 1;
        List<User> allUsers = repository.findAll();
        if (allUsers.size() < usersToUpdateCount) return;

        List<User> usersToUpdate = allUsers.subList(0, usersToUpdateCount);
        usersToUpdate.forEach(user -> user.setUsername("updated_" + user.getUsername()));

        long startTime = System.currentTimeMillis();
        repository.saveAll(usersToUpdate);
        repository.flush();
        long endTime = System.currentTimeMillis();
        results.add(new BenchmarkResult(endTime - startTime, "updateUsernameById", usersToUpdateCount));
    }

    private List<User> generateTestUsers(int recordsCount) {
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= recordsCount; i++) {
            String username = "user" + i;
            String password = BCrypt.hashpw("12345" + i, BCrypt.gensalt());
            users.add(new User(username, password, "array", "user"));
        }
        return users;
    }

    private User createTestUser() {
        String password = BCrypt.hashpw("test123", BCrypt.gensalt());
        User user = new User("function_benchmark_user", password, "ARRAY", "user");
        return userRepository.save(user);
    }

    // --- Функции ---
    private void insertInFunctionsTable(FunctionRepository repository, User owner, List<BenchmarkResult> results) {
        long startTime = System.currentTimeMillis();
        List<FunctionEntity> functions = generateTestFunctions(TOTAL_RECORDS, owner);
        repository.saveAll(functions);
        repository.flush();
        long endTime = System.currentTimeMillis();
        results.add(new BenchmarkResult(endTime - startTime, "InsertInFunctionsTable", TOTAL_RECORDS));
    }

    private List<FunctionEntity> generateTestFunctions(int recordsCount, User owner) {
        List<FunctionEntity> functions = new ArrayList<>();
        String[] types = {"ARRAY", "LINKED_LIST", "TABULATED"};
        for (int i = 1; i <= recordsCount; i++) {
            String name = "function_" + i;
            String type = types[i % types.length];
            functions.add(new FunctionEntity(name, type, owner));
        }
        return functions;
    }

    private void testFindFunctionById(FunctionRepository repository, List<BenchmarkResult> results) {
        List<FunctionEntity> allFunctions = repository.findAll();
        if (allFunctions.isEmpty()) return;

        Long functionId = allFunctions.get(0).getId();
        long startTime = System.currentTimeMillis();
        repository.findById(functionId).orElse(null);
        long endTime = System.currentTimeMillis();
        results.add(new BenchmarkResult(endTime - startTime, "findFunctionById", 1));
    }

    private FunctionEntity createTestFunction(User owner) {
        FunctionEntity function = new FunctionEntity("benchmark_function", "ARRAY", owner);
        return functionRepository.save(function);
    }

    // --- Точки ---
    private void insertInPointsTable(PointRepository repository, Long functionId, List<BenchmarkResult> results) {
        long startTime = System.currentTimeMillis();
        List<PointEntity> points = generatePoints(TOTAL_RECORDS, functionId, 1.0, 1.0);
        repository.saveAll(points);
        repository.flush();
        long endTime = System.currentTimeMillis();
        results.add(new BenchmarkResult(endTime - startTime, "insertInPointsTable", points.size()));
    }

    private List<PointEntity> generatePoints(int recordsCount, Long functionId, Double xValue, Double yValue) {
        FunctionEntity function = functionRepository.findById(functionId)
                .orElseThrow(() -> new RuntimeException("Function not found with id: " + functionId));
        List<PointEntity> points = new ArrayList<>();
        for (int i = 0; i < recordsCount; i++) {
            points.add(new PointEntity(xValue, yValue, function));
        }
        return points;
    }

    private void testFindPointById(PointRepository repository, List<BenchmarkResult> results) {
        List<PointEntity> allPoints = repository.findAll();
        if (allPoints.isEmpty()) return;

        Long pointId = allPoints.get(0).getId();
        long startTime = System.currentTimeMillis();
        repository.findById(pointId).orElse(null);
        long endTime = System.currentTimeMillis();
        results.add(new BenchmarkResult(endTime - startTime, "findPointById", 1));
    }

    private void testFindPointsByFunction(PointRepository repository, List<BenchmarkResult> results, FunctionEntity function) {
        long startTime = System.currentTimeMillis();
        List<PointEntity> points = repository.findByFunction(function);
        long endTime = System.currentTimeMillis();
        results.add(new BenchmarkResult(endTime - startTime, "findPointsByFunction", points.size()));
    }
}