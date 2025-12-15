package ru.ssau.tk.faible.labs.ui.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ssau.tk.faible.labs.ui.models.FunctionJsonDTO;
import ru.ssau.tk.faible.labs.ui.models.PointDTO;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class JsonFileHandler {
    private static final ObjectMapper mapper = new ObjectMapper();

    // Сериализация: возвращает JSON-строку для скачивания
    public static String serializeFunction(String name, String type, List<PointDTO> points) throws Exception {
        FunctionJsonDTO dto = new FunctionJsonDTO();
        dto.setName(name);
        dto.setType(type);
        dto.setXValues(points.stream().map(PointDTO::getXValue).collect(Collectors.toList()));
        dto.setYValues(points.stream().map(PointDTO::getYValue).collect(Collectors.toList()));
        return mapper.writeValueAsString(dto);
    }

    // Десериализация: из InputStream (загруженного файла) в FunctionJsonDTO
    public static FunctionJsonDTO deserializeFunction(java.io.Reader reader) throws Exception {
        return mapper.readValue(reader, FunctionJsonDTO.class);
    }
}