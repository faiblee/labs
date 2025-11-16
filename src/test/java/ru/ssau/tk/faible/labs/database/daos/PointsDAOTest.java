package ru.ssau.tk.faible.labs.database.daos;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk.faible.labs.database.models.Function;
import ru.ssau.tk.faible.labs.database.models.Point;
import ru.ssau.tk.faible.labs.database.utils.DBConnector;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PointsDAOTest {
    PointsDAO pointsDAO;
    List<Integer> functions_ids = new LinkedList<>();
    int firstDotFirstFunctionId;
    int firstDotSecondFunctionId;
    int firstDotThirdFunctionId;
    @BeforeEach
    void setUp() {
        pointsDAO = new PointsDAO(DBConnector.initConnect());
        pointsDAO.deleteAllPoints();
        FunctionsDAO functionsDAO = new FunctionsDAO(DBConnector.initConnect());
        List<Function> functions = functionsDAO.getAllFunctions();
        functions_ids.add(functions.get(0).getId());
        functions_ids.add(functions.get(1).getId());
        functions_ids.add(functions.get(2).getId());
        firstDotFirstFunctionId = pointsDAO.insertPoint(1.0, 1.0, functions_ids.getFirst());
        pointsDAO.insertPoint(2.0, 1.0, functions_ids.getFirst());
        pointsDAO.insertPoint(3.0, 1.0, functions_ids.getFirst());
        firstDotSecondFunctionId = pointsDAO.insertPoint(1.0, 2.0, functions_ids.get(1));
        pointsDAO.insertPoint(2.0, 3.0, functions_ids.get(1));
        pointsDAO.insertPoint(3.0, 4.0, functions_ids.get(1));
        firstDotThirdFunctionId = pointsDAO.insertPoint(1.0, 1.0, functions_ids.get(2));
        pointsDAO.insertPoint(2.0, 4.0, functions_ids.get(2));
        pointsDAO.insertPoint(3.0, 9.0, functions_ids.get(2));
    }

    @AfterEach
    void close() {
        DBConnector.closeConnection(pointsDAO.getConnection());
    }

    @Test
    void insertPoint() {
        int pointId = pointsDAO.insertPoint(0.0, 1.0, functions_ids.getFirst());
        Point point = pointsDAO.getPointById(pointId);
        assertEquals(0.0, point.getX_value());
        assertEquals(1.0, point.getY_value());
        assertEquals(functions_ids.getFirst(), point.getFunction_id());
    }

    @Test
    void getPointById() {
        Point point = pointsDAO.getPointById(firstDotFirstFunctionId);
        assertEquals(1.0, point.getX_value());
        assertEquals(1.0, point.getY_value());
        assertEquals(functions_ids.getFirst(), point.getFunction_id());
    }

    @Test
    void getPointsByFunctionId() {
        List<Point> points = pointsDAO.getPointsByFunctionId(functions_ids.getFirst());
        assertEquals(firstDotFirstFunctionId, points.getFirst().getId());
        assertEquals(1.0, points.getFirst().getX_value());
        assertEquals(2.0, points.get(1).getX_value());
        assertEquals(3.0, points.get(2).getX_value());
        assertEquals(1.0, points.getFirst().getY_value());
        assertEquals(1.0, points.get(1).getY_value());
        assertEquals(1.0, points.get(2).getY_value());
    }

    @Test
    void updatePointById() {
        pointsDAO.updatePointById(firstDotFirstFunctionId, 0.0, 1.0);
        Point point = pointsDAO.getPointById(firstDotFirstFunctionId);
        assertEquals(0.0, point.getX_value());
        assertEquals(1.0, point.getY_value());
    }
}