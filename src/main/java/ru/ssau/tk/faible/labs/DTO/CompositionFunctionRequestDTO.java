package ru.ssau.tk.faible.labs.DTO;

public class CompositionFunctionRequestDTO {
    private String name;
    private Long outerFunctionId;
    private Long innerFunctionId;
    private Double xFrom; // Диапазон для вычисления композиции
    private Double xTo;
    private Integer count; // Количество точек

    // Геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getOuterFunctionId() { return outerFunctionId; }
    public void setOuterFunctionId(Long outerFunctionId) { this.outerFunctionId = outerFunctionId; }

    public Long getInnerFunctionId() { return innerFunctionId; }
    public void setInnerFunctionId(Long innerFunctionId) { this.innerFunctionId = innerFunctionId; }

    public Double getXFrom() { return xFrom; }
    public void setXFrom(Double xFrom) { this.xFrom = xFrom; }

    public Double getXTo() { return xTo; }
    public void setXTo(Double xTo) { this.xTo = xTo; }

    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
}