/*
Noncompliant Code Example
This noncompliant code example demonstrates how a malicious subclass Sub can both override the doLogic() method of the superclass and increase the accessibility of the overriding method. Any user of Sub can invoke the doLogic method because the base class Super defines it to be protected, consequently allowing class Sub to increase the accessibility of doLogic() by declaring its own version of the method to be public.

*/

class Super {
  protected void doLogic() {
    System.out.println("Super invoked");
  }
}

public class Sub extends Super {
  public void doLogic() {
    System.out.println("Sub invoked");
    // Do sensitive operations
  }
}


