package ru.ssau.tk.faible.labs.ui.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointDTO {
    private int id;
    private double xValue;
    private double yValue;
    private int functionId;
}