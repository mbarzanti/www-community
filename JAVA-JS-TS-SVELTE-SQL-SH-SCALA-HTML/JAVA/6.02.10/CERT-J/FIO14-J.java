/*
Noncompliant Code Example
This example creates a new file, outputs some text to it, and abruptly exits using Runtime.exit(). Consequently, the file may be closed without the text actually being written.
*/


public class CreateFile {
  public static void main(String[] args)
                          throws FileNotFoundException {
    final PrintStream out =
        new PrintStream(new BufferedOutputStream(
                        new FileOutputStream("foo.txt")));
    out.println("hello");
    Runtime.getRuntime().exit(1);
  }
}

/*
Noncompliant Code Example (Runtime.halt())
This noncompliant code example calls Runtime.halt() instead of Runtime.exit(). The Runtime.halt() method stops the JVM without invoking any shutdown hooks; consequently, the file is not properly written to or closed.
*/


public class CreateFile {
  public static void main(String[] args)
                          throws FileNotFoundException {
    final PrintStream out =
          new PrintStream(new BufferedOutputStream(
                          new FileOutputStream("foo.txt")));
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        public void run() {
          out.close();
        }
    }));
    out.println("hello");
    Runtime.getRuntime().halt(1);
  }
}
/*
Noncompliant Code Example (Signal)
When a user forcefully exits a program, for example by pressing the Ctrl+C keys or by using the kill command, the JVM terminates abruptly. Although this event cannot be captured, the program should nevertheless perform any mandatory cleanup operations before exiting. This noncompliant code example fails to do so.
*/


public class InterceptExit {
  public static void main(String[] args)
                          throws FileNotFoundException {
    InputStream in = null;
    try {
      in = new FileInputStream("file");
      System.out.println("Regular code block");
      // Abrupt exit such as ctrl + c key pressed
      System.out.println("This never executes");
    } finally {
      if (in != null) {
        try {
          in.close();  // This never executes either
        } catch (IOException x) {
          // Handle error
        }
      }
    }
  }
}