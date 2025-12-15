package ru.ssau.tk.faible.labs.DTO;

public class CreateFunctionDTO {
    private String name;
    private int ownerId;
    private String type;
    private double xfrom;
    private double xto;
    private int count;
    private double constant;
    private String factory_type;
    private double[] xvalues;
    private double[] yvalues;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getXfrom() {
        return xfrom;
    }

    public void setXfrom(double xfrom) {
        this.xfrom = xfrom;
    }

    public double getXto() {
        return xto;
    }

    public void setXto(double xto) {
        this.xto = xto;
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

    public double[] getXvalues() {
        return xvalues;
    }

    public void setXvalues(double[] xvalues) {
        this.xvalues = xvalues;
    }

    public double[] getYvalues() {
        return yvalues;
    }

    public void setYvalues(double[] yvalues) {
        this.yvalues = yvalues;
    }
}
