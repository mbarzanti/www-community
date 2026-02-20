/*
Noncompliant Code Example
This noncompliant code example simply prints the exception's stack trace:
*/


try {
  //...
} catch (IOException ioe) {
  ioe.printStackTrace();
}

/*
Noncompliant Code Example
If a thread is interrupted while sleeping or waiting, it causes a java.lang.InterruptedException to be thrown. However, the run() method of interface Runnable cannot throw a checked exception and must handle InterruptedException. This noncompliant code example catches and suppresses InterruptedException:
This code prevents callers of the run() method from determining that an interrupted exception occurred. Consequently, caller methods such as Thread.start() cannot act on the exception [Goetz 2006]. Likewise, if this code were called in its own thread, it would prevent the calling thread from knowing that the thread was interrupted.
*/

class Foo implements Runnable {
  public void run() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      // Ignore
    }
  }
}
