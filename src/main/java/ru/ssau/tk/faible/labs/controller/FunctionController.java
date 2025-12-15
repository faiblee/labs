package ru.ssau.tk.faible.labs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.ssau.tk.faible.labs.DTO.CompositionFunctionRequestDTO;
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

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class FunctionController {

    private static final Logger log = LoggerFactory.getLogger(FunctionController.class);

    private final FunctionRepository functionRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final PointRepository pointRepository;

    public FunctionController(
            FunctionRepository functionRepository,
            UserRepository userRepository,
            SecurityService securityService, PointRepository pointRepository) {
        this.functionRepository = functionRepository;
        this.userRepository = userRepository;
        this.securityService = securityService;
        this.pointRepository = pointRepository;
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

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/functions/{functionId}/apply")
    public Double apply(@PathVariable Long functionId, @RequestParam Double x) {
        if (functionId == null || x == null)  {
            throw new IllegalArgumentException("Illegal arguments");
        }

        FunctionEntity function = functionRepository.findById(functionId).orElseThrow(() -> new IllegalArgumentException("Function not found"));

        List<PointEntity> points = pointRepository.findByFunctionOrderByXValueAsc(function);

        List<Double> xValuesList = new LinkedList<>();
        List<Double> yValuesList = new LinkedList<>();
        for (PointEntity point : points) {
            xValuesList.add(point.getXValue());
            yValuesList.add(point.getYValue());
        }

        double[] xValues = xValuesList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] yValues = yValuesList.stream().mapToDouble(Double::doubleValue).toArray();

        TabulatedFunction tabulatedFunction = new ArrayTabulatedFunction(xValues, yValues);

        return tabulatedFunction.apply(x);
    }

    @ResponseStatus(HttpStatus.CREATED) // устанавливает HTTP-статус 201
    @PostMapping("/functions")
    public FunctionDTO createFunction(@RequestBody CreateFunctionDTO dto) {
        // получаем текущего авторизованного пользователя (владельца)
        User owner = securityService.getCurrentUser();

        log.info("Создаем функцию для user = {}", owner.getUsername());

        // создаём новую сущность FunctionEntity
        FunctionEntity functionEnt = new FunctionEntity(
                dto.getName(),
                dto.getType(),
                owner // владелец — текущий пользователь
        );
        // Сохраняем в базу данных
        FunctionEntity func = functionRepository.save(functionEnt);

        log.info("Сохранена функция с именем {}", func.getName());

        String factory_type = dto.getFactory_type();
        String type = dto.getType();
        TabulatedFunctionFactory factory;

        if (factory_type.equals("array")) {
            factory = new ArrayTabulatedFunctionFactory();
        } else {
            factory = new LinkedListTabulatedFunctionFactory();
        }


        if (!type.isEmpty() && !type.equals("Табулированная функция")) {
            double xFrom = dto.getXfrom();
            double xTo = dto.getXto();
            int count = dto.getCount();


            Map<String, MathFunction> functions = new HashMap<>();
            functions.put("Квадратичная функция", new SqrFunction());
            functions.put("Тождественная функция", new IdentityFunction());
            functions.put("Константная функция", new ConstantFunction(dto.getConstant()));
            functions.put("Функция с константой 0", new ZeroFunction());
            functions.put("Функция с константой 1", new UnitFunction());

            TabulatedFunction function = factory.create(functions.get(type), xFrom, xTo, count);

            for (ru.ssau.tk.faible.labs.functions.Point point : function) {
                log.info("Создаем точку со значениями ({}, {})", point.x, point.y);
                pointRepository.save(new PointEntity(point.x, point.y, func));
            }
        } else {
            double[] xValues = dto.getXvalues();
            double[] yValues = dto.getYvalues();

            TabulatedFunction function = factory.create(xValues, yValues);

            for (ru.ssau.tk.faible.labs.functions.Point point : function) {
                log.info("Создаем точку со значениями ({}, {})", point.x, point.y);
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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/functions/composition")
    public void createCompositionFunction(
            @RequestBody CompositionFunctionRequestDTO compositeFunctionDTO) {


        User currentUser = securityService.getCurrentUser();
        log.info("User {} is creating a composition function: {}", currentUser.getUsername(), compositeFunctionDTO.getName());

        FunctionEntity func = new FunctionEntity(compositeFunctionDTO.getName(), "Сложная функция", currentUser);

        FunctionEntity function = functionRepository.save(func);

        Long innerFunctionId = compositeFunctionDTO.getInnerFunctionId();
        Long outerFunctionId = compositeFunctionDTO.getOuterFunctionId();

        FunctionEntity innerFunction = functionRepository.findById(innerFunctionId)
                .orElseThrow(() -> new RuntimeException("Inner function not found"));

        FunctionEntity outerFunction = functionRepository.findById(outerFunctionId)
                .orElseThrow(() -> new RuntimeException("Outer function not found"));

        // 3. Загрузить точки обеих функций
        List<PointEntity> outerPoints = pointRepository.findByFunctionId(outerFunctionId);
        List<PointEntity> innerPoints = pointRepository.findByFunctionId(innerFunctionId);

        log.info("Точек в outer = {}", outerPoints.size());
        log.info("Точек в inner = {}", innerPoints.size());

        List<Double> xInnerValues = new LinkedList<>();
        List<Double> yInnerValues = new LinkedList<>();
        List<Double> xOuterValues = new LinkedList<>();
        List<Double> yOuterValues = new LinkedList<>();

        for (PointEntity point : innerPoints) {
            xInnerValues.add(point.getXValue());
            yInnerValues.add(point.getYValue());
        }
        for (PointEntity point : outerPoints) {
            xOuterValues.add(point.getXValue());
            yOuterValues.add(point.getYValue());
        }

        log.info("Массивы точек: xInnerValues - {}, yInnerValues - {}, xOuterValues - {}, yOuterValues - {}", xInnerValues, yInnerValues, xOuterValues, yOuterValues);

        TabulatedFunctionFactory factory;

        if ("array".equals(currentUser.getFactoryType())) {
            factory = new ArrayTabulatedFunctionFactory();
        } else {
            factory = new LinkedListTabulatedFunctionFactory();
        }

        double[] xInnerValuesArray = xInnerValues.stream().mapToDouble(Double::doubleValue).toArray();
        double[] yInnerValuesArray = yInnerValues.stream().mapToDouble(Double::doubleValue).toArray();
        double[] xOuterValuesArray = xOuterValues.stream().mapToDouble(Double::doubleValue).toArray();
        double[] yOuterValuesArray = yOuterValues.stream().mapToDouble(Double::doubleValue).toArray();

        TabulatedFunction innerFunctionTabulated = factory.create(xInnerValuesArray, yInnerValuesArray);
        TabulatedFunction outerFunctionTabulated = factory.create(xOuterValuesArray, yOuterValuesArray);

        log.info("Созданы 2 функции успешно");

        CompositeTabulatedFunction compositeFunction = new CompositeTabulatedFunction(innerFunctionTabulated, outerFunctionTabulated);

        log.info("создана композитная функция");

        for (ru.ssau.tk.faible.labs.functions.Point point : compositeFunction) {
            PointEntity pointEntity = new PointEntity(point.x, point.y, function);
            pointRepository.save(pointEntity);
        }

        log.info("Сложная функция успешно добавлена");
    }
    private FunctionDTO toDto(FunctionEntity entity) {
        return new FunctionDTO(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                entity.getOwner().getId()
        );
    }
}