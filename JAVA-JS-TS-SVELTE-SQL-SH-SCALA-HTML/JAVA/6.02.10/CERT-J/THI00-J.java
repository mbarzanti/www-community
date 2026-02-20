/*
Noncompliant Code Example
This noncompliant code example explicitly invokes run() in the context of the current thread:
The newly created thread is never started because of the incorrect assumption that run() starts the new thread. Consequently, the statements in the run() method are executed by the current thread rather than by the new thread.
*/

public final class Foo implements Runnable {
  @Override public void run() {
    // ...
  }

  public static void main(String[] args) {
    Foo foo = new Foo();
    new Thread(foo).run();
  }
}
