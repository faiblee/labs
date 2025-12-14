package ru.ssau.tk.faible.labs.DTO;

public class CreateFunctionDTO {
    private String name;
    private int ownerId;
    private String type;
    private double xFrom;
    private double xTo;
    private int count;
    private double constant;
    private String factory_type;

    public CreateFunctionDTO(String name, int ownerId, String type, double xFrom, double xTo, int count, double constant, String factory_type) {
        this.name = name;
        this.ownerId = ownerId;
        this.type = type;
        this.xFrom = xFrom;
        this.xTo = xTo;
        this.count = count;
        this.constant = constant;
        this.factory_type = factory_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int owner_id) {
        this.ownerId = owner_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getxFrom() {
        return xFrom;
    }

    public void setxFrom(double xFrom) {
        this.xFrom = xFrom;
    }

    public double getxTo() {
        return xTo;
    }

    public void setxTo(double xTo) {
        this.xTo = xTo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getConstant() {
        return constant;
    }

    public void setConstant(double constant) {
        this.constant = constant;
    }

    public String getFactory_type() {
        return factory_type;
    }

    public void setFactory_type(String factory_type) {
        this.factory_type = factory_type;
    }
}
