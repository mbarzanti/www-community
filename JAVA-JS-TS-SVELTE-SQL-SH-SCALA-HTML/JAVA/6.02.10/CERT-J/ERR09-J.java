/*
Noncompliant Code Example
This noncompliant code example uses System.exit() to forcefully shut down the JVM and terminate the running process. The program lacks a security manager; consequently, it lacks the capability to check whether the caller is permitted to invoke System.exit().
*/


public class InterceptExit {
  public static void main(String[] args) {
    // ...
    System.exit(1);  // Abrupt exit 
    System.out.println("This never executes");
  }
}	
