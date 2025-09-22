class MyFirstClass {
    public static void main(String[] s) {
        // создание и инициализация объекта "o" типа MySecondClass
        MySecondClass o = new MySecondClass(10, 2);
        
        System.out.println(o.calculate()); // вызов метода calculate
        
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                o.setA(i); // метод установки значения первого числового поля
                o.setB(j); // метод установки значения второго числового поля
                System.out.print(o.calculate()); // Расчет и вывод
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}

class MySecondClass {
    
    private int a;
    private int b;
    
    // геттеры
    public int getA() { return a; }
    public int getB() { return b; }
    
    // сеттеры
    public void setA(int a) { this.a = a; }
    public void setB(int b) { this.b = b; }
    
    // конструктор
    public MySecondClass(int a, int b) {
        this.a = a;
        this.b = b;
    }
    

    public int calculate() {
        return a / b;
    }
}