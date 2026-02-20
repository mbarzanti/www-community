/*
Noncompliant Code Example (Checked Exception)
This noncompliant code example protects a resource, an open file, by using a ReentrantLock. However, the method fails to release the lock when an exception occurs while performing operations on the open file. When an exception is thrown, control transfers to the catch block and the call to unlock() never executes.

*/

public final class Client {
  private final Lock lock = new ReentrantLock();

  public void doSomething(File file) {
    InputStream in = null;
    try {
      in = new FileInputStream(file);
      lock.lock();

      // Perform operations on the open file

      lock.unlock();
    } catch (FileNotFoundException x) {
      // Handle exception
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException x) {
          // Handle exception
        }  
      }
    }
  }
}
/*
Noncompliant Code Example (finally Block)
This noncompliant code example attempts to rectify the problem of the lock not being released by invoking Lock.unlock() in the finally block. This code ensures that the lock is released regardless of whether or not an exception occurs. However, it does not acquire the lock until after trying to open the file. If the file cannot be opened, the lock may be unlocked without ever being locked in the first place.

*/

public final class Client {
  private final Lock lock = new ReentrantLock();

  public void doSomething(File file) {
    InputStream in = null;
    try {
      in = new FileInputStream(file);
      lock.lock();
      // Perform operations on the open file
    } catch (FileNotFoundException fnf) {
      // Forward to handler
    } finally {
      lock.unlock();
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          // Forward to handler
        }
      }
    }
  }
}

/*
Noncompliant Code Example (Unchecked Exception)
This noncompliant code example uses a ReentrantLock to protect a java.util.Date instance—recall that java.util.Date is thread-unsafe by design.
A runtime exception can occur because the doSomething() method fails to check whether str is a null reference, preventing the lock from being released.
*/

final class DateHandler {

  private final Date date = new Date();

  private final Lock lock = new ReentrantLock();

  // str could be null
  public void doSomething(String str) {
    lock.lock();
    String dateString = date.toString();
    if (str.equals(dateString)) {
      // ...
    }
    // ...

    lock.unlock();
  }
}
