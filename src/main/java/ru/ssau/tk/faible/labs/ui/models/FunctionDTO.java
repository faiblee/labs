package ru.ssau.tk.faible.labs.ui.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionDTO {
    private int id;
    private String name;
    private int ownerId;
    private String type;
}
