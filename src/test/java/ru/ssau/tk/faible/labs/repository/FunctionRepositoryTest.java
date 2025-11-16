package ru.ssau.tk.faible.labs.repository;

import ru.ssau.tk.faible.labs.entity.FunctionEntity;
import ru.ssau.tk.faible.labs.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest

class FunctionRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Генерация тестового пользователя
        testUser = new User("testuser", "password", "ARRAY", "USER");
        entityManager.persist(testUser);
        entityManager.flush();
    }

    @Test
    void testSaveFunction() {
        // Генерация данных функции
        FunctionEntity function = new FunctionEntity("Test Function", "ARRAY", testUser);

        // Сохранение
        FunctionEntity savedFunction = functionRepository.save(function);

        // Проверка
        assertNotNull(savedFunction.getId());
        assertEquals("Test Function", savedFunction.getName());
        assertEquals("ARRAY", savedFunction.getType());
        assertEquals(testUser.getId(), savedFunction.getOwner().getId());
    }

    @Test
    void testFindByOwner() {
        // Генерация нескольких функций для пользователя
        FunctionEntity func1 = new FunctionEntity("Func1", "ARRAY", testUser);
        FunctionEntity func2 = new FunctionEntity("Func2", "LINKED_LIST", testUser);

        entityManager.persist(func1);
        entityManager.persist(func2);
        entityManager.flush();

        // Поиск функций пользователя
        List<FunctionEntity> userFunctions = functionRepository.findByOwner(testUser);

        // Проверка
        assertEquals(2, userFunctions.size());
        assertTrue(userFunctions.stream().anyMatch(f -> f.getName().equals("Func1")));
        assertTrue(userFunctions.stream().anyMatch(f -> f.getName().equals("Func2")));
    }

    @Test
    void testFindByOwnerId() {
        // Генерация данных
        FunctionEntity function = new FunctionEntity("My Function", "ARRAY", testUser);
        entityManager.persist(function);
        entityManager.flush();

        // Поиск по ID владельца
        List<FunctionEntity> functions = functionRepository.findByOwnerId(testUser.getId());

        // Проверка
        assertEquals(1, functions.size());
        assertEquals("My Function", functions.get(0).getName());
    }

    @Test
    void testFindByType() {
        // Генерация данных разных типов
        FunctionEntity arrayFunc = new FunctionEntity("Array Func", "ARRAY", testUser);
        FunctionEntity linkedFunc = new FunctionEntity("Linked Func", "LINKED_LIST", testUser);

        entityManager.persist(arrayFunc);
        entityManager.persist(linkedFunc);
        entityManager.flush();

        // Поиск по типу
        List<FunctionEntity> arrayFunctions = functionRepository.findByType("ARRAY");

        // Проверка
        assertEquals(1, arrayFunctions.size());
        assertEquals("Array Func", arrayFunctions.get(0).getName());
    }

    @Test
    void testFindByName() {
        // Генерация данных
        FunctionEntity function = new FunctionEntity("Sine Wave", "ARRAY", testUser);
        entityManager.persist(function);
        entityManager.flush();

        // Поиск по имени
        List<FunctionEntity> foundFunctions = functionRepository.findByName("Sine Wave");

        // Проверка
        assertEquals(1, foundFunctions.size());
        assertEquals("Sine Wave", foundFunctions.get(0).getName());
    }

    @Test
    void testDeleteFunction() {
        // Генерация данных
        FunctionEntity function = new FunctionEntity("To Delete", "ARRAY", testUser);
        FunctionEntity savedFunction = entityManager.persist(function);
        entityManager.flush();

        // Удаление
        functionRepository.deleteById(savedFunction.getId());

        // Проверка
        assertFalse(functionRepository.findById(savedFunction.getId()).isPresent());
    }

}