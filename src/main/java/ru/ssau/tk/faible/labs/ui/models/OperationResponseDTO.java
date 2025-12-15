package ru.ssau.tk.faible.labs.ui.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationResponseDTO {
    private double[] xvalues;
    private double[] yvalues;
}
