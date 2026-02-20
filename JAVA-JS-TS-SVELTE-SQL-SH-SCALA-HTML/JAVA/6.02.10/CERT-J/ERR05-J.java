/*
Noncompliant Code Example
This noncompliant code example contains a finally block that closes the reader object. The programmer incorrectly assumes that the statements in the finally block cannot throw exceptions and consequently fails to appropriately handle any exception that may arise.

The close() method can throw an IOException, which, if thrown, would prevent execution of any subsequent cleanup statements. This problem will not be diagnosed by the compiler because any IOException would be caught by the outer catch block. Also, an exception thrown from the close() operation can mask any exception that gets thrown during execution of the Do operations block, preventing proper recovery.
*/

public class Operation {
  public static void doOperation(String some_file) {
    // ... Code to check or set character encoding ...
    try {
      BufferedReader reader =
          new BufferedReader(new FileReader(some_file));
      try {
        // Do operations 
      } finally {
        reader.close();
        // ... Other cleanup code ...
      }
    } catch (IOException x) {
      // Forward to handler
    }
  }
}



