package ru.ssau.tk.faible.labs.performance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;
import ru.ssau.tk.faible.labs.config.DatabaseConfig;
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


    private static final int TOTAL_RECORDS = 10000;
    private static final int BATCH_SIZE = 1000;
    private final Random random = new Random();

    public void runPerformanceBenchmark() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(
                        DatabaseConfig.class,
                        ExcelWriter.class
                );
        UserRepository userRepository = context.getBean(UserRepository.class);
        ExcelWriter excelWriter = context.getBean(ExcelWriter.class);

        List<BenchmarkResult> results = new ArrayList<>();
        BenchUsers(userRepository, results, context);
        User testUser = createTestUser();
        FunctionEntity testFunction = createTestFunction(testUser);

        benchPoints(pointRepository, testFunction, results, context);
        testFindPointsByFunction(pointRepository, results, testFunction);

        benchFunctions(functionRepository, testUser, results, context);

        String filename = "output/benchmark_results.xlsx";
        excelWriter.saveToExcel(results, filename);

    }

    private void BenchUsers(UserRepository userRepository, List<BenchmarkResult> results, AnnotationConfigApplicationContext context) {
        try {

            userRepository.deleteAll();

            insertInUsersTable(userRepository, results);

            deleteUserById(userRepository, results);
            updateUsernameById(userRepository, results);

        } finally {
            userRepository.deleteAll();
            context.close();
        }
    }
    private void benchFunctions(FunctionRepository functionRepository,
                                User testUser,
                                List<BenchmarkResult> results,
                                AnnotationConfigApplicationContext context) {
        try {
            functionRepository.deleteAll();

            insertInFunctionsTable(functionRepository, testUser, results);

            testFindFunctionById(functionRepository, results);

        } finally {

            functionRepository.deleteAll();
            context.close();
        }
    }
    private void benchPoints(PointRepository pointRepository,
                              FunctionEntity testFunction,
                              List<BenchmarkResult> results,
                              AnnotationConfigApplicationContext context) {
        try {
            pointRepository.deleteAll();

            insertInPointsTable(pointRepository, testFunction.getId(), results);
            testFindPointById(pointRepository, results);



        } finally {

            pointRepository.deleteAll();
            context.close();
        }
    }
    private void insertInUsersTable(UserRepository repository, List<BenchmarkResult> results) {

        long startTime = System.currentTimeMillis();

        List<User> users = generateTestUsers(TOTAL_RECORDS);
        repository.saveAll(users);
        repository.flush();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        results.add(new BenchmarkResult(duration, "insertInUsersTable", TOTAL_RECORDS));
    }


    private FunctionEntity createTestFunction(User owner) {
        FunctionEntity function = new FunctionEntity("benchmark_function", "ARRAY", owner);
        return functionRepository.save(function);
    }



    private void deleteUserById(UserRepository repository, List<BenchmarkResult> results) {
        int usersToDeleteCount = 1;

        List<User> allUsers = repository.findAll();
        if (allUsers.size() < usersToDeleteCount) {
            return;
        }

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
        if (allUsers.size() < usersToUpdateCount) {
            return;
        }

        List<User> usersToUpdate = allUsers.subList(0, usersToUpdateCount);

        usersToUpdate.forEach(user ->
                user.setUsername("updated_" + user.getUsername())
        );

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
            String password = "12345" + i;
            String factoryType = "array";
            String role = "user";

            User user = new User(username, password, factoryType, role);
            users.add(user);
        }
        return users;
    }
    private void testFindFunctionById(FunctionRepository repository, List<BenchmarkResult> results) {
        int functionsToFindCount = 1;

        List<FunctionEntity> allFunctions = repository.findAll();
        if (allFunctions.size() < functionsToFindCount) {
            System.out.println("Not enough functions for FIND BY ID test");
            return;
        }

        Long functionId = allFunctions.get(0).getId();

        long startTime = System.currentTimeMillis();
        FunctionEntity foundFunction = repository.findById(functionId)
                .orElse(null);
        long endTime = System.currentTimeMillis();

        results.add(new BenchmarkResult(endTime - startTime, "findFunctionById", functionsToFindCount));
    }

    private List<FunctionEntity> generateFunctions(int recordsCount, Long userId) {
        List<FunctionEntity> functions = new ArrayList<>();

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        for (int i = 1; i < recordsCount; i++) {
            String name = "function_" + i;
            String type = "array";

            FunctionEntity function = new FunctionEntity(name, type, owner);
            functions.add(function);
        }
        return functions;
    }

    private List<PointEntity> generatePoints(int recordsCount, Long functionId, Double xValue, Double yValue) {
        List<PointEntity> points = new ArrayList<>();

        FunctionEntity function = functionRepository.findById(functionId)
                .orElseThrow(() -> new RuntimeException("Function not found with id: " + functionId));

        for (int i = 0; i < recordsCount; i++) {
            PointEntity point = new PointEntity(xValue, yValue, function);
            points.add(point);
        }
        return points;
    }


    private void insertInPointsTable(PointRepository repository, Long functionId, List<BenchmarkResult> results) {
        long startTime = System.currentTimeMillis();

        List<PointEntity> points1 = generatePoints(TOTAL_RECORDS, functionId, 1.0, 1.0);

        List<PointEntity> allPoints = new ArrayList<>();
        allPoints.addAll(points1);
        repository.saveAll(allPoints);
        repository.flush();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        results.add(new BenchmarkResult(duration, "insertInPointsTable", allPoints.size()));
    }

    private User createTestUser() {
        User testUser = new User("function_benchmark_user", "test123", "ARRAY", "user");
        return userRepository.save(testUser);
    }
    private void testFindPointsByFunction(PointRepository pointRepository, List<BenchmarkResult> results, FunctionEntity function) {
        int pointsToFindCount = 10000;

        long startTime = System.currentTimeMillis();
        List<PointEntity> pointsByFunction = pointRepository.findByFunction(function);
        long endTime = System.currentTimeMillis();
        int actualCount = Math.min(pointsToFindCount, pointsByFunction.size());

        results.add(new BenchmarkResult(endTime - startTime, "findPointsByFunction", actualCount));
    }

    private void insertInFunctionsTable(FunctionRepository repository, User owner, List<BenchmarkResult> results) {
        long startTime = System.currentTimeMillis();

        List<FunctionEntity> functions = generateTestFunctions(TOTAL_RECORDS, owner);
        repository.saveAll(functions);
        repository.flush();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        results.add(new BenchmarkResult(duration, "InsertInFunctionsTable", TOTAL_RECORDS));
    }


    private List<FunctionEntity> generateTestFunctions(int recordsCount, User owner) {
        List<FunctionEntity> functions = new ArrayList<>();
        String[] types = {"ARRAY", "LINKED_LIST", "TABULATED"};

        for (int i = 1; i <= recordsCount; i++) {
            String name = "function_" + i;
            String type = types[i % types.length];

            FunctionEntity function = new FunctionEntity(name, type, owner);
            functions.add(function);
        }
        return functions;
    }
    private void testFindPointById(PointRepository repository, List<BenchmarkResult> results) {
        int pointsToFindCount = 1;

        List<PointEntity> allPoints = repository.findAll();
        if (allPoints.size() < pointsToFindCount) {
            System.out.println("Not enough points for FIND BY ID test");
            return;
        }

        Long pointId = allPoints.get(0).getId();

        long startTime = System.currentTimeMillis();
        PointEntity foundPoint = repository.findById(pointId)
                .orElse(null);
        long endTime = System.currentTimeMillis();

        results.add(new BenchmarkResult(endTime - startTime, "findPointById", pointsToFindCount));
    }
}



