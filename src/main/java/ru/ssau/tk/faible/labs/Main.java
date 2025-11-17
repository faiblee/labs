package ru.ssau.tk.faible.labs;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.ssau.tk.faible.labs.config.DatabaseConfig;
import ru.ssau.tk.faible.labs.entity.FunctionEntity;
import ru.ssau.tk.faible.labs.entity.PointEntity;
import ru.ssau.tk.faible.labs.entity.User;
import ru.ssau.tk.faible.labs.repository.FunctionRepository;
import ru.ssau.tk.faible.labs.repository.PointRepository;
import ru.ssau.tk.faible.labs.repository.UserRepository;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {


        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(DatabaseConfig.class);

        UserRepository userRepository = context.getBean(UserRepository.class);
        FunctionRepository functionRepository = context.getBean(FunctionRepository.class);
        PointRepository pointRepository = context.getBean(PointRepository.class);

        userRepository.deleteAll();
        functionRepository.deleteAll();
        pointRepository.deleteAll();

        // Создаем тестовые данные для всех таблиц
        createTestData(userRepository, functionRepository, pointRepository);

        // Проверяем что данные создались
        checkData(userRepository, functionRepository, pointRepository);
        userRepository.deleteAll();
        functionRepository.deleteAll();
        pointRepository.deleteAll();



        context.close();
    }

    private static void createTestData(UserRepository userRepo,
                                       FunctionRepository functionRepo,
                                       PointRepository pointRepo) {


        // Создаем пользователя
        User user = new User("test_user", "password_hash", "ARRAY", "USER");
        User savedUser = userRepo.save(user);

        // Создаем функцию
        FunctionEntity function = new FunctionEntity("Test Function", "ARRAY", savedUser);
        FunctionEntity savedFunction = functionRepo.save(function);

        // Создаем точки для функции
        PointEntity point1 = new PointEntity(1.0, 2.0, savedFunction);
        PointEntity point2 = new PointEntity(3.0, 4.0, savedFunction);
        PointEntity point3 = new PointEntity(5.0, 6.0, savedFunction);

        pointRepo.saveAll(Arrays.asList(point1, point2, point3));

        // Связываем точки с функцией
        savedFunction.addPoint(point1);
        savedFunction.addPoint(point2);
        savedFunction.addPoint(point3);
        functionRepo.save(savedFunction);


    }

    private static void checkData(UserRepository userRepo,
                                  FunctionRepository functionRepo,
                                  PointRepository pointRepo) {

        // Проверяем пользователей
        long userCount = userRepo.count();

        // Проверяем функции
        long functionCount = functionRepo.count();

        // Проверяем точки
        long pointCount = pointRepo.count();


    }
}