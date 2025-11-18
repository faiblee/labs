package ru.ssau.tk.faible.labs.repos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.faible.labs.entity.FunctionEntity;
import ru.ssau.tk.faible.labs.entity.PointEntity;
import ru.ssau.tk.faible.labs.repository.FunctionRepository;
import ru.ssau.tk.faible.labs.repository.PointRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class PointRepositoryLogged {
    private static final Logger log = LoggerFactory.getLogger(PointRepositoryLogged.class);

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private FunctionRepository functionRepository;

    public Optional<PointEntity> findPointById(long pointId) {
        log.debug("Одиночный поиск: точка с ID {}", pointId);
        return pointRepository.findById(pointId);
    }

    public List<PointEntity> findPointsByFunctionId(Long functionId) {
        log.debug("Множественный поиск: точки функции ID={}", functionId);
        return pointRepository.findByFunctionId(functionId);
    }

    public List<PointEntity> findAllPoints() {
        log.debug("Получение всех записей: точки");
        return pointRepository.findAll();
    }

    public List<PointEntity> findPointsByXValueBetween(double minX, double maxX) {
        log.debug("Множественный поиск: точки с X от {} до {}", minX, maxX);
        return pointRepository.findByxValueBetween(minX, maxX);
    }

    public List<PointEntity> findPointsByFunctionId(long functionId) {
        log.debug("Множественный поиск: точки функции с ID {}", functionId);
        return pointRepository.findByFunctionId(functionId);
    }

}