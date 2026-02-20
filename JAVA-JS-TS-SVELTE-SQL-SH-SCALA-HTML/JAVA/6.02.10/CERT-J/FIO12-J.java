/*
Noncompliant Code Example
The read methods (readByte(), readShort(), readInt(), readLong(), readFloat(), and readDouble()) and the corresponding write methods defined by class java.io.DataInputStream and class java.io.DataOutputStream operate only on big-endian data. Use of these methods while interoperating with traditional languages, such as C and C++, is insecure because such languages lack any guarantees about endianness. This noncompliant code example shows such a discrepancy:
*/


try {
  DataInputStream dis = null;
  try {
    dis = new DataInputStream(new FileInputStream("data"));
    // Little-endian data might be read as big-endian
    int serialNumber = dis.readInt();
  } catch (IOException x) {
    // Handle error
  } finally {
    if (dis != null) {
      try {
       dis.close();
      } catch (IOException e) {
      // Handle error
      }
    }
  }
}