// src/main/java/ru/ssau/tk/faible/labs/ui/models/OperationRequestDTO.java

package ru.ssau.tk.faible.labs.ui.models;

public class OperationRequestDTO {
    private String operation; // "Сложение", "Вычитание", "Умножение", "Деление"
    private int function1Id;
    private int function2Id;
    private String resultName;

    // Геттеры и сеттеры
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public int getFunction1Id() { return function1Id; }
    public void setFunction1Id(int function1Id) { this.function1Id = function1Id; }

    public int getFunction2Id() { return function2Id; }
    public void setFunction2Id(int function2Id) { this.function2Id = function2Id; }

    public String getResultName() { return resultName; }
    public void setResultName(String resultName) { this.resultName = resultName; }
}