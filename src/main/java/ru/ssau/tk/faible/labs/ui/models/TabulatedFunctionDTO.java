package ru.ssau.tk.faible.labs.ui.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TabulatedFunctionDTO {
    private String name;
    private String type;
    private int ownerId;
    private double[] xValues;
    private double[] yValues;

}