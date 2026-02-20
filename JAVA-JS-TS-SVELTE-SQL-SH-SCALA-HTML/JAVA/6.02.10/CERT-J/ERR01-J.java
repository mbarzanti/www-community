/*
Noncompliant Code Example (Leaks from Exception Message and Type)
In this noncompliant code example, the program must read a file supplied by the user, but the contents and layout of the file system are sensitive. The program accepts a file name as an input argument but fails to prevent any resulting exceptions from being presented to the user.
When a requested file is absent, the FileInputStream constructor throws a FileNotFoundException, allowing an attacker to reconstruct the underlying file system by repeatedly passing fictitious path names to the program.
*/


class ExceptionExample {
  public static void main(String[] args) throws FileNotFoundException {
    // Linux stores a user's home directory path in 
    // the environment variable $HOME, Windows in %APPDATA%
    FileInputStream fis =
        new FileInputStream(System.getenv("APPDATA") + args[0]);  
  }
}


/*
Noncompliant Code Example (Wrapping and Rethrowing Sensitive Exception)
This noncompliant code example logs the exception and then wraps it in a more general exception before rethrowing it:

Even when the logged exception is not accessible to the user, the original exception is still informative and can be used by an attacker to discover sensitive information about the file system layout.
Note that this example also violates FIO04-J. Release resources when they are no longer needed, as it fails to close the input stream in a finally block. Subsequent code examples also omit this finally block for brevity.
*/

try {
  FileInputStream fis =
      new FileInputStream(System.getenv("APPDATA") + args[0]);
} catch (FileNotFoundException e) {
  // Log the exception
  throw new IOException("Unable to retrieve file", e);
}


/*
Noncompliant Code Example (Sanitized Exception)
This noncompliant code example logs the exception and throws a custom exception that does not wrap the FileNotFoundException:
Although this exception is less likely than the previous noncompliant code examples to leak useful information, it still reveals that the specified file cannot be read. More specifically, the program reacts differently to nonexistent file paths than it does to valid ones, and an attacker can still infer sensitive information about the file system from this program's behavior. Failure to restrict user input leaves the system vulnerable to a brute-force attack in which the attacker discovers valid file names by issuing queries that collectively cover the space of possible file names. File names that cause the program to return the sanitized exception indicate nonexistent files, whereas file names that do not return exceptions reveal existing files.
*/


class SecurityIOException extends IOException {/* ... */};

try {
  FileInputStream fis =
      new FileInputStream(System.getenv("APPDATA") + args[0]);
} catch (FileNotFoundException e) {
  // Log the exception
  throw new SecurityIOException();
}
