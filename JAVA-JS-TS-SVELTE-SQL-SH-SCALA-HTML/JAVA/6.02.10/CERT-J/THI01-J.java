/*
Noncompliant Code Example
This noncompliant code example contains a NetworkHandler class that maintains a controller thread. The controller thread delegates each new request to a worker thread. To demonstrate the race condition in this example, the controller thread serves three requests by starting three threads in succession from its run() method. All threads are defined to belong to the Chief thread group.

This implementation contains a time-of-check, time-of-use (TOCTOU) vulnerability because it obtains the count and enumerates the list without ensuring atomicity. If one or more new requests were to occur after the call to activeCount() and before the call to enumerate() in the main() method, the total number of threads in the group would increase, but the enumerated list ta would contain only the initial number, that is, two thread references: main and controller. Consequently, the program would fail to account for the newly started threads in the Chief thread group.

Any subsequent use of the ta array would be insecure. For example, calling the destroy() method to destroy the thread group and its subgroups would not work as expected. The precondition to calling destroy() is that the thread group must be empty with no executing threads. The code attempts to comply with the precondition by interrupting every thread in the thread group. However, the thread group would not be empty when the destroy() method was called, causing a java.lang.IllegalThreadStateException to be thrown.
*/

final class HandleRequest implements Runnable {
  public void run() {
    // Do something
  }
}

public final class NetworkHandler implements Runnable {
  private static ThreadGroup tg = new ThreadGroup("Chief");

  @Override public void run() {
    new Thread(tg, new HandleRequest(), "thread1").start();
    new Thread(tg, new HandleRequest(), "thread2").start();
    new Thread(tg, new HandleRequest(), "thread3").start();
  }

  public static void printActiveCount(int point) {
    System.out.println("Active Threads in Thread Group " + tg.getName() +
        " at point(" + point + "):" + " " + tg.activeCount());
  }

  public static void printEnumeratedThreads(Thread[] ta, int len) {
    System.out.println("Enumerating all threads...");
    for (int i = 0; i < len; i++) {
      System.out.println("Thread " + i + " = " + ta[i].getName());
    }
  }

  public static void main(String[] args) throws InterruptedException {
    // Start thread controller
    Thread thread = new Thread(tg, new NetworkHandler(), "controller");
    thread.start();

    // Gets the active count (insecure)
    Thread[] ta = new Thread[tg.activeCount()];

    printActiveCount(1); // P1
    // Delay to demonstrate TOCTOU condition (race window)
    Thread.sleep(1000);
    // P2: the thread count changes as new threads are initiated
    printActiveCount(2);  
    // Incorrectly uses the (now stale) thread count obtained at P1
    int n = tg.enumerate(ta);  
    // Silently ignores newly initiated threads 
    printEnumeratedThreads(ta, n); 
                                   // (between P1 and P2)

    // This code destroys the thread group if it does 
    // not have any live threads
    for (Thread thr : ta) {
      thr.interrupt();
      while(thr.isAlive());
    }
    tg.destroy();
  }
}

