/*
Noncompliant Code Example
This noncompliant code example allows the user to specify the path of an image file to open. By prepending /img/ to the directory, this code enforces a policy that only files in this directory should be opened. The program also uses the isInSecureDir() method defined in FIO00-J. Do not operate on files in shared directories. 

However, the user can still specify a file outside the intended directory by entering an argument that contains ../ sequences.  An attacker can also create a link in the /img directory that refers to a directory or file outside of that directory. The path name of the link might appear to reside in the /img directory and consequently pass validation, but the operation will actually be performed on the final target of the link, which can reside outside the intended directory.
*/
File file = new File("/img/" + args[0]);
if (!isInSecureDir(file)) {
  throw new IllegalArgumentException();
}
FileOutputStream fis = new FileOutputStream(file);
/*
Noncompliant Code Example (getCanonicalPath())
This noncompliant code example attempts to mitigate the issue by using the File.getCanonicalPath() method, introduced in Java 2, which fully resolves the argument and constructs a canonicalized path. Special file names such as dot dot (..) are also removed so that the input is reduced to a canonicalized form before validation is carried out. An attacker cannot use ../ sequences to break out of the specified directory when the validate() method is present. For example, the path /img/../etc/passwd resolves to /etc/passwd. The getCanonicalPath() method throws a security exception when used in applets because it reveals too much information about the host machine. The getCanonicalFile() method behaves like getCanonicalPath() but returns a new File object instead of a String.

Unfortunately, the canonicalization is performed after the validation, which renders the validation ineffective.
*/
File file = new File("/img/" + args[0]);
if (!isInSecureDir(file)) {
  throw new IllegalArgumentException();
}
String canonicalPath = file.getCanonicalPath();
FileOutputStream fis = new FileOutputStream(canonicalPath);
// ...