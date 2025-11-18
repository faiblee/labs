package ru.ssau.tk.faible.labs.repos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.faible.labs.entity.User;
import ru.ssau.tk.faible.labs.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryLogged {
    private static final Logger log = LoggerFactory.getLogger(UserRepositoryLogged.class);

    @Autowired
    public UserRepositoryLogged(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private final UserRepository userRepository;

    public Optional<User> findUserById(long userId) {
        log.debug("Одиночный поиск: пользователь с ID {}", userId);
        return userRepository.findById(userId);
    }

    public Optional<User> findUserByUsername(String username) {
        log.debug("Одиночный поиск: пользователь с именем {}", username);
        return userRepository.findByUsername(username);
    }

    public List<User> findByRoleOrderByUsernameAsc(String role) {
        log.debug("Множественный поиск: пользователи с ролью {}", role);
        return userRepository.findByRoleOrderByUsernameAsc(role);
    }

    public List<User> findByFactoryTypeOrderByIdAsc(String factoryType) {
        log.debug("Множественный поиск: пользователи с фабрикой {}", factoryType);
        return userRepository.findByFactoryTypeOrderByIdAsc(factoryType);
    }

    public boolean existsUserByUsername(String username) {
        log.debug("Проверка существования: пользователь с именем {}", username);
        return userRepository.existsByUsername(username);
    }

    public boolean existsUserById(long userId) {
        log.debug("Проверка существования: пользователь с ID {}", userId);
        return userRepository.existsById(userId);
    }

    public List<User> findUsersByUsernameContaining(String username) {
        log.debug("Поиск по шаблону: пользователи с именем содержащим {}", username);
        return userRepository.findByUsernameContaining(username);
    }

    public List<User> findAllAdmins() {
        log.debug("Множественный поиск: все администраторы");
        return findByRoleOrderByUsernameAsc("ADMIN");
    }

    public List<User> findAllRegularUsers() {
        log.debug("Множественный поиск: все обычные пользователи");
        return findByRoleOrderByUsernameAsc("USER");
    }

    public List<User> findAllUsers() {
        log.debug("Получение всех записей: пользователи");
        return userRepository.findAll();
    }
}