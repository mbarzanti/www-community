/*
Noncompliant Code Example
This noncompliant code example compares the name of the class of object auth to the string "com.application.auth.DefaultAuthenticationHandler" and branches on the result of the comparison:
Comparing fully qualified class names is insufficient because distinct class loaders can load differing classes with identical fully qualified names into a single JVM.
*/


 // Determine whether object auth has required/expected class object
 if (auth.getClass().getName().equals(
      "com.application.auth.DefaultAuthenticationHandler")) {
   // ...
}
