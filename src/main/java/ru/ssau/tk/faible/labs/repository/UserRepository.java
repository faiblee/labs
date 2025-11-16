package ru.ssau.tk.faible.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.tk.faible.labs.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository <User, Long> {
    Optional<User> findByUsername(String username);
    boolean existByUsername(String username);
    List<User> findByRole(String role);
    List<User> findBeFactoryType(String factoryType);


}
