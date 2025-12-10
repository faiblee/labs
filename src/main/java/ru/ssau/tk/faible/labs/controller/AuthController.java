package ru.ssau.tk.faible.labs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.faible.labs.DTO.UserRegistrationDTO;
import ru.ssau.tk.faible.labs.DTO.UserDTO;
import ru.ssau.tk.faible.labs.entity.User;
import ru.ssau.tk.faible.labs.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserRegistrationDTO dto) {
        // Логируем факт начала регистрации
        log.info("Registering new user: {}, {}", dto.getUsername(), dto.getFactory_type());

        // логин обязателен
        if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
            // Если логин не указан, выбрасываем исключение
            throw new RuntimeException("Username is required");
        }

        // Пароль обязателен, иначе исключение
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        // Проверка уникальности логина
        if (userRepository.existsByUsername(dto.getUsername())) {
            // Если пользователь с таким логином уже существует, выбрасываем исключение
            throw new RuntimeException("Username already exists");
        }

        // Хэширование пароля с использованием BCrypt
        String hashedPassword = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());

        // Установка роли по умолчанию, если роль не указана — присваиваем "USER"
        String role = dto.getRole() != null ? dto.getRole() : "USER";

        // Установка фабрики по умолчанию, если фабрика не указана — присваиваем "array"
        String factory_type = dto.getFactory_type() != null ? dto.getFactory_type() : "array";

        User user = new User(
                dto.getUsername(),
                hashedPassword,
                factory_type,
                role
        );

        // Сохраненяем в базы данных
        User savedUser = userRepository.save(user);

        // Логирование успешной регистрации
        log.info("User registered successfully: {}", savedUser.getUsername());

        // UserDto не содержит passwordHash
        UserDTO userDto = new UserDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getFactoryType(),
                savedUser.getRole()
        );

        // возврат ответа с HTTP-статусом 201 C
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }
}