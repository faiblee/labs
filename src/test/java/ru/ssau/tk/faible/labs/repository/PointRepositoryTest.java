package ru.ssau.tk.faible.labs.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.faible.labs.config.DatabaseConfig;
import ru.ssau.tk.faible.labs.entity.FunctionEntity;
import ru.ssau.tk.faible.labs.entity.PointEntity;
import ru.ssau.tk.faible.labs.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(DatabaseConfig.class)
@Transactional
class PointRepositoryTest {

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private FunctionEntity testFunction;
    private PointEntity testPoint1;
    private PointEntity testPoint2;
    private PointEntity testPoint3;

    @BeforeEach
    void setUp() {
        testUser = new User("point_owner", "password", "ARRAY", "USER");
        userRepository.save(testUser);

        testFunction = new FunctionEntity("Test Function", "ARRAY", testUser);
        functionRepository.save(testFunction);

        testPoint1 = new PointEntity(1.0, 1.0, testFunction);
        testPoint2 = new PointEntity(5.0, 25.0, testFunction);
        testPoint3 = new PointEntity(10.0, 100.0, testFunction);

        pointRepository.save(testPoint1);
        pointRepository.save(testPoint2);
        pointRepository.save(testPoint3);
    }

    @Test
    void testSavePoint() {
        PointEntity newPoint = new PointEntity(15.0, 225.0, testFunction);
        PointEntity savedPoint = pointRepository.save(newPoint);

        assertNotNull(savedPoint.getId());
        assertEquals(15.0, savedPoint.getXValue());
        assertEquals(225.0, savedPoint.getYValue());
        assertEquals(testFunction.getId(), savedPoint.getFunction().getId());
    }

    @Test
    void testFindByFunction() {
        List<PointEntity> functionPoints = pointRepository.findByFunction(testFunction);
        assertEquals(3, functionPoints.size());
        assertTrue(functionPoints.stream().anyMatch(p -> p.getXValue().equals(1.0)));
        assertTrue(functionPoints.stream().anyMatch(p -> p.getXValue().equals(5.0)));
        assertTrue(functionPoints.stream().anyMatch(p -> p.getXValue().equals(10.0)));
    }

    @Test
    void testFindByFunctionId() {
        List<PointEntity> points = pointRepository.findByFunctionId(testFunction.getId());
        assertEquals(3, points.size());
        assertTrue(points.stream().allMatch(p -> p.getFunction().getId().equals(testFunction.getId())));
    }

    @Test
    void testFindByXValueBetween() {
        List<PointEntity> pointsInRange = pointRepository.findByxValueBetween(2.0, 8.0);
        assertEquals(1, pointsInRange.size());
        assertEquals(5.0, pointsInRange.get(0).getXValue());
    }

    @Test
    void testDeleteByFunction() {
        FunctionEntity anotherFunction = new FunctionEntity("Another Function", "LINKED_LIST", testUser);
        functionRepository.save(anotherFunction);

        PointEntity point = new PointEntity(2.0, 4.0, anotherFunction);
        pointRepository.save(point);

        pointRepository.deleteByFunction(anotherFunction);
        List<PointEntity> remainingPoints = pointRepository.findByFunction(anotherFunction);
        assertTrue(remainingPoints.isEmpty());
    }

    @Test
    void testDeletePoint() {
        pointRepository.deleteById(testPoint1.getId());
        assertFalse(pointRepository.findById(testPoint1.getId()).isPresent());
    }

    @Test
    void testUpdatePoint() {
        PointEntity point = pointRepository.findByFunction(testFunction).get(0);
        point.setYValue(999.0);

        PointEntity updatedPoint = pointRepository.save(point);
        assertEquals(999.0, updatedPoint.getYValue());
    }

    @Test
    void testFindAllPoints() {
        List<PointEntity> allPoints = pointRepository.findAll();
        assertTrue(allPoints.size() >= 3);
    }
}