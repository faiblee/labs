package ru.ssau.tk.faible.labs.ui.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateFunctionDTO {
    private String name;
    private int ownerId;
    private String type;
    private double xFrom;
    private double xTo;
    private int count;
    private double constant;
    private String factory_type;
}
