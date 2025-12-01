package ru.ssau.tk.faible.labs.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.faible.labs.entity.FunctionEntity;
import ru.ssau.tk.faible.labs.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/functions_db",
        "spring.datasource.username=postgres",
        "spring.datasource.password=nailamir",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect"
})
@Transactional
class FunctionRepositoryTest {

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private FunctionEntity testFunction1;
    private FunctionEntity testFunction2;

    @BeforeEach
    void setUp() {
        testUser = new User("function_owner", "password", "ARRAY", "USER");
        userRepository.save(testUser);

        testFunction1 = new FunctionEntity("Sine Function", "ARRAY", testUser);
        testFunction2 = new FunctionEntity("Cosine Function", "LINKED_LIST", testUser);

        functionRepository.save(testFunction1);
        functionRepository.save(testFunction2);
    }

    @Test
    void testSaveFunction() {
        FunctionEntity newFunction = new FunctionEntity("New Function", "ARRAY", testUser);
        FunctionEntity savedFunction = functionRepository.save(newFunction);

        assertNotNull(savedFunction.getId());
        assertEquals("New Function", savedFunction.getName());
        assertEquals("ARRAY", savedFunction.getType());
        assertEquals(testUser.getId(), savedFunction.getOwner().getId());
    }

    @Test
    void testFindByOwner() {
        List<FunctionEntity> userFunctions = functionRepository.findByOwner(testUser);
        assertEquals(2, userFunctions.size());
        assertTrue(userFunctions.stream().anyMatch(f -> f.getName().equals("Sine Function")));
        assertTrue(userFunctions.stream().anyMatch(f -> f.getName().equals("Cosine Function")));
    }

    @Test
    void testFindByOwnerId() {
        List<FunctionEntity> functions = functionRepository.findByOwnerId(testUser.getId());
        assertEquals(2, functions.size());
        assertTrue(functions.stream().allMatch(f -> f.getOwner().getId().equals(testUser.getId())));
    }

    @Test
    void testFindByType() {
        List<FunctionEntity> arrayFunctions = functionRepository.findByType("ARRAY");
        assertFalse(arrayFunctions.isEmpty());
        assertTrue(arrayFunctions.stream().allMatch(f -> "ARRAY".equals(f.getType())));
    }

    @Test
    void testFindByName() {
        List<FunctionEntity> foundFunctions = functionRepository.findByName("Sine Function");
        assertFalse(foundFunctions.isEmpty());
        assertEquals("Sine Function", foundFunctions.get(0).getName());
    }

    @Test
    void testDeleteFunction() {
        FunctionEntity functionToDelete = new FunctionEntity("To Delete", "ARRAY", testUser);
        FunctionEntity savedFunction = functionRepository.save(functionToDelete);

        functionRepository.deleteById(savedFunction.getId());
        assertFalse(functionRepository.findById(savedFunction.getId()).isPresent());
    }

    @Test
    void testUpdateFunction() {
        FunctionEntity function = functionRepository.findByName("Sine Function").get(0);
        function.setType("UPDATED_TYPE");

        FunctionEntity updatedFunction = functionRepository.save(function);
        assertEquals("UPDATED_TYPE", updatedFunction.getType());
        assertEquals("Sine Function", updatedFunction.getName());
    }

    @Test
    void testFindAllFunctions() {
        List<FunctionEntity> allFunctions = functionRepository.findAll();
        assertTrue(allFunctions.size() >= 2);
    }
}