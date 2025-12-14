package ru.ssau.tk.faible.labs.DTO;

public class CompositionFunctionRequestDTO {
    private String name;
    private Long outerFunctionId; // f(x)
    private Long innerFunctionId; // g(x)

    public CompositionFunctionRequestDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getOuterFunctionId() { return outerFunctionId; }
    public void setOuterFunctionId(Long outerFunctionId) { this.outerFunctionId = outerFunctionId; }

    public Long getInnerFunctionId() { return innerFunctionId; }
    public void setInnerFunctionId(Long innerFunctionId) { this.innerFunctionId = innerFunctionId; }
}