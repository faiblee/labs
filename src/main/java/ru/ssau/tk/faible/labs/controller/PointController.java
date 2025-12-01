package ru.ssau.tk.faible.labs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.faible.labs.DTO.PointDTO;
import ru.ssau.tk.faible.labs.entity.FunctionEntity;
import ru.ssau.tk.faible.labs.entity.PointEntity;
import ru.ssau.tk.faible.labs.entity.User;
import ru.ssau.tk.faible.labs.repository.FunctionRepository;
import ru.ssau.tk.faible.labs.repository.PointRepository;
import ru.ssau.tk.faible.labs.service.SecurityService;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final PointRepository pointRepository;
    private final FunctionRepository functionRepository;
    private final SecurityService securityService;

    public PointController(
            PointRepository pointRepository,
            FunctionRepository functionRepository,
            SecurityService securityService
    ) {
        this.pointRepository = pointRepository;
        this.functionRepository = functionRepository;
        this.securityService = securityService;
    }

    @GetMapping("/points/{id}")
    public PointDTO getPoint(@PathVariable Long id) {
        // Проверка, пользователь должен быть авторизован
        securityService.getCurrentUser();

        // Находим точку или бросаем исключение
        PointEntity point = pointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Point not found with id: " + id));

        log.info("Retrieved point ID: {}", id);

        // Возвращаем DTO
        return toDto(point);
    }

    @GetMapping("/functions/{functionId}/points")
    public List<PointDTO> getPointsByFunction(@PathVariable Long functionId) {
        // Проверка авторизации
        securityService.getCurrentUser();

        // Получаем точки по ID функции
        List<PointEntity> points = pointRepository.findByFunctionId(functionId);

        log.info("Retrieved {} points for function ID: {}", points.size(), functionId);

        // Преобразуем в список DTO
        return points.stream()
                .map(this::toDto)
                .collect(Collectors.toList()); // Собирает все DTO из потока обратно в список
    }

    // Устанавливаем статус 201
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/functions/{functionId}/points")
    public PointDTO addPoint(
            @PathVariable Long functionId,
            @RequestBody PointDTO dto
    ) {
        // Получаем текущего пользователя
        User currentUser = securityService.getCurrentUser();

        // Находим функцию, к которой привязываем точку
        FunctionEntity function = functionRepository.findById(functionId)
                .orElseThrow(() -> new RuntimeException("Function not found with id: " + functionId));

        // Проверка прав, только владелец функции или ADMIN
        if (!securityService.isAdmin() && !function.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied: only owner or ADMIN can add points");
        }

        // Создаём новую точку
        PointEntity point = new PointEntity(
                dto.getXValue(),
                dto.getYValue(),
                function
        );

        // Сохраняем в базу данных
        pointRepository.save(point);

        log.info("User {} added point ID {} to function ID {}",
                currentUser.getUsername(), point.getId(), functionId);

        return toDto(point);
    }

    @PutMapping("/points/{id}")
    public PointDTO updatePoint(
            @PathVariable Long id,
            @RequestBody PointDTO dto
    ) {
        User currentUser = securityService.getCurrentUser();
        PointEntity point = pointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Point not found with id: " + id));

        // Получаем функцию, к которой привязана точка
        FunctionEntity function = point.getFunction();

        // Проверка прав
        if (!securityService.isAdmin() && !function.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied: only owner or ADMIN can update this point");
        }

        // Обновляем координаты
        point.setXValue(dto.getXValue());
        point.setYValue(dto.getYValue());
        pointRepository.save(point);

        log.info("User {} updated point ID {}", currentUser.getUsername(), id);

        return toDto(point);
    }

    // Устанавливаем статус 204
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/points/{id}")
    public void deletePoint(@PathVariable Long id) {
        User currentUser = securityService.getCurrentUser();
        PointEntity point = pointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Point not found with id: " + id));

        FunctionEntity function = point.getFunction();

        // проверка прав
        if (!securityService.isAdmin() && !function.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied: only owner or ADMIN can delete this point");
        }

        // Удаляем точку
        pointRepository.deleteById(id);

        log.info("User {} deleted point ID {}", currentUser.getUsername(), id);

    }

    private PointDTO toDto(PointEntity entity) {
        return new PointDTO(
                entity.getId(),
                entity.getXValue(),
                entity.getYValue(),
                entity.getFunction().getId()
        );
    }
}