/*
Noncompliant Code Example (exitValue())
This noncompliant code example invokes a hypothetical cross-platform notepad application using the external command notemaker. The notemaker application does not read its input stream but sends output to both its output stream and error stream.

This noncompliant code example invokes notemaker using the exec() method, which returns a Process object. The exitValue() method returns the exit value for processes that have terminated, but it throws an IllegalThreadStateException when invoked on an active process. Because this noncompliant example program fails to wait for the notemaker process to terminate, the call to exitValue() is likely to throw an IllegalThreadStateException.

*/

public class Exec {
  public static void main(String args[]) throws IOException {
    Runtime rt = Runtime.getRuntime();
    Process proc = rt.exec("notemaker");
    int exitVal = proc.exitValue();
  }
}
/*
Noncompliant Code Example (waitFor())
In this noncompliant code example, the waitFor() method blocks the calling thread until the notemaker process terminates. This approach prevents the IllegalThreadStateException from the previous example. However, the example program may experience an arbitrary delay before termination. Output from the notemaker process can exhaust the available buffer for the output or error stream because neither stream is read while waiting for the process to complete. If either buffer becomes full, it can block the notemaker process as well, preventing all progress for both the notemaker process and the Java program.

*/

public class Exec {
  public static void main(String args[])
                          throws IOException, InterruptedException {
    Runtime rt = Runtime.getRuntime();
    Process proc = rt.exec("notemaker");
    int exitVal = proc.waitFor();
  }
}
/*
Noncompliant Code Example (Process Output Stream)
This noncompliant code example properly empties the process's output stream, thereby preventing the output stream buffer from becoming full and blocking. However, it ignores the process's error stream, which can also fill and cause the process to block.
*/


public class Exec {
  public static void main(String args[])
                     throws IOException, InterruptedException {
    Runtime rt = Runtime.getRuntime();
    Process proc = rt.exec("notemaker");
    InputStream is = proc.getInputStream();
    int c;
    while ((c = is.read()) != -1) {
      System.out.print((char) c);
    }
    int exitVal = proc.waitFor();
  }
}