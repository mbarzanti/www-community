/*
Noncompliant Code Example
This noncompliant code example writes a critical security exception to the standard error stream:
Writing such exceptions to the standard error stream is inadequate for logging purposes. First, the standard error stream may be exhausted or closed, preventing recording of subsequent exceptions. Second, the trust level of the standard error stream may be insufficient for recording certain security-critical exceptions or errors without leaking sensitive information. If an I/O error were to occur while writing the security exception, the catch block would throw an IOException and the critical security exception would be lost. Finally, an attacker may disguise the exception so that it occurs with several other innocuous exceptions.

Using Console.printf(), System.out.print*(), or Throwable.printStackTrace() to output a security exception also constitutes a violation of this rule.
*/

try {
  // ...
} catch (SecurityException se) {
  System.err.println(se);
  // Recover from exception
}
