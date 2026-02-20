/*
Noncompliant Code Example
This noncompliant code example results in the use of uninitialized data by the doLogic() method:
The doLogic() method is invoked from the superclass's constructor. When the superclass is constructed directly, the doLogic() method in the superclass is invoked and executes successfully. However, when the subclass initiates the superclass's construction, the subclass's doLogic() method is invoked instead. In this case, the value of color is still null because the subclass's constructor has not yet concluded.
*/

class SuperClass {
  public SuperClass () {
    doLogic();
  }

  public void doLogic() {
    System.out.println("This is superclass!");
  }
}

class SubClass extends SuperClass {
  private String color = "red";

  public void doLogic() {
    System.out.println("This is subclass! The color is :" + color);
    // ...
  }
}

public class Overridable {
  public static void main(String[] args) {
    SuperClass bc = new SuperClass();
    // Prints "This is superclass!"
    SuperClass sc = new SubClass();
    // Prints "This is subclass! The color is :null"
  }
}



