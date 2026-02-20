/*
Noncompliant Code Example
This noncompliant code example describes a security vulnerability from the Java 1.5 java.io package. In this release, java.io.File is nonfinal, allowing an attacker to supply an untrusted argument constructed by extending the legitimate File class. In this manner, the getPath() method can be overridden so that the security check passes the first time it is called but the value changes the second time to refer to a sensitive file such as /etc/passwd. This is an example of a time-of-check, time-of-use (TOCTOU) vulnerability.
*/


public RandomAccessFile openFile(final java.io.File f) {
  askUserPermission(f.getPath());
  // ...
  return (RandomAccessFile)AccessController.doPrivileged(new PrivilegedAction <Object>() {
    public Object run() {
      return new RandomAccessFile(f, f.getPath());
    }
  });
}