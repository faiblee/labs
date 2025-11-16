package ru.ssau.tk.faible.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.tk.faible.labs.entity.FunctionEntity;
import ru.ssau.tk.faible.labs.entity.User;
import java.util.List;

public interface FunctionRepository extends JpaRepository<FunctionEntity, Long> {

    List<FunctionEntity> findByOwner(User owner);
    List<FunctionEntity> findByOwnerId(Long ownerId);
    List<FunctionEntity> findByType(String type);
    List<FunctionEntity> findByName(String name);

}
