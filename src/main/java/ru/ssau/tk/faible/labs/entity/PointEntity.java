package ru.ssau.tk.faible.labs.entity;

import javax.persistence.*;

@Entity
@Table(name = "point")
public class PointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "x_value", nullable = false)
    private Double xValue;

    @Column(name = "y_value", nullable = false)
    private Double yValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "function_id", nullable = false)
    private FunctionEntity function;

    public PointEntity() {}

    public PointEntity(Double xValue, Double yValue, FunctionEntity function) {
        this.xValue = xValue;
        this.yValue = yValue;
        this.function = function;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getXValue() { return xValue; }
    public void setXValue(Double xValue) { this.xValue = xValue; }

    public Double getYValue() { return yValue; }
    public void setYValue(Double yValue) { this.yValue = yValue; }

    public FunctionEntity getFunction() { return function; }
    public void setFunction(FunctionEntity function) { this.function = function; }
}