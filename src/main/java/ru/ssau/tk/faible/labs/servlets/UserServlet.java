package ru.ssau.tk.faible.labs.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ru.ssau.tk.faible.labs.database.daos.UsersDAO;
import ru.ssau.tk.faible.labs.database.models.User;
import ru.ssau.tk.faible.labs.database.utils.DBConnector;
import ru.ssau.tk.faible.labs.database.utils.ServletHelper;

import java.io.*;
import java.util.List;

import static ru.ssau.tk.faible.labs.database.utils.ServletHelper.sendError;

@Slf4j
@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = -9055067694334707718L;
    private UsersDAO usersDAO;
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
        this.usersDAO = new UsersDAO(DBConnector.initConnect());
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        log.info("Получен запрос на получение пользователей");
        User user = ServletHelper.authenticateUser(req, usersDAO);
        if (user == null) { // пользователь не авторизован
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован", objectMapper);
            return;
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) { // Получение всех пользователей - только admin
            if (!ServletHelper.isAllowed(user.getRole(), "ADMIN")) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
            }
            // GET /api/users
            log.info("Получен запрос на получение всех пользователей");
            List<User> users = usersDAO.selectAllUsers();
            users.forEach(us -> us.setPassword_hash(null)); // Удаляем пароль из ответа
            out.print(objectMapper.writeValueAsString(users)); // Преобразуем список пользователей в JSON
            log.info("Отправлен список пользователей");
            out.flush(); // Отправляем ответ
        } else { // Получение пользователя по ID - админ или сам пользователь
            // GET /api/users/{id}
            log.info("Получен запрос на получение пользователя по ID");
            pathInfo = pathInfo.substring(1); // Убираем первый символ '/'
            try {
                int id = Integer.parseInt(pathInfo); // Преобразуем ID в число
                log.info("ID пользователя: {}", id);
                if (user.getId() != id && !user.getRole().equals("ADMIN")) { // Проверяем, что пользователь не пытается получить данные другого пользователя
                    log.error("Доступ запрещен юзеру с ролью {}", user.getRole());
                    sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
                    return;
                }
                User us = usersDAO.getUserById(id); // Получаем пользователя по ID
                if (us != null) {
                    us.setPassword_hash(null); // Удаляем пароль из ответа
                    out.print(objectMapper.writeValueAsString(us)); // Преобразуем пользователя в JSON
                    out.flush(); // Отправляем ответ
                    log.info("Отправлен пользователь с ID: {}", id);
                } else {
                    log.warn("Пользователь с ID {} не найден", id);
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Пользователь не найден", objectMapper); // Отправляем ошибку 404
                }
            } catch (NumberFormatException e) {
                log.error("Неверный формат ID");
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат ID", objectMapper); // Отправляем ошибку 400
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // PUT /api/users/{id} - изменение юзера, либо админ, либо сам юзер
        log.info("Получен запрос на обновление пользователя");
        User user = ServletHelper.authenticateUser(req, usersDAO);
        if (user == null) { // пользователь не авторизован
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован", objectMapper);
            return;
        }
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            log.error("Неверный формат URL");
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат URL", objectMapper); // Отправляем ошибку 400
        } else {
            pathInfo = pathInfo.substring(1);
            int userId;
            try {
                userId = Integer.parseInt(pathInfo); // Преобразуем ID в число
            } catch (NumberFormatException e) {
                log.error("Неверный формат ID");
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат ID", objectMapper); // Отправляем ошибку 400
                return;
            }
            if (user.getId() != userId && !user.getRole().equals("ADMIN")) { // Проверяем, что пользователь не пытается получить данные другого пользователя
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
                return;
            }
            if (usersDAO.getUserById(userId) == null) { // Проверяем, что пользователь существует
                log.warn("Пользователь с ID {} не найден", userId);
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Пользователь не найден", objectMapper); // Отправляем ошибку 404
                return;
            }

            try {
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = req.getReader();
                String line;
                while((line = reader.readLine()) != null) { // Читаем тело запроса
                    sb.append(line);
                }
                log.info("Тело запроса: {}", sb);
                ObjectNode userNode = (ObjectNode) objectMapper.readTree(sb.toString()); // Преобразуем JSON в объект ObjectNode
                if (userNode.has("username") && !userNode.get("username").isNull()) { // Проверяем, что username не null
                    String newUsername = userNode.get("username").asText(); // Получаем новый username
                    usersDAO.updateUsername(newUsername, userId); // Обновляем username
                    log.info("Имя пользователя обновлено на: {}", newUsername);
                }
                if (userNode.has("new_password") && !userNode.get("new_password").isNull() &&
                userNode.has("old_password") && !userNode.get("old_password").isNull()) { // Проверяем, что old и new password не null
                    String newPassword = userNode.get("new_password").asText(); // Получаем новый password
                    String oldPassword = userNode.get("old_password").asText(); // Получаем старый password

                    usersDAO.updatePassword(oldPassword, newPassword, userId); // Обновляем password
                    log.info("Пароль пользователя обновлен");
                }
                if (userNode.has("role") && !userNode.get("role").isNull()) { // Проверяем, что role не null
                    String newRole = userNode.get("role").asText(); // Получаем новый role
                    usersDAO.updateRole(newRole, userId); // Обновляем role
                    log.info("Роль пользователя обновлена на: {}", newRole);
                }
                if (userNode.has("factory_type") && !userNode.get("factory_type").isNull()) {
                    String factoryType = userNode.get("factory_type").asText();
                    usersDAO.updateFactoryType(factoryType, userId);
                    log.info("Тип фабрики пользователя обновлен на: {}", factoryType);
                }

                User updatedUser = usersDAO.getUserById(userId); // Получаем обновленного пользователя
                updatedUser.setPassword_hash(null); // Удаляем пароль из ответа
                resp.setContentType("application/json"); // Отправляем ответ
                resp.setCharacterEncoding("UTF-8");
                PrintWriter out = resp.getWriter();
                out.print(objectMapper.writeValueAsString(updatedUser)); // Преобразуем пользователя в JSON
                out.flush(); // Отправляем ответ
                log.info("Пользователь обновлен с ID: {}", userId);
            } catch (Exception e) {
                log.error("Ошибка при обновлении пользователя", e);
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат данных", objectMapper); // Отправляем ошибку 400 по умолчанию
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        // DELETE /api/users/{id} - либо админ либо сам юзер
        log.info("Получен запрос на удаление пользователя");
        User user = ServletHelper.authenticateUser(req, usersDAO);
        if (user == null) { // пользователь не авторизован
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован", objectMapper);
            return;
        }
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            log.error("Неверный формат URL");
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат URL", objectMapper); // Отправляем ошибку 400
        } else {
            pathInfo = pathInfo.substring(1); // Убираем первый символ '/'
            int userId;
            try {
                userId = Integer.parseInt(pathInfo); // Преобразуем ID в число
            } catch (NumberFormatException e) {
                log.error("Неверный формат ID");
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат ID", objectMapper); // Отправляем ошибку 400
                return;
            }
            if (user.getId() != userId && !user.getRole().equals("ADMIN")) { // Проверяем, что пользователь не пытается удалить другого пользователя
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен", objectMapper);
                return;
            }
            int changedRows = usersDAO.deleteUserById(userId); // Удаляем пользователя
            if (changedRows > 0) {
                log.info("Пользователь с ID {} удален", userId);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // Отправляем ответ 204
            } else {
                log.warn("Пользователь с ID {} не найден", userId);
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Пользователь не найден", objectMapper); // Отправляем ошибку 404
            }
        }
    }

    @Override
    public void destroy() {
        DBConnector.closeConnection(usersDAO.getConnection());
    }
}
