package ru.ssau.tk.faible.labs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import ru.ssau.tk.faible.labs.DTO.CreateFunctionDTO;
import ru.ssau.tk.faible.labs.DTO.FunctionDTO;
import ru.ssau.tk.faible.labs.entity.FunctionEntity;
import ru.ssau.tk.faible.labs.entity.PointEntity;
import ru.ssau.tk.faible.labs.entity.User;
import ru.ssau.tk.faible.labs.functions.*;
import ru.ssau.tk.faible.labs.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk.faible.labs.repository.FunctionRepository;
import ru.ssau.tk.faible.labs.repository.PointRepository;
import ru.ssau.tk.faible.labs.repository.UserRepository;
import ru.ssau.tk.faible.labs.service.SecurityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class FunctionController {

    private static final Logger log = LoggerFactory.getLogger(FunctionController.class);

    private final FunctionRepository functionRepository;
    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final SecurityService securityService;

    public FunctionController(
            FunctionRepository functionRepository,
            UserRepository userRepository, PointRepository pointRepository,
            SecurityService securityService) {
        this.functionRepository = functionRepository;
        this.userRepository = userRepository;
        this.pointRepository = pointRepository;
        this.securityService = securityService;
    }

    @GetMapping("/functions")
    public List<FunctionDTO> getFunctions(
            @RequestParam(required = false) Long ownerId) { // ownerId — необязательный параметр

        // если ownerId указан, то это запрос функций по владельцу
        if (ownerId != null) {
            // пользователь должен быть авторизован
            securityService.getCurrentUser();
            log.info("Fetching functions for owner ID: {}", ownerId);

            // Находим функции по ownerId и преобразуем в DTO
            return functionRepository.findByOwnerId(ownerId).stream()
                    .map(f -> new FunctionDTO(
                            f.getId(),
                            f.getName(),
                            f.getType(),
                            f.getOwner().getId() // только ID владельца
                    ))
                    .collect(Collectors.toList());

            // запрос всех функций (только для админов)
        } else {
            // проверка роли
            if (!securityService.isAdmin()) {
                throw new RuntimeException("Access denied: ADMIN only");
            }
            log.info("ADMIN requested all functions");

            // возвращаем все функции
            return functionRepository.findAll().stream()
                    .map(f -> new FunctionDTO(
                            f.getId(),
                            f.getName(),
                            f.getType(),
                            f.getOwner().getId()
                    ))
                    .collect(Collectors.toList());
        }
    }

    @ResponseStatus(HttpStatus.CREATED) // устанавливает HTTP-статус 201
    @PostMapping("/functions")
    public FunctionDTO createFunction(@RequestBody CreateFunctionDTO dto) {
        // получаем текущего авторизованного пользователя (владельца)
        User owner = securityService.getCurrentUser();

        // создаём новую сущность FunctionEntity
        FunctionEntity functionEnt = new FunctionEntity(
                dto.getName(),
                dto.getType(),
                owner // владелец — текущий пользователь
        );
        // Сохраняем в базу данных
        FunctionEntity func = functionRepository.save(functionEnt);

        String factory_type = dto.getFactory_type();
        String type = dto.getType();
        if (!type.isEmpty() && !type.equals("Tabulated")) {
            double xFrom = dto.getxFrom();
            double xTo = dto.getxTo();
            int count = dto.getCount();

            TabulatedFunctionFactory factory;
            if (factory_type.equals("array")) {
                factory = new ArrayTabulatedFunctionFactory();
            } else {
                factory = new LinkedListTabulatedFunctionFactory();
            }
            Map<String, MathFunction> functions = new HashMap<>();
            functions.put("Квадратичная функция", new SqrFunction());
            functions.put("Тождественная функция", new IdentityFunction());
            functions.put("Константная функция", new ConstantFunction(dto.getConstant()));
            functions.put("Функция с константой 0", new ZeroFunction());
            functions.put("Функция с константой 1", new UnitFunction());

            TabulatedFunction function = factory.create(functions.get(type), xFrom, xTo, count);

            for (ru.ssau.tk.faible.labs.functions.Point point : function) {
                pointRepository.save(new PointEntity(point.x, point.y, func));
            }
        }

        // логируем успешное создание
        log.info("User {} created function {}", owner.getUsername(), functionEnt.getName());

        // Возвращаем DTO с присвоенным ID
        return new FunctionDTO(
                functionEnt.getId(),
                functionEnt.getName(),
                functionEnt.getType(),
                owner.getId()
        );
    }

    @GetMapping("/functions/{id}")
    public FunctionDTO getFunction(@PathVariable Long id) {
        // Проверка авторизации
        securityService.getCurrentUser();

        // Находим функцию по ID или выбрасываем исключение
        FunctionEntity f = functionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Function not found"));

        log.info("Accessed function ID: {}", id);

        // Возвращаем DTO
        return new FunctionDTO(
                f.getId(),
                f.getName(),
                f.getType(),
                f.getOwner().getId()
        );
    }


    @PutMapping("/functions/{id}")
    public FunctionDTO updateFunction(
            @PathVariable Long id,
            @RequestBody FunctionDTO dto) {

        // Получаем текущего пользователя.
        User currentUser = securityService.getCurrentUser();
        FunctionEntity f = functionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Function not found"));

        // Проверка прав: только владелец или ADMIN
        if (!securityService.isAdmin() && !f.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Обновляем поля
        f.setName(dto.getName());
        f.setType(dto.getType());

        // сохраняем изменения
        functionRepository.save(f);

        log.info("User {} updated function {}", currentUser.getUsername(), f.getName());

        return new FunctionDTO(
                f.getId(),
                f.getName(),
                f.getType(),
                f.getOwner().getId()
        );
    }

    // Устанавливает HTTP-статус 204
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/functions/{id}")
    public void deleteFunction(@PathVariable Long id) {
        // Проверка авторизации
        User currentUser = securityService.getCurrentUser();

        // Находим функцию по ID или бросаем исключение, если не найдена
        FunctionEntity f = functionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Function not found"));

        // Проверка прав
        if (!securityService.isAdmin() && !f.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Удаляем из базы данных
        functionRepository.delete(f);

        log.info("User {} deleted function ID: {}", currentUser.getUsername(), id);
    }
}