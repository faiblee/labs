package ru.ssau.tk.faible.labs.servlets;

import com.fasterxml.jackson.databind.JsonNode;
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

import static ru.ssau.tk.faible.labs.database.utils.ServletHelper.sendError;

@Slf4j
@WebServlet("/api/points/*")
public class PointServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = -8088709893079542673L;

    private FunctionsDAO functionDAO;
    private UsersDAO usersDAO;
    private PointsDAO pointsDAO;
    private ObjectMapper objectMapper;

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

        this.pointsDAO = new PointsDAO(DBConnector.initConnect());
        this.usersDAO = new UsersDAO(pointsDAO.getConnection());
        this.functionDAO = new FunctionsDAO(pointsDAO.getConnection());
        this.objectMapper = new ObjectMapper();
    }

    // GET /api/points/{id} - получение точки по id
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        User user = ServletHelper.authenticateUser(req, usersDAO);
        if (user == null) { // пользователь не авторизован
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован", objectMapper);
            return;
        }

        if (pathInfo == null || pathInfo.isEmpty() || pathInfo.equals("/")) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный запрос", objectMapper);
            return;
        }

        try {
            int pointId = Integer.parseInt(pathInfo.substring(1));
            Point point = pointsDAO.getPointById(pointId);
            if (point == null) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Не найдена точка с данным id", objectMapper);
                return;
            }
            int functionId = point.getFunction_id();
            Function function = functionDAO.getFunctionById(functionId);
            if (function.getOwner_id() != user.getId() && !user.getRole().equals("ADMIN")) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
                return;
            }
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();
            out.print(objectMapper.writeValueAsString(point));
            out.flush();
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат данных", objectMapper);
        }
    }

    // PUT /api/points/{id} - изменение точки по id - владелец или admin
    // Body: { "x_value": 1.0, "y_value": 2.0 }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        User user = ServletHelper.authenticateUser(req, usersDAO);
        if (user == null) { // пользователь не авторизован
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован", objectMapper);
            return;
        }
        if (pathInfo == null || pathInfo.isEmpty() || pathInfo.equals("/")) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный URL запроса, требуется id точки", objectMapper);
            return;
        }
        int pointId;
        try {
            pointId = Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат данных (id)", objectMapper);
            return;
        }
        Point point = pointsDAO.getPointById(pointId);
        if (point == null) { // если точки с данным id не существует
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Точки с данным id не существует", objectMapper);
            return;
        }
        int functionId = point.getFunction_id();
        Function function = functionDAO.getFunctionById(functionId);
        if (function.getOwner_id() != user.getId() && !user.getRole().equals("ADMIN")) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
            return;
        }
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;

            while ((line = reader.readLine()) != null) { // Считываем body запроса
                sb.append(line);
            }

            ObjectNode update = (ObjectNode) objectMapper.readTree(sb.toString());
            if (!update.has("x_value") || !update.has("y_value")) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Отсутствуют необходимые поля", objectMapper);
                return;
            }

            JsonNode xNode = update.get("x_value");
            JsonNode yNode = update.get("y_value");

            if (xNode == null || xNode.isNull() || !xNode.isNumber()) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "x_value должен быть числом", objectMapper);
                return;
            }
            if (yNode == null || yNode.isNull() || !yNode.isNumber()) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "y_value должен быть числом", objectMapper);
                return;
            }

            double new_x = xNode.asDouble();
            double new_y = yNode.asDouble();

            int changedRows = pointsDAO.updatePointById(pointId, new_x, new_y);
            if (changedRows > 0) {
                Point updated = pointsDAO.getPointById(pointId);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter out = resp.getWriter();
                out.print(objectMapper.writeValueAsString(updated));
                out.flush();
            } else {
                sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Не удалось обновить точку", objectMapper);
            }
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Ошибка при обновлении точки", objectMapper);
        }
    }


    // DELETE /api/points/{id} - либо владелец функции, либо админ
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        User user = ServletHelper.authenticateUser(req, usersDAO);
        if (user == null) { // пользователь не авторизован
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован", objectMapper);
            return;
        }
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Требуется ID точки", objectMapper);
            return;
        }

        int pointId;
        try {
            pointId = Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат ID точки", objectMapper);
            return;
        }
        Point point = pointsDAO.getPointById(pointId);
        if (point == null) { // если точки с данным id не существует
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Точки с данным id не существует", objectMapper);
            return;
        }
        int functionId = point.getFunction_id();
        Function function = functionDAO.getFunctionById(functionId);
        if (function.getOwner_id() != user.getId() && !user.getRole().equals("ADMIN")) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
            return;
        }

        int deleted = pointsDAO.deletePointById(pointId);
        if (deleted > 0) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Точка не найдена", objectMapper);
        }
    }

    @Override
    public void destroy() {
        DBConnector.closeConnection(pointsDAO.getConnection());
    }
}
