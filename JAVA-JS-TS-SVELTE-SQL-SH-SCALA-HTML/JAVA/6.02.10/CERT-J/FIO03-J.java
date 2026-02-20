/*
Noncompliant Code Example
This and subsequent code examples assume that files are created in a secure directory in compliance with FIO00-J. Do not operate on files in shared directories and are created with proper access permissions in compliance with FIO01-J. Create files with appropriate access permissions. Both requirements may be managed outside the Java Virtual Machine (JVM).

This noncompliant code example fails to remove the file upon completion:

*/

class TempFile {
  public static void main(String[] args) throws IOException{
    File f = new File("tempnam.tmp");
    if (f.exists()) {
      System.out.println("This file already exists");
      return;
    }

    FileOutputStream fop = null;
    try {
      fop = new FileOutputStream(f);
      String str = "Data";
      fop.write(str.getBytes());
    } finally {
      if (fop != null) {
        try {
          fop.close();
        } catch (IOException x) {
          // Handle error
        }
      }
    }
  }
}
/*
Noncompliant Code Example (createTempFile(), deleteOnExit())
This noncompliant code example invokes the File.createTempFile() method, which generates a unique temporary file name based on two parameters: a prefix and an extension. This is the only method from Java 6 and earlier that is designed to produce unique file names, although the names produced can be easily predicted. A random number generator can be used to produce the prefix if a random file name is required.

This example also uses the deleteOnExit() method to ensure that the temporary file is deleted when the JVM terminates. However, according to the Java API [API 2014] Class File, method deleteOnExit() documentation,

Deletion will be attempted only for normal termination of the virtual machine, as defined by the Java Language Specification. Once deletion has been requested, it is not possible to cancel the request. This method should therefore be used with care.
Note: this method should not be used for file-locking, as the resulting protocol cannot be made to work reliably.

Consequently, the file is not deleted if the JVM terminates unexpectedly. A longstanding bug on Windows-based systems, reported as Bug ID: 4171239 [SDN 2008], causes JVMs to fail to delete a file when deleteOnExit() is invoked before the associated stream or RandomAccessFile is closed.

*/

class TempFile {
  public static void main(String[] args) throws IOException{
    File f = File.createTempFile("tempnam",".tmp");
    FileOutputStream fop = null;
    try {
      fop = new FileOutputStream(f);
      String str = "Data";
      fop.write(str.getBytes());
      fop.flush();
    } finally {
      // Stream/file still open; file will
      // not be deleted on Windows systems
      f.deleteOnExit(); // Delete the file when the JVM terminates

      if (fop != null) {
        try {
          fop.close();
        } catch (IOException x) {
          // Handle error
        }
      }
    }
  }
}