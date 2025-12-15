package ru.ssau.tk.faible.labs.database.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationResponseDTO {
    private double[] xvalues;
    private double[] yvalues;
}
