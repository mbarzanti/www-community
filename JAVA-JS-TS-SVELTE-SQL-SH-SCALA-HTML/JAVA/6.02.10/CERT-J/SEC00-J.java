/*
Noncompliant Code Example
In this noncompliant code example, the doPrivileged() method is called from the openPasswordFile() method. The openPasswordFile() method is privileged and returns a FileInputStream for the sensitive password file. Because the method is public, it could be invoked by an untrusted caller.
*/


public class PasswordManager {

  public static void changePassword() throws FileNotFoundException {
    FileInputStream fin = openPasswordFile();

    // Test old password with password in file contents; change password,
    // then close the password file
  }

  public static FileInputStream openPasswordFile()
      throws FileNotFoundException {
    final String password_file = "password";
    FileInputStream fin = null;
    try {
      fin = AccessController.doPrivileged(
        new PrivilegedExceptionAction<FileInputStream>() {
          public FileInputStream run() throws FileNotFoundException {
            // Sensitive action; can't be done outside privileged block
            FileInputStream in = new FileInputStream(password_file);
            return in;
          }
      });
    } catch (PrivilegedActionException x) {
      Exception cause = x.getException();
      if (cause instanceof FileNotFoundException) {
        throw (FileNotFoundException) cause;
      } else {
        throw new Error("Unexpected exception type", cause); 
      }
    }
    return fin;
  }
}