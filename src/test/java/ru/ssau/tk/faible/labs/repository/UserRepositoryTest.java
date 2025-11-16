package ru.ssau.tk.faible.labs.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.ssau.tk.faible.labs.entity.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindUser(){
        User user = new User("tester", "hashed_password", "ARRAY", "ADMIN");

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("tester", savedUser.getUsername());
        assertEquals("ADMIN", savedUser.getRole());
    }

    @Test
    void testFindById() {
        // Генерация данных
        User user = new User("john", "pass123", "LINKED_LIST", "USER");
        User savedUser = entityManager.persist(user);
        entityManager.flush();

        // Поиск по ID
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Проверка
        assertTrue(foundUser.isPresent());
        assertEquals("john", foundUser.get().getUsername());
        assertEquals("USER", foundUser.get().getRole());
    }

    @Test
    void testFindByUsername() {
        // Генерация данных
        User user = new User("alice", "password", "ARRAY", "ADMIN");
        entityManager.persist(user);
        entityManager.flush();

        // Поиск по имени
        Optional<User> found = userRepository.findByUsername("alice");

        // Проверка
        assertTrue(found.isPresent());
        assertEquals("alice", found.get().getUsername());
    }

    @Test
    void testFindByRole() {
        // Генерация разнообразных данных
        User admin1 = new User("admin1", "pass1", "ARRAY", "ADMIN");
        User admin2 = new User("admin2", "pass2", "LINKED_LIST", "ADMIN");
        User user1 = new User("user1", "pass3", "ARRAY", "USER");

        entityManager.persist(admin1);
        entityManager.persist(admin2);
        entityManager.persist(user1);
        entityManager.flush();

        // Поиск по роли
        List<User> admins = userRepository.findByRole("ADMIN");

        // Проверка
        assertEquals(2, admins.size());
        assertTrue(admins.stream().allMatch(u -> "ADMIN".equals(u.getRole())));
    }





}