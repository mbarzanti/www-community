/*
Noncompliant Code Example (notify())
This noncompliant code example shows a complex, multistep process being undertaken by several threads. Each thread executes the step identified by the time field. Each thread waits for the time field to indicate that it is time to perform the corresponding thread's step. After performing the step, each thread first increments time and then notifies the thread that is responsible for the next step.

This noncompliant code example violates the liveness property. Each thread has a different condition predicate because each requires step to have a different value before proceeding. The Object.notify() method wakes only one thread at a time. Unless it happens to wake the thread that is required to perform the next step, the program will deadlock.
*/

public final class ProcessStep implements Runnable {
  private static final Object lock = new Object();
  private static int time = 0;
  private final int step; // Do Perform operations when field time 
                          // reaches this value

  public ProcessStep(int step) {
    this.step = step;
  }

  @Override public void run() {
    try {
      synchronized (lock) {
        while (time != step) {
          lock.wait();
        }

        // Perform operations

        time++;
        lock.notify();
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt(); // Reset interrupted status
    }
  }

  public static void main(String[] args) {
    for (int i = 4; i >= 0; i--) {
      new Thread(new ProcessStep(i)).start();
    }
  }
}
/*
Noncompliant Code Example (Condition Interface)
This noncompliant code example is similar to the noncompliant code example for notify() but uses the Condition interface for waiting and notification:

As with Object.notify(), the signal() method may awaken an arbitrary thread.
*/

public class ProcessStep implements Runnable {
  private static final Lock lock = new ReentrantLock();
  private static final Condition condition = lock.newCondition();
  private static int time = 0;
  private final int step; // Perform operations when field time 
                          // reaches this value
  public ProcessStep(int step) {
    this.step = step;
  }

  @Override public void run() {
    lock.lock();
    try {
      while (time != step) {
        condition.await();
      }

      // Perform operations

      time++;
      condition.signal();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt(); // Reset interrupted status
    } finally {
      lock.unlock();
    }
  }

  public static void main(String[] args) {
    for (int i = 4; i >= 0; i--) {
      new Thread(new ProcessStep(i)).start();
    }
  }
}
