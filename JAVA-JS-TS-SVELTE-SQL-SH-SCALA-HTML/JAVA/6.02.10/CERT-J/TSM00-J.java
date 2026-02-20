/*
Noncompliant Code Example (Synchronized Method)
This noncompliant code example overrides the synchronized doSomething() method in the Base class with an unsynchronized method in the Derived class:

The doSomething() method of the Base class can be safely used by multiple threads, but instances of the Derived subclass cannot.

This programming error can be difficult to diagnose because threads that accept instances of Base can also accept instances of its subclasses. Consequently, clients could be unaware that they are operating on a thread-unsafe instance of a subclass of a thread-safe class.
*/

class Base {
  public synchronized void doSomething() {
    // ...
  }
}

class Derived extends Base {
  @Override public void doSomething() {
    // ...
  }
}
