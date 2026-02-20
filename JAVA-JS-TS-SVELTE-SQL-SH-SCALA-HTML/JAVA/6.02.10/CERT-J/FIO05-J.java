/*
Noncompliant Code Example (wrap())
This noncompliant code example declares a char array, wraps it within a CharBuffer, and exposes that CharBuffer to untrusted code via the getBufferCopy() method:
*/


final class Wrap {
  private char[] dataArray;

  public Wrap() {
    dataArray = new char[10];
    // Initialize
  }

  public CharBuffer getBufferCopy() {
    return CharBuffer.wrap(dataArray);
  }
}