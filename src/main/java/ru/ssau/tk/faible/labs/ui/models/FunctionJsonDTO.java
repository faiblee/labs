package ru.ssau.tk.faible.labs.ui.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionJsonDTO {
    // Геттеры и сеттеры
    private String name;
    private String type;
    private List<Double> xValues;
    private List<Double> yValues;
}