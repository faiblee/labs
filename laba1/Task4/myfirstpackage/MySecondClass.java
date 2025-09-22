package myfirstpackage;

public class MySecondClass {
    private int a;
    private int b;
    public int getValueA() {
        return a;
    }
    public int getValueB() {
        return b;
    }
    public void setValueA(int value) {
        a = value;
    }
    public void setValueB(int value) {
        b = value;
    }
    public MySecondClass(int valueA, int valueB) {
        a = valueA;
        b = valueB;
    }
    public int sum() { 
        return a + b;
    }
}