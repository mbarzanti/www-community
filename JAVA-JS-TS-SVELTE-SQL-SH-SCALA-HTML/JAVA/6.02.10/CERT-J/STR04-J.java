/*
Noncompliant Code Example
This noncompliant code example reads a byte array and converts it into a String using the platform's default character encoding. If the byte array does not represent a string, or if it represents a string that was encoded using other than the default encoding, the resulting String is likely to be incorrect. The behavior resulting from malformed-input and unmappable-character errors is unspecified.

*/

FileInputStream fis = null;
try {
  fis = new FileInputStream("SomeFile");
  DataInputStream dis = new DataInputStream(fis);
  byte[] data = new byte[1024];
  dis.readFully(data);
  String result = new String(data);
} catch (IOException x) {
  // Handle error
} finally {
  if (fis != null) {
    try {
      fis.close();
    } catch (IOException x) {
      // Forward to handler
    }
  }
}