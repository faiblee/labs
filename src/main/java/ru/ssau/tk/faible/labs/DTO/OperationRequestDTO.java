package ru.ssau.tk.faible.labs.DTO;

public class OperationRequestDTO {
    private String operation; // "Сложение", "Вычитание", "Умножение", "Деление"
    private Long function1Id;
    private Long function2Id;

    // Геттеры и сеттеры
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public Long getFunction1Id() { return function1Id; }
    public void setFunction1Id(Long function1Id) { this.function1Id = function1Id; }

    public Long getFunction2Id() { return function2Id; }
    public void setFunction2Id(Long function2Id) { this.function2Id = function2Id; }
}