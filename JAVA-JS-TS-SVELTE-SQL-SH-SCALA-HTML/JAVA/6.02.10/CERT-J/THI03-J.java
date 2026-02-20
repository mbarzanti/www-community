/*
Noncompliant Code Example
This noncompliant code example invokes the wait() method inside a traditional if block and fails to check the postcondition after the notification is received. If the notification were accidental or malicious, the thread could wake up prematurely.
*/


synchronized (object) {
  if (<condition does not hold>) {
    object.wait();
  }
  // Proceed when condition holds
}