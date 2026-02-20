/*
Noncompliant Code Example (Deprecated Thread.stop())
This noncompliant code example shows a thread that fills a vector with pseudorandom numbers. The thread is forcefully stopped after a given amount of time.

Because the Vector class is thread-safe, operations performed by multiple threads on its shared instance are expected to leave it in a consistent state. For instance, the Vector.size() method always returns the correct number of elements in the vector, even after concurrent changes to the vector, because the vector instance uses its own intrinsic lock to prevent other threads from accessing it while its state is temporarily inconsistent.

However, the Thread.stop() method causes the thread to stop what it is doing and throw a ThreadDeath exception. All acquired locks are subsequently released [API 2014]. If the thread were in the process of adding a new integer to the vector when it was stopped, the vector would become accessible while it is in an inconsistent state. For example, this could result in Vector.size() returning an incorrect element count because the element count is incremented after adding the element.
*/

public final class Container implements Runnable {
  private final Vector<Integer> vector = new Vector<Integer>(1000);

  public Vector<Integer> getVector() {
    return vector;
  }

  @Override public synchronized void run() {
    Random number = new Random(123L);
    int i = vector.capacity();
    while (i > 0) {
      vector.add(number.nextInt(100));
      i--;
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Thread thread = new Thread(new Container());
    thread.start();
    Thread.sleep(5000);
    thread.stop();
  }
}
