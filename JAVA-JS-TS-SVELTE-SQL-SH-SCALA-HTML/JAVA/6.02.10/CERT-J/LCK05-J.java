/*
Noncompliant Code Example
This noncompliant code example fails to synchronize access to the static counter field:
This class definition complies with VNA02-J. Ensure that compound operations on shared variables are atomic, which applies only to classes that promise thread-safety. However, this class has a mutable static counter field that is modified by the publicly accessible incrementCounter() method. Consequently, this class cannot be used securely by trusted client code because untrusted code can purposely fail to externally synchronize access to the field.
*/


/* This class is not thread-safe */
public final class CountHits {
  private static int counter;

  public void incrementCounter() {
    counter++;
  }
}


