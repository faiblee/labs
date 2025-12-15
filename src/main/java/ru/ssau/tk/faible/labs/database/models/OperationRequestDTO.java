package ru.ssau.tk.faible.labs.database.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationRequestDTO {
    private String operation; // "Сложение", "Вычитание", "Умножение", "Деление"
    private int function1Id;
    private int function2Id;
    private String resultName;
}
