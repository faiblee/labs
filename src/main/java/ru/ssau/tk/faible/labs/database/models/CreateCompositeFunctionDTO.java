package ru.ssau.tk.faible.labs.database.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompositeFunctionDTO {
    String name;
    int outerFunctionId;
    int innerFunctionId;
}
