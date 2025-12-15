package ru.ssau.tk.faible.labs.DTO;

public class OperationResponseDTO {
    double[] xvalues;
    double[] yvalues;

    public OperationResponseDTO() {
    }

    public OperationResponseDTO(double[] xvalues, double[] yvalues) {
        this.xvalues = xvalues;
        this.yvalues = yvalues;
    }

    public double[] getxvalues() {
        return xvalues;
    }

    public void setxvalues(double[] xvalues) {
        this.xvalues = xvalues;
    }

    public double[] getyvalues() {
        return yvalues;
    }

    public void setyvalues(double[] yvalues) {
        this.yvalues = yvalues;
    }
}
