package ru.ssau.tk.faible.labs.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.GettableHashMap;
import ru.ssau.tk.faible.labs.database.daos.FunctionsDAO;
import ru.ssau.tk.faible.labs.database.daos.PointsDAO;
import ru.ssau.tk.faible.labs.database.daos.UsersDAO;
import ru.ssau.tk.faible.labs.database.models.User;
import ru.ssau.tk.faible.labs.database.models.UserResponseDTO;
import ru.ssau.tk.faible.labs.database.utils.DBConnector;

import java.io.IOException;
import java.io.PrintWriter;

import static ru.ssau.tk.faible.labs.database.utils.ServletHelper.authenticateUser;

@WebServlet("/api/auth/login")
@Slf4j
public class LoginAuthServlet extends HttpServlet {
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = authenticateUser(req, usersDAO); // ваш метод проверки
        if (currentUser == null) {
            log.error("Пользователь не найден");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        // Возвращаем только безопасные данные (например, id, username, role)
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(currentUser.getId());
        dto.setUsername(currentUser.getUsername());
        dto.setRole(currentUser.getRole());
        dto.setFactory_type(currentUser.getFactory_type());

        out.print(objectMapper.writeValueAsString(dto));
        out.flush();
    }
}
