package ru.ssau.tk.faible.labs.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.faible.labs.config.DatabaseConfig;
import ru.ssau.tk.faible.labs.entity.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(DatabaseConfig.class)
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        // Генерация тестовых данных
        userRepository.deleteAll();
        testUser1 = new User("john_doe", "hash123", "ARRAY", "USER");
        testUser2 = new User("jane_smith", "hash456", "LINKED_LIST", "ADMIN");
        testUser3 = new User("bob_johnson", "hash789", "ARRAY", "USER");

        userRepository.save(testUser1);
        userRepository.save(testUser2);
        userRepository.save(testUser3);
    }

    @Test
    void testSaveUser() {
        User newUser = new User("new_user", "new_hash", "ARRAY", "USER");
        User savedUser = userRepository.save(newUser);

        assertNotNull(savedUser.getId());
        assertEquals("new_user", savedUser.getUsername());
        assertEquals("USER", savedUser.getRole());
    }

    @Test
    void testFindById() {
        Optional<User> foundUser = userRepository.findById(testUser1.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("john_doe", foundUser.get().getUsername());
    }

    @Test
    void testFindByUsername() {
        Optional<User> foundUser = userRepository.findByUsername("jane_smith");
        assertTrue(foundUser.isPresent());
        assertEquals("ADMIN", foundUser.get().getRole());
    }

    @Test
    void testExistsByUsername() {
        boolean exists = userRepository.existsByUsername("john_doe");
        boolean notExists = userRepository.existsByUsername("non_existent");
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testFindByRole() {
        List<User> users = userRepository.findByRoleOrderByUsernameAsc("USER");
        assertEquals(2, users.size());
        assertTrue(users.stream().allMatch(user -> "USER".equals(user.getRole())));
    }

    @Test
    void testFindByFactoryTypeOrderByIdAsc() {
        List<User> arrayUsers = userRepository.findByFactoryTypeOrderByIdAsc("ARRAY");
        assertEquals(2, arrayUsers.size());
        assertTrue(arrayUsers.stream().allMatch(user -> "ARRAY".equals(user.getFactoryType())));
    }

    @Test
    void testFindAllUsers() {
        List<User> allUsers = userRepository.findAll();
        assertTrue(allUsers.size() >= 3);
    }

    @Test
    void testDeleteUser() {
        User userToDelete = new User("to_delete", "hash", "ARRAY", "USER");
        User savedUser = userRepository.save(userToDelete);

        userRepository.deleteById(savedUser.getId());

        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testUpdateUser() {
        User user = userRepository.findByUsername("john_doe").get();
        user.setRole("SUPER_ADMIN");

        User updatedUser = userRepository.save(user);

        assertEquals("SUPER_ADMIN", updatedUser.getRole());
        assertEquals("john_doe", updatedUser.getUsername());
    }
    @Test
    void testCreateAndFindUser() {

        User user = new User("test_user_" + System.currentTimeMillis(),
                "password_hash", "ARRAY", "USER");

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());

        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getUsername(), foundUser.get().getUsername());
    }
}