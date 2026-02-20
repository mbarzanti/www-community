/*
Noncompliant Code Example
This noncompliant code example creates multiple BufferedInputStream wrappers on System.in, even though there is only one declaration of a BufferedInputStream. The getChar() method creates a new BufferedInputStream each time it is called. Data that is read from the underlying stream and placed in the buffer during execution of one call cannot be replaced in the underlying stream so that a second call has access to it. Consequently, data that remains in the buffer at the end of a particular execution of getChar() is lost. Although this noncompliant code example uses a BufferedInputStream, any buffered wrapper is unsafe; this condition is also exploitable when using a Scanner, for example.

Implementation Details (POSIX)
When compiled under Java 1.6.0 and run from the command line, this program successfully takes two characters as input and prints them out. However, when run with a file redirected to standard input, the program throws EOFException because the second call to getChar() finds no characters to read upon encountering the end of the stream.

It may appear that the mark() and reset() methods of BufferedInputStream could be used to replace the read bytes. However, these methods provide look-ahead by operating on the internal buffers of the BufferedInputStream rather than by operating directly on the underlying stream. Because the example code creates a new BufferedInputStream on each call to getchar(), the internal buffers of the previous BufferedInputStream are lost.
*/

public final class InputLibrary {
  public static char getChar() throws EOFException, IOException {
    BufferedInputStream in = new BufferedInputStream(System.in); // Wrapper
    int input = in.read();
    if (input == -1) {
      throw new EOFException();
    }
    // Down casting is permitted because InputStream guarantees read() in range
    // 0..255 if it is not -1
    return (char) input;
  }

  public static void main(String[] args) {
    try {
      // Either redirect input from the console or use
      // System.setIn(new FileInputStream("input.dat"));
      System.out.print("Enter first initial: ");
      char first = getChar();
      System.out.println("Your first initial is " + first);
      System.out.print("Enter last initial: ");
      char last = getChar();
      System.out.println("Your last initial is " + last);
    } catch (EOFException e) {
      System.err.println("ERROR");
      // Forward to handler
    } catch (IOException e) {
      System.err.println("ERROR");
      // Forward to handler
    }
  }
}
