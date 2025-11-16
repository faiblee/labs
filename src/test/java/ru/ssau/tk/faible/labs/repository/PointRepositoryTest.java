package ru.ssau.tk.faible.labs.repository;

import ru.ssau.tk.faible.labs.entity.FunctionEntity;
import ru.ssau.tk.faible.labs.entity.PointEntity;
import ru.ssau.tk.faible.labs.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PointRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private FunctionRepository functionRepository;

    private FunctionEntity testFunction;

    @BeforeEach
    void setUp() {
        // Генерация тестовых данных
        User user = new User("testuser", "password", "ARRAY", "USER");
        entityManager.persist(user);

        testFunction = new FunctionEntity("Test Function", "ARRAY", user);
        entityManager.persist(testFunction);
        entityManager.flush();
    }

    @Test
    void testSavePoint() {
        // Генерация точки
        PointEntity point = new PointEntity(1.0, 2.0, testFunction);

        // Сохранение
        PointEntity savedPoint = pointRepository.save(point);

        // Проверка
        assertNotNull(savedPoint.getId());
        assertEquals(1.0, savedPoint.getXValue());
        assertEquals(2.0, savedPoint.getYValue());
        assertEquals(testFunction.getId(), savedPoint.getFunction().getId());
    }

    @Test
    void testFindByFunction() {
        // Генерация точек
        PointEntity point1 = new PointEntity(1.0, 1.0, testFunction);
        PointEntity point2 = new PointEntity(2.0, 4.0, testFunction);

        entityManager.persist(point1);
        entityManager.persist(point2);
        entityManager.flush();

        // Поиск точек функции
        List<PointEntity> points = pointRepository.findByFunction(testFunction);

        // Проверка
        assertEquals(2, points.size());
        assertTrue(points.stream().anyMatch(p -> p.getXValue().equals(1.0)));
        assertTrue(points.stream().anyMatch(p -> p.getXValue().equals(2.0)));
    }

    @Test
    void testFindByFunctionId() {
        // Генерация точек
        PointEntity point = new PointEntity(5.0, 25.0, testFunction);
        entityManager.persist(point);
        entityManager.flush();

        // Поиск по ID функции
        List<PointEntity> points = pointRepository.findByFunctionId(testFunction.getId());

        // Проверка
        assertEquals(1, points.size());
        assertEquals(5.0, points.get(0).getXValue());
    }

    @Test
    void testFindByXValueBetween() {
        // Генерация точек в разных диапазонах
        PointEntity point1 = new PointEntity(1.0, 1.0, testFunction);
        PointEntity point2 = new PointEntity(5.0, 25.0, testFunction);
        PointEntity point3 = new PointEntity(10.0, 100.0, testFunction);

        entityManager.persist(point1);
        entityManager.persist(point2);
        entityManager.persist(point3);
        entityManager.flush();

        // Поиск по диапазону X
        List<PointEntity> pointsInRange = pointRepository.findByXValueBetween(2.0, 8.0);

        // Проверка
        assertEquals(1, pointsInRange.size());
        assertEquals(5.0, pointsInRange.get(0).getXValue());
    }

    @Test
    void testDeleteByFunction() {
        // Генерация точек
        PointEntity point1 = new PointEntity(1.0, 1.0, testFunction);
        PointEntity point2 = new PointEntity(2.0, 4.0, testFunction);

        entityManager.persist(point1);
        entityManager.persist(point2);
        entityManager.flush();

        // Удаление всех точек функции
        pointRepository.deleteByFunction(testFunction);

        // Проверка
        List<PointEntity> remainingPoints = pointRepository.findByFunction(testFunction);
        assertTrue(remainingPoints.isEmpty());
    }

    @Test
    void testFindAllPoints() {
        // Генерация точек
        PointEntity point1 = new PointEntity(1.0, 1.0, testFunction);
        PointEntity point2 = new PointEntity(2.0, 4.0, testFunction);

        entityManager.persist(point1);
        entityManager.persist(point2);
        entityManager.flush();

        // Поиск всех точек
        List<PointEntity> allPoints = pointRepository.findAll();

        // Проверка
        assertEquals(2, allPoints.size());
    }
}