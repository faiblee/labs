package ru.ssau.tk.faible.labs.controller;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.faible.labs.DTO.UserDTO;
import ru.ssau.tk.faible.labs.DTO.UserPutDTO;
import ru.ssau.tk.faible.labs.entity.User;
import ru.ssau.tk.faible.labs.repository.UserRepository;
import ru.ssau.tk.faible.labs.service.SecurityService;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final SecurityService securityService;

    public UserController(UserRepository userRepository, SecurityService securityService) {
        this.userRepository = userRepository;
        this.securityService = securityService;
    }

    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        // Проверка роли, если не админ — ошибка
        if (!securityService.isAdmin()) {
            throw new RuntimeException("Access denied: ADMIN only");
        }

        log.info("ADMIN requested all users");


        // Получаем всех пользователей из БД, преобразуем каждую сущность User в UserDTO
        return userRepository.findAll().stream()
                .map(u -> new UserDTO(
                        u.getId(),
                        u.getUsername(),
                        u.getFactoryType(),
                        u.getRole()
                ))
                .collect(Collectors.toList()); // Собираем в список.
    }

    @GetMapping("/users/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        // получаем текущего авторизованного пользователя
        User currentUser = securityService.getCurrentUser();

        // Находим целевого пользователя по ID или бросаем исключение
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Проверка прав, если ADMIN — разрешено,если обычный пользователь — разрешено только если ID совпадает.
        if (!securityService.isAdmin() && !currentUser.getId().equals(targetUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        log.info("User {} accessed profile of user {}", currentUser.getUsername(), targetUser.getUsername());

        // Возвращаем DTO без пароля
        return new UserDTO(
                targetUser.getId(),
                targetUser.getUsername(),
                targetUser.getFactoryType(),
                targetUser.getRole()
        );
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserPutDTO dto
    ) {
        User currentUser = securityService.getCurrentUser();
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // проверка прав
        if (!securityService.isAdmin() && !currentUser.getId().equals(targetUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Обновляем поля
        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            targetUser.setUsername(dto.getUsername());
        }
        if (dto.getFactory_type() != null && !dto.getFactory_type().isEmpty() ) {
            targetUser.setFactoryType(dto.getFactory_type());
        }

        if (dto.getOld_password() != null && !dto.getOld_password().isEmpty() &&
        dto.getNew_password() != null && !dto.getNew_password().isEmpty()) {
            String old_hash_password = currentUser.getPasswordHash();

            if (!BCrypt.checkpw(dto.getOld_password(), old_hash_password)) { // если введен неверный старый пароль
                log.error("Введен неверный старый пароль");
                throw new IllegalArgumentException("Введен неверный старый пароль");
            }

            String newPassword = dto.getNew_password();
            targetUser.setPasswordHash(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        }

        // Сохраняем изменения в базу данных
        userRepository.save(targetUser);


        log.info("User {} updated user {}", currentUser.getUsername(), targetUser.getUsername());


        UserDTO userDto = new UserDTO(
                targetUser.getId(),
                targetUser.getUsername(),
                targetUser.getFactoryType(),
                targetUser.getRole()
        );

        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }

    // Устанавливает HTTP-статус 204
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        // Проверка роли
        if (!securityService.isAdmin()) {
            throw new RuntimeException("Access denied: ADMIN only");
        }

        // Удаляем пользователя из БД
        userRepository.deleteById(id);

        log.info("ADMIN deleted user with ID: {}", id);
    }
}