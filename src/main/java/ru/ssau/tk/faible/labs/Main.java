package ru.ssau.tk.faible.labs;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import ru.ssau.tk.faible.labs.entity.FunctionEntity;
import ru.ssau.tk.faible.labs.entity.PointEntity;
import ru.ssau.tk.faible.labs.entity.User;
import ru.ssau.tk.faible.labs.repository.FunctionRepository;
import ru.ssau.tk.faible.labs.repository.PointRepository;
import ru.ssau.tk.faible.labs.repository.UserRepository;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        // Запускаем Spring Boot-контекст без веб-сервера
        ConfigurableApplicationContext context = new SpringApplicationBuilder()
                .sources(Application.class) // ваш главный класс
                .web(WebApplicationType.NONE)
                .run();

        // Получаем бины через контекс
        UserRepository userRepository = context.getBean(UserRepository.class);
        FunctionRepository functionRepository = context.getBean(FunctionRepository.class);
        PointRepository pointRepository = context.getBean(PointRepository.class);

        // Очистка
        pointRepository.deleteAll();
        functionRepository.deleteAll();
        userRepository.deleteAll();

        // Создание тестовых данных
        createTestData(userRepository, functionRepository, pointRepository);

        // Проверка
        checkData(userRepository, functionRepository, pointRepository);

        // Повторная очистка
        pointRepository.deleteAll();
        functionRepository.deleteAll();
        userRepository.deleteAll();

        context.close();
    }

    private static void createTestData(UserRepository userRepo,
                                       FunctionRepository functionRepo,
                                       PointRepository pointRepo) {

        // Хэшируем пароль (как в AuthController)
        String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw("password", org.mindrot.jbcrypt.BCrypt.gensalt());
        User user = new User("test_user", hashedPassword, "ARRAY", "USER");
        User savedUser = userRepo.save(user);

        FunctionEntity function = new FunctionEntity("Test Function", "ARRAY", savedUser);
        FunctionEntity savedFunction = functionRepo.save(function);

        PointEntity point1 = new PointEntity(1.0, 2.0, savedFunction);
        PointEntity point2 = new PointEntity(3.0, 4.0, savedFunction);
        PointEntity point3 = new PointEntity(5.0, 6.0, savedFunction);

        pointRepo.saveAll(Arrays.asList(point1, point2, point3));

        // Связывание не обязательно — Hibernate сам управляет через mappedBy
        // savedFunction.getPoints().addAll(...);
    }

    private static void checkData(UserRepository userRepo,
                                  FunctionRepository functionRepo,
                                  PointRepository pointRepo) {

        long userCount = userRepo.count();
        long functionCount = functionRepo.count();
        long pointCount = pointRepo.count();

        System.out.println("Проверка данных:");
        System.out.println("Пользователей: " + userCount);
        System.out.println("Функций: " + functionCount);
        System.out.println("Точек: " + pointCount);
    }
}