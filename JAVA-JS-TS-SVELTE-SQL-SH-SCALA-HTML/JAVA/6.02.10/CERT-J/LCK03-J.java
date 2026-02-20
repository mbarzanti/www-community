/*
Noncompliant Code Example (ReentrantLock)
The doSomething() method in this noncompliant code example synchronizes on the intrinsic lock of an instance of ReentrantLock rather than on the reentrant mutual exclusion Lock encapsulated by ReentrantLock.

*/

private final Lock lock = new ReentrantLock();

public void doSomething() {
  synchronized(lock) {
    // ...
  }
}
