/*
Noncompliant Code Example
This noncompliant code example accepts a tainted path or file name as an argument. An attacker can access a protected file by supplying its path name as an argument to this method.
*/


private void privilegedMethod(final String filename) 
                              throws FileNotFoundException {
  try {
    FileInputStream fis =
        (FileInputStream) AccessController.doPrivileged(
          new PrivilegedExceptionAction() {
        public FileInputStream run() throws FileNotFoundException {
          return new FileInputStream(filename);
        }
      }
    );
    // Do something with the file and then close it
  } catch (PrivilegedActionException e) {
    // Forward to handler
  }
}