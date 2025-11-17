package ru.ssau.tk.faible.labs.repos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.faible.labs.entity.FunctionEntity;
import ru.ssau.tk.faible.labs.entity.User;
import ru.ssau.tk.faible.labs.repository.FunctionRepository;
import ru.ssau.tk.faible.labs.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class FunctionRepositoryLogged {
    private static final Logger log = LoggerFactory.getLogger(FunctionRepositoryLogged.class);

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Одиночный поиск: функция по ID
     */
    public Optional<FunctionEntity> findFunctionById(long functionId) {
        log.debug("Одиночный поиск: функция с ID {}", functionId);
        return functionRepository.findById(functionId);
    }

    /**
     * Множественный поиск: все функции пользователя по ID пользователя
     */
    public List<FunctionEntity> findFunctionsByUserId(long userId) {
        log.debug("Множественный поиск: функции пользователя с ID {}", userId);
        return functionRepository.findByOwnerId(userId);
    }

    /**
     * Множественный поиск: все функции определенного типа
     */
    public List<FunctionEntity> findFunctionsByType(String type) {
        log.debug("Множественный поиск: функции типа {}", type);
        return functionRepository.findByType(type);
    }

    /**
     * Проверка существования: существует ли функция с именем
     */
    public boolean existsFunctionByName(String name) {
        log.debug("Проверка существования: функция с именем {}", name);
        return functionRepository.existsByName(name);
    }


    /**
     * Получение всех записей: все функции
     */
    public List<FunctionEntity> findAllFunctions() {
        log.debug("Получение всех записей: функции");
        return functionRepository.findAll();
    }

    /**
     * Множественный поиск: все функции ARRAY типа
     */
    public List<FunctionEntity> findAllArrayFunctions() {
        log.debug("Множественный поиск: все функции ARRAY типа");
        return findFunctionsByType("ARRAY");
    }

    /**
     * Множественный поиск: все функции LINKED_LIST типа
     */
    public List<FunctionEntity> findAllLinkedListFunctions() {
        log.debug("Множественный поиск: все функции LINKED_LIST типа");
        return findFunctionsByType("LINKED_LIST");
    }

    /**
     * Поиск владельца функции по ID функции
     */
    public Optional<User> findFunctionOwner(long functionId) {
        log.debug("Поиск владельца: функция с ID {}", functionId);
        Optional<FunctionEntity> function = functionRepository.findById(functionId);
        return function.map(FunctionEntity::getOwner);
    }

    /**
     * Множественный поиск: функции по имени пользователя
     */
    public List<FunctionEntity> findFunctionsByUsername(String username) {
        log.debug("Множественный поиск: функции пользователя с именем {}", username);
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(functionRepository::findByOwner)
                .orElse(List.of());
    }


}