package ru.ssau.tk.faible.labs.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ru.ssau.tk.faible.labs.database.daos.UsersDAO;
import ru.ssau.tk.faible.labs.database.models.User;
import ru.ssau.tk.faible.labs.database.models.UserWithPassword;
import ru.ssau.tk.faible.labs.database.utils.DBConnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;

import static ru.ssau.tk.faible.labs.database.utils.ServletHelper.sendError;

@Slf4j
@WebServlet("/api/auth/register")
public class AuthServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 3200801214537736701L;
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
        this.objectMapper = new ObjectMapper();
        this.usersDAO = new UsersDAO(DBConnector.initConnect());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            log.info("Получен запрос на добавление пользователя");
            // POST /api/users
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) { // Читаем тело запроса
                sb.append(line);
            }
            log.info("Тело запроса: {}", sb);
            UserWithPassword user = objectMapper.readValue(sb.toString(), UserWithPassword.class); // Преобразуем JSON в объект UserWithPassword
            if (user.getUsername() == null || user.getPassword() == null) { // Проверяем, что username и password не null
                log.error("Неверный формат данных");
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат данных", objectMapper); // Отправляем ошибку 400
                return;
            }

            String factoryType = user.getFactoryType() == null ? "array" : user.getFactoryType();
            String role = user.getRole() == null ? "user" : user.getRole();
            int userId = usersDAO.insertUser(user.getUsername(), user.getPassword(), factoryType, role); // Добавляем пользователя в базу данных

            User responseUser = new User();
            responseUser.setId(userId); // Создаем ответ
            responseUser.setPassword_hash(null); // Удаляем пароль из ответа
            responseUser.setUsername(user.getUsername()); // Устанавливаем username в ответ
            responseUser.setFactoryType(factoryType); // Устанавливаем factory_type в ответ
            responseUser.setRole(role); // Устанавливаем role в ответ

            resp.setStatus(HttpServletResponse.SC_CREATED); // Отправляем ответ 201
            out.print(objectMapper.writeValueAsString(responseUser)); // Преобразуем ответ в JSON
            out.flush(); // Отправляем ответ
            log.info("Пользователь добавлен с ID: {}", userId);
        } catch (Exception e) {
            log.error("Ошибка при добавлении пользователя", e);
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат данных", objectMapper); // Отправляем ошибку 400 по умолчанию
        }
    }
}
