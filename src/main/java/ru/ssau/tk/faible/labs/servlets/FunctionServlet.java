package ru.ssau.tk.faible.labs.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ru.ssau.tk.faible.labs.database.daos.FunctionsDAO;
import ru.ssau.tk.faible.labs.database.daos.PointsDAO;
import ru.ssau.tk.faible.labs.database.daos.UsersDAO;
import ru.ssau.tk.faible.labs.database.models.CreateFunctionDTO;
import ru.ssau.tk.faible.labs.database.models.Function;
import ru.ssau.tk.faible.labs.database.models.Point;
import ru.ssau.tk.faible.labs.database.models.User;
import ru.ssau.tk.faible.labs.database.utils.DBConnector;
import ru.ssau.tk.faible.labs.database.utils.ServletHelper;
import ru.ssau.tk.faible.labs.functions.*;
import ru.ssau.tk.faible.labs.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.functions.factory.TabulatedFunctionFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.ssau.tk.faible.labs.database.utils.ServletHelper.isAllowed;
import static ru.ssau.tk.faible.labs.database.utils.ServletHelper.sendError;

@Slf4j
@WebServlet("/api/functions/*")
public class FunctionServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 5880201855947928659L;

    private FunctionsDAO functionsDAO;
    private PointsDAO pointsDAO;
    private ObjectMapper objectMapper;
    private UsersDAO usersDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            Class.forName("org.postgresql.Driver");
            log.info("Драйвер успешно загружен");
        } catch (ClassNotFoundException e) {
            log.error("Ошибка загрузки драйвера");
            throw new RuntimeException(e);
        }

        this.functionsDAO = new FunctionsDAO(DBConnector.initConnect());
        this.usersDAO = new UsersDAO(functionsDAO.getConnection());
        this.pointsDAO = new PointsDAO(functionsDAO.getConnection());
        this.objectMapper = new ObjectMapper();
    }

    // Обработка запросов GET /api/functions
    private void handleGetAllFunctions(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, User user) throws IOException {
        String ownerIdParam = req.getParameter("ownerId");

        List<Function> functions;

        log.info("Owner id param = {}", ownerIdParam);

        if (ownerIdParam != null) { // сортировка по ownerId
            log.info("Получен запрос на получение данных с сортировкой ownerId");
            try {
                int ownerId = Integer.parseInt(ownerIdParam);
                if (user.getId() != ownerId) {
                    log.warn("Доступ запрещен");
                    sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
                    return;
                }
                functions = functionsDAO.getAllFunctionsByOwnerId(ownerId);
            } catch (NumberFormatException e) {
                log.error("Неверный формат ownerId");
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат ownerId", objectMapper);
                return;
            }
        } else {
            if (!isAllowed(user.getRole(), "ADMIN")) {
                log.warn("Доступ запрещен");
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
                return;
            }
            log.info("Получен запрос на получение всех функций");
            functions = functionsDAO.getAllFunctions();
        }
        log.info("Успешно получены все функции");
        out.print(objectMapper.writeValueAsString(functions));
        out.flush();
    }

    // Обработка запросов GET /api/functions/{id}
    private void handleGetFunctionById(String idStr, HttpServletResponse resp, PrintWriter out, User owner) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
            log.info("Обработка запроса на получение функции по ID: {}", id);

            Function function = functionsDAO.getFunctionById(id);
            if (function == null) {
                log.warn("Функция с ID={} не найдена", id);
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Функция не найдена", objectMapper);
                return;
            }
            if (function.getOwnerId() != owner.getId() && !owner.getRole().equals("ADMIN")) { // если юзер - не владелец функции и не админ
                log.warn("Доступ запрещен");
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
                return;
            }
            out.print(objectMapper.writeValueAsString(function));
            out.flush();

            log.info("Функция с ID={} успешно отправлена пользователю ID={}", id, owner.getId());

        } catch (NumberFormatException e) {
            log.warn("Неверный формат ID при запросе функции: {}", idStr, e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат ID", objectMapper);
        } catch (Exception e) {
            log.error("Неожиданная ошибка при обработке запроса функции по ID={}", idStr, e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка сервера", objectMapper);
        }
    }

    // обработка запросов GET /api/functions/{id}/points
    private void handleGetPointsByFunctionId(String idStr, HttpServletRequest req, HttpServletResponse resp, PrintWriter out, User owner) throws IOException {
        try {
            int functionId = Integer.parseInt(idStr);
            Function function = functionsDAO.getFunctionById(functionId);
            // Проверяем, существует ли функция
            if (function == null) {
                log.error("Функция с id = {} не найдена", functionId);
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Функция не найдена", objectMapper);
                return;
            }

            if (function.getOwnerId() != owner.getId() && !owner.getRole().equals("ADMIN")) { // если юзер - не владелец функции и не админ
                log.warn("Доступ запрещен");
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
                return;
            }

            List<Point> points;
            points = pointsDAO.getPointsByFunctionId(functionId);
            if (points == null) {
                log.error("Не найдены точки выбранной функции");
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Не найдены точки выбранной функции", objectMapper);
            }
            out.print(objectMapper.writeValueAsString(points));
            log.info("Тело ответа: {}", objectMapper.writeValueAsString(points));
            out.flush();

        } catch (NumberFormatException e) {
            log.error("Неверный формат ID функции");
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат ID функции", objectMapper);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("Получен GET запрос по /api/functions");
        User user = ServletHelper.authenticateUser(req, usersDAO);
        if (user == null) { // пользователь не авторизован
            log.error("Пользователь не авторизован");
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован", objectMapper);
            return;
        }
        log.info("Пользователь авторизован");
        String role = user.getRole();
        // Если пользователь авторизован, продолжаем
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String pathInfo = req.getPathInfo();

        log.info("Path info - {}", pathInfo);
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            // GET /api/functions — фильтрация по ownerId/все функции
            log.info("Получен запрос на получение функций");
            handleGetAllFunctions(req, resp, out, user);
            return;
        }

        // Убираем начальный /
        pathInfo = pathInfo.substring(1);
        String[] parts = pathInfo.split("/", 2);

        if (parts.length == 1) {
            // GET /api/functions/{id} - получение функции по id - только для admin или владелец функции
            log.debug("Получен запрос на получение функции по id");
            if (!isAllowed(role, "ADMIN", "USER")) {
                log.warn("Доступ запрещен");
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
                return;
            }
            handleGetFunctionById(parts[0], resp, out, user);
        } else if (parts.length == 2 && "points".equals(parts[1])) {
            // GET /api/functions/{id}/points - получение всех точек функции - ADMIN или владелец функции
            log.debug("Получен запрос на получение всех точек функции");
            if (!isAllowed(role, "ADMIN", "USER")) {
                log.warn("Доступ запрещен");
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
                return;
            }
            handleGetPointsByFunctionId(parts[0], req, resp, out, user);
        } else {
            log.error("Неверный путь");
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный путь", objectMapper);
        }
    }

    // Обработка запроса POST /api/functions - создание функции
    private void handlePostFunction(HttpServletRequest req, HttpServletResponse resp, int ownerId) throws IOException {
        try {
            log.info("Начало обработки POST-запроса для создания функции");
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();

            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            log.info("Тело запроса - {}", sb.toString());

            CreateFunctionDTO input = objectMapper.readValue(sb.toString(), CreateFunctionDTO.class);

            log.info("Тело успешно считано, функция - {}, тип - {}, owner_id - {}", input.getName(), input.getType(), input.getOwnerId());
            log.info("Owners ids - {} {}", input.getOwnerId(), ownerId);

            if (input.getName() == null || input.getType() == null || ownerId <= 0) {
                log.error("Отклонён запрос: некорректные данные — name={}, type={}, owner_id={}",
                        input.getName(), input.getType(), input.getOwnerId());
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат данных", objectMapper);
                return;
            }

            int functionId = functionsDAO.insertFunction(input.getName(), input.getOwnerId(), input.getType());

            log.info("Функция успешно добавлена в БД с id={}", functionId);

            String factory_type = input.getFactory_type();
            String type = input.getType();
            if (!type.isEmpty() && !type.equals("Tabulated")) {
                double xFrom = input.getXFrom();
                double xTo = input.getXTo();
                int count = input.getCount();

                TabulatedFunctionFactory factory;
                if (factory_type.equals("array")) {
                    factory = new ArrayTabulatedFunctionFactory();
                } else {
                    factory = new LinkedListTabulatedFunctionFactory();
                }
                Map<String, MathFunction> functions = new HashMap<>();
                functions.put("Квадратичная функция", new SqrFunction());
                functions.put("Тождественная функция", new IdentityFunction());
                functions.put("Константная функция", new ConstantFunction(input.getConstant()));
                functions.put("Функция с константой 0", new ZeroFunction());
                functions.put("Функция с константой 1", new UnitFunction());

                TabulatedFunction function = factory.create(functions.get(type), xFrom, xTo, count);

                for (ru.ssau.tk.faible.labs.functions.Point point : function) {
                    pointsDAO.insertPoint(point.x, point.y, functionId);
                }
            }

            Function response = new Function();
            response.setId(functionId);
            response.setName(input.getName());
            response.setOwnerId(ownerId);
            response.setType(input.getType());

            resp.setStatus(HttpServletResponse.SC_CREATED);
            String jsonResponse = objectMapper.writeValueAsString(response);
            out.print(jsonResponse);
            out.flush();

            log.info("Функция с id = {} успешно добавлена", functionId);

        } catch (Exception e) {
            log.error("Ошибка при обработке POST-запроса для /function - {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат данных", objectMapper);
        }
    }

    // Обработка запросов POST /api/functions/{id}/points - добавление точки
    private void handlePostPoint(String idStr, HttpServletRequest req, HttpServletResponse resp, User owner) throws IOException {
        try {
            int functionId = Integer.parseInt(idStr);
            Function function = functionsDAO.getFunctionById(functionId);
            // Проверяем, существует ли функция
            if (function == null) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Функция не найдена", objectMapper);
                return;
            }
            log.info("Функция существует");
            // если юзер - не владелец функции и не админ
            if (function.getOwnerId() != owner.getId() && !owner.getRole().equals("ADMIN")) {
                log.warn("Доступ запрещен");
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
                return;
            }
            log.info("Доступ разрешен");
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();

            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            log.info("Body успешно считан: {}", sb);
            Point input = objectMapper.readValue(sb.toString(), Point.class);
            log.info("objectMapper успешно считал");
            if (input.getXValue() == null || input.getYValue() == null) {
                log.error("x или y - null");
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Требуются x_value и y_value", objectMapper);
                return;
            }

            log.info("{} {}", input.getXValue(), input.getYValue());

            int pointId = pointsDAO.insertPoint(input.getXValue(), input.getYValue(), functionId);

            Point response = new Point();
            response.setId(pointId);
            response.setXValue(input.getXValue());
            response.setYValue(input.getYValue());
            response.setFunctionId(functionId);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            log.info("Точка успешно добавлена");
            out.print(objectMapper.writeValueAsString(response));
            out.flush();

        } catch (Exception e) {
            log.error("Ошибка при парсинге JSON", e); // ← вот это обязательно!
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат данных", objectMapper);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("Получен POST запрос");
        String pathInfo = req.getPathInfo();
        User user = ServletHelper.authenticateUser(req, usersDAO);
        if (user == null) { // пользователь не авторизован
            log.error("Пользователь не авторизован");
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован", objectMapper);
            return;
        }
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            // POST /api/functions — создание функции - любой авторизованный
            log.debug("Получен запрос на создание функции");
            handlePostFunction(req, resp, user.getId());
            return;
        }

        pathInfo = pathInfo.substring(1);
        String[] parts = pathInfo.split("/", 2);

        if (parts.length == 2 && "points".equals(parts[1])) {
            // POST /api/functions/{id}/points — добавить точку - владелец функции или admin
            log.debug("Получен POST запрос на добавление точки");
            handlePostPoint(parts[0], req, resp, user);
        } else {
            log.error("Неверный путь");
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный путь", objectMapper);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // PUT - /api/functions/{id} - изменение функции - владелец функции или admin
        log.info("Получен PUT запрос на изменение функции");
        User owner = ServletHelper.authenticateUser(req, usersDAO);
        if (owner == null) { // пользователь не авторизован
            log.error("Пользователь не авторизован");
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован", objectMapper);
            return;
        }

        log.info("Получен запрос на изменение функции");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            log.error("Неверный URL - требуется /id");
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Требуется ID функции", objectMapper);
            return;
        }

        pathInfo = pathInfo.substring(1); // считываем id
        int functionId;
        try {
            functionId = Integer.parseInt(pathInfo);
        } catch (NumberFormatException e) {
            log.error("Неверный формат Id");
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат ID", objectMapper);
            return;
        }
        Function function = functionsDAO.getFunctionById(functionId);

        if (function == null) {
            log.error("Функция с id = {} не найдена", functionId);
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Функция не найдена", objectMapper);
            return;
        }
        // если юзер - не владелец функции и не админ
        if (function.getOwnerId() != owner.getId() && !owner.getRole().equals("ADMIN")) {
            log.warn("Доступ запрещен");
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
            return;
        }

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) { // считываем body запроса
                sb.append(line);
            }

            ObjectNode update = (ObjectNode) objectMapper.readTree(sb.toString());
            if (update.has("name") && !update.get("name").isNull()) { // если в теле был передан новый name, изменяем
                String name = update.get("name").asText();
                functionsDAO.updateName(name, functionId);
                log.info("Успешно изменен name функции на {}", name);
            }
            if (update.has("type") && !update.get("type").isNull()) { // если в теле был передан новый type, изменяем
                String type = update.get("type").asText();
                functionsDAO.updateType(type, functionId);
                log.info("Успешно изменен type функции на {}", type);
            }

            Function updated = functionsDAO.getFunctionById(functionId);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();
            out.print(objectMapper.writeValueAsString(updated));
            out.flush();
            log.info("Запрос на изменение успешно обработан");
        } catch (Exception e) {
            log.error("Ошибка обновления");
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Ошибка обновления", objectMapper);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // DELETE - /api/functions/{id} - удаление функции по id - admin или владелец функции
        log.info("Получен DELETE запрос на удаление функции по id");
        User owner = ServletHelper.authenticateUser(req, usersDAO);
        if (owner == null) { // пользователь не авторизован
            log.error("Пользователь не авторизован");
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован", objectMapper);
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            log.error("Неверный формат URL - требуется /id");
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Требуется id функции", objectMapper);
            return;
        }

        pathInfo = pathInfo.substring(1);
        int functionId;
        try {
            functionId = Integer.parseInt(pathInfo);
        } catch (NumberFormatException e) {
            log.error("Неверный формат id");
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат id", objectMapper);
            return;
        }

        Function function = functionsDAO.getFunctionById(functionId);
        // если юзер - не владелец функции и не админ
        if (function.getOwnerId() != owner.getId() && !owner.getRole().equals("ADMIN")) {
            log.warn("Доступ запрещен");
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
            return;
        }

        int changedRows = functionsDAO.deleteFunctionById(functionId);
        if (changedRows > 0) { // если было успешное удаление
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            log.error("Функция по id = {} не найдена", functionId);
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Функция не найдена", objectMapper);
        }
    }

    @Override
    public void destroy() {
        DBConnector.closeConnection(functionsDAO.getConnection());
    }
}
