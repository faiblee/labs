package ru.ssau.tk.faible.labs.performance;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.ssau.tk.faible.labs.config.DatabaseConfig;
import ru.ssau.tk.faible.labs.entity.User;
import ru.ssau.tk.faible.labs.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BenchmarkService {

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

        try {
            // Очищаем базу
            userRepository.deleteAll();

            // Заполняем данными
            populateTestData(userRepository, results);

            // Тестируем только нужные запросы
            testSelectLimit(userRepository, results);
            testWhereFactoryType(userRepository, results);
            testDeleteRecords(userRepository, results);

        } finally {
            // Очищаем тестовые данные
            userRepository.deleteAll();
            context.close();
        }

        // Сохраняем результаты в Excel
        String filename = "output/benchmark_results.xlsx";
        excelWriter.saveToExcel(results, filename);

    }

    private void populateTestData(UserRepository repository, List<BenchmarkResult> results) {

        long startTime = System.currentTimeMillis();

        List<User> users = generateTestUsers(TOTAL_RECORDS);
        repository.saveAll(users);
        repository.flush();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        results.add(new BenchmarkResult(duration, "Insert in UsersTable", TOTAL_RECORDS));
    }

    private void testSelectLimit(UserRepository repository, List<BenchmarkResult> results) {

        long startTime = System.currentTimeMillis();
        List<User> first100 = repository.findAll().subList(0, Math.min(100, TOTAL_RECORDS));
        long endTime = System.currentTimeMillis();

        results.add(new BenchmarkResult(endTime - startTime, "SELECT LIMIT 100", first100.size()));
    }

    private void testWhereFactoryType(UserRepository repository, List<BenchmarkResult> results) {

        long startTime = System.currentTimeMillis();
        List<User> arrayUsers = repository.findByFactoryTypeOrderByIdAsc("ARRAY");
        long endTime = System.currentTimeMillis();

        results.add(new BenchmarkResult(endTime - startTime, "WHERE factory_type='ARRAY'", arrayUsers.size()));
    }

    private void testDeleteRecords(UserRepository repository, List<BenchmarkResult> results) {

        List<User> allUsers = repository.findAll();
        if (allUsers.size() < 100) {
            System.out.println("⚠️ Not enough records for DELETE test");
            return;
        }

        List<User> usersToDelete = allUsers.subList(0, 100);

        long startTime = System.currentTimeMillis();
        repository.deleteAll(usersToDelete);
        repository.flush();
        long endTime = System.currentTimeMillis();

        results.add(new BenchmarkResult(endTime - startTime, "DELETE 100 records", 100));
    }

    private List<User> generateTestUsers(int count) {
        List<User> users = new ArrayList<>();
        String[] roles = {"USER", "ADMIN"};
        String[] factoryTypes = {"ARRAY", "LINKED_LIST"};

        for (int i = 0; i < count; i++) {
            User user = new User(
                    "user_" + i,
                    "pass_" + i,
                    factoryTypes[random.nextInt(factoryTypes.length)],
                    roles[random.nextInt(roles.length)]
            );
            users.add(user);
        }
        return users;
    }


}