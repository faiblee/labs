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
import ru.ssau.tk.faible.labs.database.models.Function;
import ru.ssau.tk.faible.labs.database.models.Point;
import ru.ssau.tk.faible.labs.database.models.User;
import ru.ssau.tk.faible.labs.database.utils.DBConnector;
import ru.ssau.tk.faible.labs.database.utils.ServletHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;
import java.util.List;

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
    private void handleGetAllFunctions(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) throws IOException {
        String ownerIdParam = req.getParameter("ownerId");
        String typeParam = req.getParameter("type");

        List<Function> functions;
        if (ownerIdParam != null && typeParam != null) { // два параметра ownerId и type
            try {
                int ownerId = Integer.parseInt(ownerIdParam);
                functions = functionsDAO.getFunctionsByOwnerAndType(ownerId, typeParam);
            } catch (NumberFormatException e) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат ownerId", objectMapper);
                return;
            }
        } else if (ownerIdParam != null) { // сортировка по type
            try {
                int ownerId = Integer.parseInt(ownerIdParam);
                functions = functionsDAO.getAllFunctionsByOwnerId(ownerId);
            } catch (NumberFormatException e) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат ownerId", objectMapper);
                return;
            }
        } else if (typeParam != null) {
            functions = functionsDAO.getAllFunctionsByType(typeParam);
        } else {
            functions = functionsDAO.getAllFunctions();
        }
        out.print(objectMapper.writeValueAsString(functions));
        out.flush();
    }

    // Обработка запросов GET /api/functions/{id}
    private void handleGetFunctionById(String idStr, HttpServletResponse resp, PrintWriter out, User owner) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
            Function function = functionsDAO.getFunctionById(id);
            if (function == null) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Функция не найдена", objectMapper);
                return;
            }
            if (function.getOwner_id() != owner.getId() && !owner.getRole().equals("ADMIN")) { // если юзер - не владелец функции и не админ
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
                return;
            }
            out.print(objectMapper.writeValueAsString(function));
            out.flush();
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат ID", objectMapper);
        }
    }

    // обработка запросов GET /api/functions/{id}/points
    private void handleGetPointsByFunctionId(String idStr, HttpServletRequest req, HttpServletResponse resp, PrintWriter out, User owner) throws IOException {
        try {
            int functionId = Integer.parseInt(idStr);
            Function function = functionsDAO.getFunctionById(functionId);
            // Проверяем, существует ли функция
            if (function == null) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Функция не найдена", objectMapper);
                return;
            }

            if (function.getOwner_id() != owner.getId() && !owner.getRole().equals("ADMIN")) { // если юзер - не владелец функции и не админ
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
                return;
            }

            // возможные параметры - minX и maxX
            String minXParam = req.getParameter("minX");
            String maxXParam = req.getParameter("maxX");

            List<Point> points;
            if (minXParam != null && maxXParam != null) {
                try {
                    double minX = Double.parseDouble(minXParam);
                    double maxX = Double.parseDouble(maxXParam);
                    points = pointsDAO.getPointsByFunctionIdAndBetweenXValue(functionId, minX, maxX);
                } catch (NumberFormatException e) {
                    sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат minX или maxX", objectMapper);
                    return;
                }
            } else { // если нет фильтрации, просто возвращаем все точки
                points = pointsDAO.getPointsByFunctionId(functionId);
            }

            out.print(objectMapper.writeValueAsString(points));
            out.flush();

        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат ID функции", objectMapper);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        User user = ServletHelper.authenticateUser(req, usersDAO);
        if (user == null) { // пользователь не авторизован
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован", objectMapper);
            return;
        }
        String role = user.getRole();
        // Если пользователь авторизован, продолжаем
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            // GET /api/functions — фильтрация по ownerId/type
            if (!isAllowed(role, "ADMIN")) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
                return;
            }
            handleGetAllFunctions(req, resp, out);
            return;
        }

        // Убираем начальный /
        pathInfo = pathInfo.substring(1);
        String[] parts = pathInfo.split("/", 2);

        if (parts.length == 1) {
            // GET /api/functions/{id} - получение функции по id - только для admin или владелец функции
            if (!isAllowed(role, "ADMIN", "USER")) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
                return;
            }
            int functionId = Integer.parseInt(parts[0]);
            handleGetFunctionById(parts[0], resp, out, user);
        } else if (parts.length == 2 && "points".equals(parts[1])) {
            // GET /api/functions/{id}/points - получение всех точек функции - ADMIN или владелец функции
            if (!isAllowed(role, "ADMIN", "USER")) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
                return;
            }
            handleGetPointsByFunctionId(parts[0], req, resp, out, user);
        } else {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный путь", objectMapper);
        }
    }

    // Обработка запроса POST /api/functions - создание функции
    private void handlePostFunction(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

            Function input = objectMapper.readValue(sb.toString(), Function.class);

            if (input.getName() == null || input.getType() == null || input.getOwner_id() <= 0) {
                log.error("Отклонён запрос: некорректные данные — name={}, type={}, owner_id={}",
                        input.getName(), input.getType(), input.getOwner_id());
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат данных", objectMapper);
                return;
            }

            log.info("{}, {}, {}", input.getName(), input.getOwner_id(), input.getType());
            int functionId = functionsDAO.insertFunction(input.getName(), input.getOwner_id(), input.getType());
            log.info("Функция успешно добавлена в БД с id={}", functionId);

            Function response = new Function();
            response.setId(functionId);
            response.setName(input.getName());
            response.setOwner_id(input.getOwner_id());
            response.setType(input.getType());

            resp.setStatus(HttpServletResponse.SC_CREATED);
            String jsonResponse = objectMapper.writeValueAsString(response);
            out.print(jsonResponse);
            out.flush();

            log.info("Функция с id = {} успешно добавлена", functionId);

        } catch (Exception e) {
            log.error("Ошибка при обработке POST-запроса для /function");
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

            // если юзер - не владелец функции и не админ
            if (function.getOwner_id() != owner.getId() && !owner.getRole().equals("ADMIN")) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
                return;
            }

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();

            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            Point input = objectMapper.readValue(sb.toString(), Point.class);
            if (input.getX_value() == null || input.getY_value() == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Требуются x_value и y_value", objectMapper);
                return;
            }

            int pointId = pointsDAO.insertPoint(input.getX_value(), input.getY_value(), functionId);

            Point response = new Point();
            response.setId(pointId);
            response.setX_value(input.getX_value());
            response.setY_value(input.getY_value());
            response.setFunction_id(functionId);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            out.print(objectMapper.writeValueAsString(response));
            out.flush();

        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат данных", objectMapper);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        User user = ServletHelper.authenticateUser(req, usersDAO);
        if (user == null) { // пользователь не авторизован
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован", objectMapper);
            return;
        }
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            // POST /api/functions — создание функции - любой авторизованный
            handlePostFunction(req, resp);
            return;
        }

        pathInfo = pathInfo.substring(1);
        String[] parts = pathInfo.split("/", 2);

        if (parts.length == 2 && "points".equals(parts[1])) {
            // POST /api/functions/{id}/points — добавить точку - владелец функции или admin
            handlePostPoint(parts[0], req, resp, user); // v
        } else {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный путь", objectMapper);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // PUT - /api/functions/{id} - изменение функции - владелец функции или admin

        User owner = ServletHelper.authenticateUser(req, usersDAO);
        if (owner == null) { // пользователь не авторизован
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
        if (function.getOwner_id() != owner.getId() && !owner.getRole().equals("ADMIN")) {
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

        User owner = ServletHelper.authenticateUser(req, usersDAO);
        if (owner == null) { // пользователь не авторизован
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
        if (function.getOwner_id() != owner.getId() && !owner.getRole().equals("ADMIN")) {
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
