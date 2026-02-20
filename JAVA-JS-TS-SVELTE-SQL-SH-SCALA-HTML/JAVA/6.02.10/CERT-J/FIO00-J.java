/*
Noncompliant Code Example
In this noncompliant code example, an attacker could specify the name of a locked device or a first in, first out (FIFO) file, causing the program to hang when opening the file:

*/

String file = /* Provided by user */;
InputStream in = null;
try {
  in = new FileInputStream(file);
  // ...
} finally {
  try {
    if (in !=null) { in.close();}
  } catch (IOException x) {
    // Handle error
  }
}
7+
Noncompliant Code Example
This noncompliant code example uses the try-with-resources statement (introduced in Java SE 7) to open the file. The try-with-resources statement guarantees the file's successful closure if an exception is thrown, but this code is subject to the same vulnerabilities as the previous example.
+/


String filename = /* Provided by user */;
Path path = new File(filename).toPath();
try (InputStream in = Files.newInputStream(path)) {
  // Read file
} catch (IOException x) {
  // Handle error
}
/*
Noncompliant Code Example (isRegularFile())
This noncompliant code example first checks that the file is a regular file (using the NIO.2 API) before opening it:

This test can still be circumvented by a symbolic link. By default, the readAttributes() method follows symbolic links and reads the file attributes of the final target of the link. The result is that the program may reference a file other than the one intended.
*/

String filename = /* Provided by user */
Path path = new File(filename).toPath();
try {
  BasicFileAttributes attr =
      Files.readAttributes(path, BasicFileAttributes.class);

  // Check
  if (!attr.isRegularFile()) {
    System.out.println("Not a regular file");
    return;
  }
  // Other necessary checks
  
  // Use
  try (InputStream in = Files.newInputStream(path)) {
    // Read file
  }
} catch (IOException x) {
  // Handle error
}

/*
Noncompliant Code Example (NOFOLLOW_LINKS)
This noncompliant code example checks the file by calling the readAttributes() method with the NOFOLLOW_LINKS link option to prevent the method from following symbolic links. This approach allows the detection of symbolic links because the isRegularFile() check is carried out on the symbolic link file and not on the final target of the link.

This code is still vulnerable to a time-of-check, time-of-use (TOCTOU) race condition. For example, an attacker can replace the regular file with a file link or device file after the code has completed its checks but before it opens the file.
*/

String filename = /* Provided by user */;
Path path = new File(filename).toPath();
try {
  BasicFileAttributes attr = Files.readAttributes(
      path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);

  // Check
  if (!attr.isRegularFile()) {
    System.out.println("Not a regular file");
    return;
  }
  // Other necessary checks

  // Use
  try (InputStream in = Files.newInputStream(path)) {
    // Read file
  };
} catch (IOException x) {
  // Handle error
}

/*
Noncompliant Code Example (POSIX: Check-Use-Check)
This noncompliant code example performs the necessary checks and then opens the file. After opening the file, it performs a second check to make sure that the file has not been moved and that the file opened is the same file that was checked. This approach reduces the chance that an attacker has changed the file between checking and then opening the file. In both checks, the file's fileKey attribute is examined. The fileKey attribute serves as a unique key for identifying files and is more reliable than the path name as on indicator of the file's identity.

The SE 7 Documentation [J2SE 2011] describes the fileKey attribute:

Returns an object that uniquely identifies the given file, or null if a file key is not available. On some platforms or file systems it is possible to use an identifier, or a combination of identifiers to uniquely identify a file. Such identifiers are important for operations such as file tree traversal in file systems that support symbolic links or file systems that allow a file to be an entry in more than one directory. On UNIX file systems, for example, the device ID and inode are commonly used for such purposes.

The file key returned by this method can only be guaranteed to be unique if the file system and files remain static. Whether a file system re-uses identifiers after a file is deleted is implementation dependent and consequently unspecified.

File keys returned by this method can be compared for equality and are suitable for use in collections. If the file system and files remain static, and two files are the same with non-null file keys, then their file keys are equal.

As noted in the documentation, FileKey cannot be used if it is not available. The fileKey() method returns null on Windows. Consequently, this solution is available only on systems such as POSIX in which fileKey() does not return null.

Although this code goes to great lengths to prevent an attacker from successfully tricking it into opening the wrong file, it still has several vulnerabilities:

The TOCTOU race condition still exists between the first check and open. During this race window, an attacker can replace the regular file with a symbolic link or other nonregular file. The second check detects this race condition but does not eliminate it.
An attacker could subvert this code by letting the check operate on a regular file, substituting the nonregular file for the open, and then resubstituting the regular file to circumvent the second check. This vulnerability exists because Java lacks a mechanism to obtain file attributes from a file by any means other than the file name, and the binding of the file name to a file object is reasserted every time the file name is used in an operation. Consequently, an attacker can still swap a file for a nefarious file, such as a symbolic link.
A system with hard links allows an attacker to construct a malicious file that is a hard link to a protected file. Hard links cannot be reliably detected by a program and can foil canonicalization attempts, which are prescribed by FIO16-J. Canonicalize path names before validating them.
*/


String filename = /* Provided by user */;
Path path = new File(filename).toPath();
try {
  BasicFileAttributes attr = Files.readAttributes(
      path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
  Object fileKey = attr.fileKey();

  // Check
  if (!attr.isRegularFile()) {
    System.out.println("Not a regular file");
    return;
  }
  // Other necessary checks

  // Use
  try (InputStream in = Files.newInputStream(path)) {

    // Check
    BasicFileAttributes attr2 = Files.readAttributes(
        path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS
    );
    Object fileKey2 = attr2.fileKey();
    if (!fileKey.equals(fileKey2)) {
      System.out.println("File has been tampered with");
    }

    // Read file
  };
} catch (IOException x) {
  // Handle error
}
