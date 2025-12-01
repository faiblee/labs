package ru.ssau.tk.faible.labs.DTO;

public class PointDTO {
    private Long id;
    private Double xValue;
    private Double yValue;
    private Long functionId;

    public PointDTO() {}

    public PointDTO(Long id, Double xValue, Double yValue, Long functionId) {
        this.id = id;
        this.xValue = xValue;
        this.yValue = yValue;
        this.functionId = functionId;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getXValue() { return xValue; }
    public void setXValue(Double xValue) { this.xValue = xValue; }

    public Double getYValue() { return yValue; }
    public void setYValue(Double yValue) { this.yValue = yValue; }

    public Long getFunctionId() { return functionId; }
    public void setFunctionId(Long functionId) { this.functionId = functionId; }
}