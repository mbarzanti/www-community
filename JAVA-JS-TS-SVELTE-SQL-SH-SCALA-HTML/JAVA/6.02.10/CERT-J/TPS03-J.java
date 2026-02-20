/*
Noncompliant Code Example (Abnormal Task Termination)
This noncompliant code example consists of the PoolService class that encapsulates a thread pool and a runnable Task class. The Task.run() method can throw runtime exceptions, such as NullPointerException.
The task fails to notify the application when it terminates unexpectedly as a result of the runtime exception. Moreover, it lacks a recovery mechanism. Consequently, if Task were to throw a NullPointerException, the exception would be ignored.
*/


final class PoolService {
  private final ExecutorService pool = Executors.newFixedThreadPool(10);

  public void doSomething() {
    pool.execute(new Task());
  }
}

final class Task implements Runnable {
  @Override public void run() {
    // ...
    throw new NullPointerException();
    // ...
  }
}

