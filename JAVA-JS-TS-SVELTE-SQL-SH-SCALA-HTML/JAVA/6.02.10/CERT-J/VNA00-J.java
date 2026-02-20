/*
Noncompliant Code Example (Non-volatile Flag)
This noncompliant code example uses a shutdown() method to set the nonvolatile done flag that is checked in the run() method:

If one thread invokes the shutdown() method to set the flag, a second thread might not observe that change. Consequently, the second thread might observe that done is still false and incorrectly invoke the sleep() method. Compilers and just-in-time compilers (JITs) are allowed to optimize the code when they determine that the value of done is never modified by the same thread, resulting in an infinite loop.
*/
final class ControlledStop implements Runnable {
  private boolean done = false;
 
  @Override public void run() {
    while (!done) {
      try {
        // ...
        Thread.currentThread().sleep(1000); // Do something
      } catch(InterruptedException ie) { 
        Thread.currentThread().interrupt(); // Reset interrupted status
      } 
    } 	 
  }

  public void shutdown() {
    done = true;
  }
}
