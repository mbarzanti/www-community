/*
Noncompliant Code Example (Nonstatic Lock Object for Static Data)
This noncompliant code example attempts to guard access to the static counter field using a nonstatic lock object. When two Runnable tasks are started, they create two instances of the lock object and lock on each instance separately.
This example fails to prevent either thread from observing an inconsistent value of counter because the increment operation on volatile fields fails to be atomic in the absence of proper synchronization (see VNA02-J. Ensure that compound operations on shared variables are atomic for more information).
*/

public final class CountBoxes implements Runnable {
  private static volatile int counter;
  // ...
  private final Object lock = new Object();

  @Override public void run() {
    synchronized (lock) {
      counter++;
      // ...
    }
  }

  public static void main(String[] args) {
    for (int i = 0; i < 2; i++) {
    new Thread(new CountBoxes()).start();
    }
  }
}


/*
Noncompliant Code Example (Method Synchronization for Static Data)
This noncompliant code example uses method synchronization to protect access to a static class counter field:
In this case, the method synchronization uses the intrinsic lock associated with each instance of the class rather than the intrinsic lock associated with the class itself. Consequently, threads constructed using different Runnable instances may observe inconsistent values of counter.
*/

public final class CountBoxes implements Runnable {
  private static volatile int counter;
  // ...

  public synchronized void run() {
    counter++;
    // ...
  }
  // ...
}



