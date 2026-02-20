/*
Noncompliant Code Example (Privileged Code)
This noncompliant code example includes a doPrivileged() block and calls a method defined in a class in a different, untrusted JAR file:

An attacker can provide an implementation of class RetValue so that the privileged code uses an incorrect return value. Even though class MixMatch consists only of trusted, signed code, an attacker can still cause this behavior by maliciously deploying a valid signed JAR file containing the untrusted RetValue class.

This example almost violates SEC01-J. Do not allow tainted variables in privileged blocks but does not do so. It instead allows potentially tainted code in its doPrivileged() block, which is a similar issue.
*/


package trusted;
import untrusted.RetValue;

public class MixMatch {
  private void privilegedMethod() throws IOException {
    try {
      AccessController.doPrivileged(
        new PrivilegedExceptionAction<Void>() {
          public Void run() throws IOException, FileNotFoundException {
            final FileInputStream fis = new FileInputStream("file.txt");
            try {
              RetValue rt = new RetValue();

              if (rt.getValue() == 1) {
                // Do something with sensitive file
              }
            } finally {
              fis.close();
            }
            return null; // Nothing to return
          }
        }
      );
    } catch (PrivilegedActionException e) {
      // Forward to handler and log
    }
  }

  public static void main(String[] args) throws IOException {
    MixMatch mm = new MixMatch();
    mm.privilegedMethod();
  }
}

// In another JAR file:
package untrusted;

class RetValue {
  public int getValue() {
    return 1;
  }
}

/*
Noncompliant Code Example (Security-Sensitive Code)
This noncompliant code example improves on the previous example by moving the use of the RetValue class outside the doPrivileged() block:

Although the RetValue class is used only outside the doPrivileged() block, the behavior of RetValue.getValue() affects the behavior of security-sensitive code that operates on the file opened within the doPrivileged() block. Consequently, an attacker can still exploit the security-sensitive code with a malicious implementation of RetValue.
*/

package trusted;
import untrusted.RetValue;

public class MixMatch {
  private void privilegedMethod() throws IOException {
    try {
      final FileInputStream fis = AccessController.doPrivileged(
        new PrivilegedExceptionAction<FileInputStream>() {
          public FileInputStream run() throws FileNotFoundException {
            return new FileInputStream("file.txt");
          }
        }
      );
      try {
        RetValue rt = new RetValue();

        if (rt.getValue() == 1) {
          // Do something with sensitive file
        }
      } finally {
        fis.close();
      }
    } catch (PrivilegedActionException e) {
      // Forward to handler and log
    }
  }

  public static void main(String[] args) throws IOException {
    MixMatch mm = new MixMatch();
    mm.privilegedMethod();
  }
}

// In another JAR file:
package untrusted;

class RetValue {
  public int getValue() {
    return 1;
  }
}
