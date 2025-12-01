package ru.ssau.tk.faible.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.faible.labs.entity.FunctionEntity;
import ru.ssau.tk.faible.labs.entity.PointEntity;
import ru.ssau.tk.faible.labs.entity.User;
import java.util.List;

@Repository
public interface FunctionRepository extends JpaRepository<FunctionEntity, Long> {

    List<FunctionEntity> findByOwner(User owner);

    List<FunctionEntity> findByOwnerId(Long ownerId);

    List<FunctionEntity> findByType(String type);

    List<FunctionEntity> findByName(String name);

    boolean existsByName(String name);
}
