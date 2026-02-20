/*
Noncompliant Code Example
In this noncompliant example, the Trusted class has permission to load libraries while the Untrusted class does not. However, the Trusted class provides a library loading service through a public method thus allowing the Untrusted class to load any libraries it desires.
*/


// Trusted.java

import java.security.*;

public class Trusted {

   public static void loadLibrary(final String library){
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
             System.loadLibrary(library);
             return null;
         }
      });
   }
}

---------------------------------------------------------------------------------

// Untrusted.java

public class Untrusted {

   private native void nativeOperation();

   public static void main(String[] args) {
      String library = new String("NativeMethodLib");
      Trusted.loadLibrary(library);
      new Untrusted.nativeOperation();  // invoke the native method
   }
}