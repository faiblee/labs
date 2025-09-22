import myfirstpackage.*;

class MyFirstClass {
    public static void main(String[] args) {
        MySecondClass o = new MySecondClass(3, 5);
        System.out.println(o.sum());
        for (int i = 1; i <= 8; i++) {
 	    for (int j = 1; j <= 8; j++) {
     	        o.setValueA(i);
     	        o.setValueB(j);
     	        System.out.print(o.sum());
     	        System.out.print(" ");
 	    }
 	System.out.println();
        }
    }
}
