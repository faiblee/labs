package ru.ssau.tk.faible.labs.database.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Point {
    private int id;
    private Double x_value;
    private Double y_value;
    private int function_id;
}
