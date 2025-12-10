package ru.ssau.tk.faible.labs.controller;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.faible.labs.DTO.LoginDTO;
import ru.ssau.tk.faible.labs.DTO.UserDTO;
import ru.ssau.tk.faible.labs.entity.User;
import ru.ssau.tk.faible.labs.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/login")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public ResponseEntity<UserDTO> login(
            @RequestHeader("Authorization") String usernameAndPassword) {

        String base64Credentials = usernameAndPassword.substring("Basic ".length());
        String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
        String[] parts = credentials.split(":", 2); // декодируем данные и разбиваем логин с паролем
        if (parts.length != 2) {
            log.error("Заголовок не соответствует формату");
            return null;
        }

        String username = parts[0];
        String password = parts[1];

        log.info("Login attempt for user: {}", username);

        // Валидация входных данных
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new RuntimeException("Username and password are required");
        }

        // Поиск пользователя в БД
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // Проверка пароля через BCrypt
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Логируем успешный вход
        log.info("User {} logged in successfully", user.getUsername());

        // Возвращаем DTO (без пароля!)
        UserDTO userDto = new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getFactoryType(),
                user.getRole()
        );

        return ResponseEntity.ok(userDto);
    }

}
