package ru.ssau.tk.faible.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.faible.labs.entity.FunctionEntity;
import ru.ssau.tk.faible.labs.entity.PointEntity;

import java.util.List;


public interface PointRepository extends JpaRepository<PointEntity, Long> {
    List<PointEntity> findByFunction(FunctionEntity function);
    List<PointEntity> findByFunctionId(Long functionId);
    void deleteByFunction(FunctionEntity function);
    List<PointEntity> findByxValueBetween(Double minX, Double maxX);



}
