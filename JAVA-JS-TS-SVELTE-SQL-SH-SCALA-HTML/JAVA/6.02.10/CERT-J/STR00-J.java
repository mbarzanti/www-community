/*
Noncompliant Code Example (Read)
This noncompliant code example tries to read up to 1024 bytes from a socket and build a String from this data. It does so by reading the bytes in a while loop, as recommended by FIO10-J. Ensure the array is filled when using read() to fill an array. If it ever detects that the socket has more than 1024 bytes available, it throws an exception, which prevents untrusted input from potentially exhausting the program's memory.
This code fails to account for the interaction between variable-width character encodings and the boundaries between the loop iterations. If the last byte read from the data stream in one read() operation is the leading byte of a character, the trailing bytes are not encountered until the next iteration of the while loop. However, variable-width encoding is resolved during construction of the new String within the loop. Consequently, the variable-width encoding can be interpreted incorrectly. A similar problem can occur when constructing strings from UTF-16 data if the surrogate pair for a supplementary character are separated.
*/
public final int MAX_SIZE = 1024;

public String readBytes(Socket socket) throws IOException {
  InputStream in = socket.getInputStream();
  byte[] data = new byte[MAX_SIZE+1];
  int offset = 0;
  int bytesRead = 0;
  String str = new String();
  while ((bytesRead = in.read(data, offset, data.length - offset)) != -1) {
    str += new String(data, offset, bytesRead, "UTF-8");
    offset += bytesRead;
    if (offset >= data.length) {
      throw new IOException("Too much input");
    }
  }
  in.close();
  return str;
}
