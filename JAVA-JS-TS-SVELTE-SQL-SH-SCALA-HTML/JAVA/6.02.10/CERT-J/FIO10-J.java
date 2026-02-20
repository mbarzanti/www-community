/*
Noncompliant Code Example (1-argument read())
This noncompliant code example attempts to read 1024 bytes encoded in UTF-8 from an InputStream and return them as a String. It explicitly specifies the character encoding used to build the string, in compliance with STR04-J. Use compatible character encodings when communicating string data between JVMs.

The programmer's misunderstanding of the general contract of the read() method can result in failure to read the intended data in full. It is possible that less than 1024 bytes exist in the stream, perhaps because the stream originates from a file with less than 1024 bytes. It is also possible that the stream contains 1024 bytes but less than 1024 bytes are immediately available, perhaps because the stream originates from a TCP socket that sent more bytes in a subsequent packet that has not arrived yet. In either case, read() will return less than 1024 bytes. It indicates this through its return value, but the program ignores the return value and uses the entire array to construct a string, even though any unread bytes will fill the string with null characters.
*/


public static String readBytes(InputStream in) throws IOException {
  byte[] data = new byte[1024];
  if (in.read(data) == -1) {
    throw new EOFException();
  }
  return new String(data, "UTF-8");
}

/*
Noncompliant Code Example (3-argument read())
This noncompliant code example uses the 3-argument version of read() to read 1024 bytes encoded in UTF-8 from an InputStream and return them as a String.

However, this code suffers from the same flaws as the previous noncompliant code example. Again, the read() method can return less than 1024 bytes, either because 1024 bytes are simply not available, or the latter bytes have not arrived in the stream yet.  In either case, read() returns less than 1024 bytes, the remaining bytes in the array remain with zero values, yet the entire array is used to construct the string.
*/

public static String readBytes(InputStream in) throws IOException {
  byte[] data = new byte[1024];
  int offset = 0;
  if (in.read(data, offset, data.length - offset)) != -1) {
    throw new EOFException();
  }
  return new String(data, "UTF-8");
}
