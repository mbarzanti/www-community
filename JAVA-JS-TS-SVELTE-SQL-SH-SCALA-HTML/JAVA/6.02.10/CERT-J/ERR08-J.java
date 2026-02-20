/*
Noncompliant Code Example (NullPointerException)
This noncompliant code example defines an isName() method that takes a String argument and returns true if the given string is a valid name. A valid name is defined as two capitalized words separated by one or more spaces. Rather than checking to see whether the given string is null, the method catches NullPointerException and returns false.
*/


boolean isName(String s) {
  try {
    String names[] = s.split(" ");

    if (names.length != 2) {
      return false;
    }
    return (isCapitalized(names[0]) && isCapitalized(names[1]));
  } catch (NullPointerException e) {
    return false;
  }
}

/*
Noncompliant Code Example (Null Object Pattern)
This noncompliant code example is derived from the logging service Null Object design pattern described by Henney [Henney 2003]. The logging service is composed of two classes: one that prints the triggering activity's details to a disk file using the FileLog class and another that prints to the console using the ConsoleLog class. An interface, Log, defines a write() method that is implemented by the respective log classes. Method selection occurs polymorphically at runtime. The logging infrastructure is subsequently used by a Service class.

Each Service object must support the possibility that a Log object may be null because clients may choose not to perform logging. This noncompliant code example eliminates null checks by using a try-catch block that ignores NullPointerException.

This design choice suppresses genuine occurrences of NullPointerException in violation of ERR00-J. Do not suppress or ignore checked exceptions. It also violates the design principle that exceptions should be used only for exceptional conditions because ignoring a null Log object is part of the ordinary operation of a server.
*/

public interface Log {
  void write(String messageToLog);
}

public class FileLog implements Log {
  private final FileWriter out;

  FileLog(String logFileName) throws IOException {
    out = new FileWriter(logFileName, true);
  }

  public void write(String messageToLog) {
    // Write message to file
  }
}

public class ConsoleLog implements Log {
  public void write(String messageToLog) {
    System.out.println(messageToLog); // Write message to console
  }
}

class Service {
  private Log log;

  Service() {
    this.log = null; // No logger
  }

  Service(Log log) {
    this.log = log; // Set the specified logger
  }

  public void handle() {
    try {
      log.write("Request received and handled");
    } catch (NullPointerException npe) {
      // Ignore
    }
  }

  public static void main(String[] args) throws IOException {
    Service s = new Service(new FileLog("logfile.log"));
    s.handle();

    s = new Service(new ConsoleLog());
    s.handle();
  }
}

/*
Noncompliant Code Example (Division)
This noncompliant code example assumes that the original version of the division() method was declared to throw only ArithmeticException. However, the caller catches the more general Exception type to report arithmetic problems rather than catching the specific exception ArithmeticException type. This practice is risky because future changes to the method signature could add more exceptions to the list of potential exceptions the caller must handle. In this example, a revision of the division() method can throw IOException in addition to ArithmeticException. However, the compiler will not diagnose the lack of a corresponding handler because the invoking method already catches IOException as a result of catching Exception. Consequently, the recovery process might be inappropriate for the specific exception type that is thrown. Furthermore, the developer has failed to anticipate that catching Exception also catches unchecked exceptions.



public class DivideException {
  public static void division(int totalSum, int totalNumber)
    throws ArithmeticException, IOException  {
    int average  = totalSum / totalNumber;
    // Additional operations that may throw IOException...
    System.out.println("Average: " + average);
  }
  public static void main(String[] args) {
    try {
      division(200, 5);
      division(200, 0); // Divide by zero
    } catch (Exception e) {
      System.out.println("Divide by zero exception : " 
                         + e.getMessage());
    }
  }
}
Noncompliant Code Example
This noncompliant code example attempts to solve the problem by specifically catching ArithmeticException. However, it continues to catch Exception and consequently catches both unanticipated checked exceptions and unanticipated runtime exceptions.
Note that DivideByZeroException is a custom exception type that extends Exception.
*/

try {
  division(200, 5);
  division(200, 0); // Divide by zero
} catch (ArithmeticException ae) {
  throw new DivideByZeroException();
} catch (Exception e) {
  System.out.println("Exception occurred :" + e.getMessage());
}
