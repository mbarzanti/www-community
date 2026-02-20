/*
Noncompliant Code Example
In this noncompliant code example, the nativeOperation() method is both native and public; consequently, untrusted callers may invoke it. Native method invocations bypass security manager checks.

This example includes the doOperation() wrapper method, which invokes the nativeOperation() native method but fails to provide input validation or security checks.
*/


public final class NativeMethod {

  // Public native method
  public native void nativeOperation(byte[] data, int offset, int len);

  // Wrapper method that lacks security checks and input validation
  public void doOperation(byte[] data, int offset, int len) {
    nativeOperation(data, offset, len);
  }
  
  static {
    // Load native library in static initializer of class
    System.loadLibrary("NativeMethodLib"); 
  }
}