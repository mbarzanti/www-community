/*
Noncompliant Code Example (byte)
FileInputStream is a subclass of InputStream. It will return −1 only when the end of the input stream has been reached. This noncompliant code example casts the value returned by the read() method directly to a value of type byte and then compares this value with −1 in an attempt to detect the end of the stream.

If the read() method encounters a 0xFF byte in the file, this value becomes indistinguishable from the −1 value used to indicate the end of stream, because the byte value is promoted and sign-extended to an int before being compared with −1. Consequently, the loop will halt prematurely if a 0xFF byte is read.
*/

FileInputStream in;
// Initialize stream 
byte data;
while ((data = (byte) in.read()) != -1) { 
  // ... 
}

/*
Noncompliant Code Example (char)
FileReader is a subclass of InputStreamReader, which is in turn a subclass of Reader. It also returns −1 only when the end of the stream is reached. This noncompliant code example casts the value of type int returned by the read() method directly to a value of type char, which is then compared with −1 in an attempt to detect the end of stream. This conversion leaves the value of data as 0xFFFF (e.g., Character.MAX_VALUE) instead of −1. Consequently, the test for the end of file never evaluates to true.
*/


FileReader in;
// Initialize stream 
char data;
while ((data = (char) in.read()) != -1) { 
  // ... 
}