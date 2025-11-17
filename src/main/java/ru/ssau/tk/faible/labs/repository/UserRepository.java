package ru.ssau.tk.faible.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.faible.labs.entity.User;

import java.util.List;
import java.util.Optional;
public interface UserRepository extends JpaRepository <User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
    List<User> findByRoleOrderByUsernameAsc(String role);
    List<User> findByFactoryTypeOrderByIdAsc(String factoryType);
    List<User> findByUsernameContaining(String username);
}
