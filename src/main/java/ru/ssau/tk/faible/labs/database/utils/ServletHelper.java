package ru.ssau.tk.faible.labs.database.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ssau.tk.faible.labs.database.daos.UsersDAO;
import ru.ssau.tk.faible.labs.database.models.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ServletHelper {

    // Проверяет, может ли пользователь с ролью role вызывать метод
    public static boolean isAllowed(String userRole, String... allowedRoles) {
        if (userRole == null) return false;
        for (String allowed : allowedRoles) {
            if (userRole.equals(allowed)) return true;
        }
        return false;
    }

    public static User authenticateUser(HttpServletRequest req, UsersDAO usersDAO) throws IOException {
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) { // если заголовок неверный
            return null;
        }

        try {
            String base64Credentials = authHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
            String[] parts = credentials.split(":", 2); // декодируем данные и разбиваем логин с паролем
            if (parts.length != 2) {
                return null;
            }

            String username = parts[0];
            String password = parts[1];

            User user = usersDAO.getUserByUsername(username); // проверяем по базе данных пользователя
            if (user == null || !SqlHelper.checkPassword(password, user.getPassword_hash())) {
                return null;
            }

            return user;
        } catch (Exception e) {
            return null;
        }
    }

    public static void sendError(HttpServletResponse resp, int statusCode, String message, ObjectMapper objectMapper) throws IOException { // Отправка ошибки
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        ObjectNode error = objectMapper.createObjectNode();
        error.put("error", message);
        PrintWriter out = resp.getWriter();
        out.print(objectMapper.writeValueAsString(error));
        out.flush();
    }
}
