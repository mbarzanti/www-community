/*
Noncompliant Code Example
This noncompliant code example allows a subclass to override the readSensitiveFile() method and omit the required security check:
*/


public void readSensitiveFile() {
  try {
    SecurityManager sm = System.getSecurityManager();
    if (sm != null) {  // Check for permission to read file
      sm.checkRead("/temp/tempFile");
    }
    // Access the file
  } catch (SecurityException se) {
    // Log exception
  }
}


