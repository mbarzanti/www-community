/*
Noncompliant Code Example (Method Synchronization)
This noncompliant code example exposes instances of the SomeObject class to untrusted code.

The untrusted code attempts to acquire a lock on the object's monitor and, upon succeeding, introduces an indefinite delay that prevents the synchronized changeValue() method from acquiring the same lock. Furthermore, the object locked is publicly available via the lookup() method.

Alternatively, an attacker could create a private SomeObject object and make it available to trusted code to use it before the attacker code grabs and holds the lock.

Note that in the untrusted code, the attacker intentionally violates rule LCK09-J. Do not perform operations that can block while holding a lock.
*/

public class SomeObject {

  // Locks on the object's monitor
  public synchronized void changeValue() { 
    // ...
  }
 
  public static SomeObject lookup(String name) {
    // ...
  }
}

// Untrusted code
String name = // ...
SomeObject someObject = SomeObject.lookup(name);
if (someObject == null) {
  // ... handle error
}
synchronized (someObject) {
  while (true) {
    // Indefinitely lock someObject
    Thread.sleep(Integer.MAX_VALUE); 
  }
}

/*
Noncompliant Code Example (Public Non-final Lock Object)
This noncompliant code example locks on a public nonfinal object in an attempt to use a lock other than {{SomeObject}}'s intrinsic lock.

This change fails to protect against malicious code. For example, untrusted or malicious code could disrupt proper synchronization by changing the value of the lock object.
*/

public class SomeObject {
  public Object lock = new Object();

  public void changeValue() {
    synchronized (lock) {
      // ...
    }
  }
}

/*
Noncompliant Code Example (Publicly Accessible Non-final Lock Object)
This noncompliant code example synchronizes on a publicly accessible but nonfinal field. The lock field is declared volatile so that changes are visible to other threads.

Any thread can modify the field's value to refer to a different object in the presence of an accessor such as setLock(). That modification might cause two threads that intend to lock on the same object to lock on different objects, thereby permitting them to execute two critical sections in an unsafe manner. For example, if the lock were changed when one thread was in its critical section, a second thread would lock on the new object instead of the old one and would enter its critical section erroneously.

A class that lacks accessible methods to change the lock is secure against untrusted manipulation. However, it remains susceptible to inadvertent modification by the programmer.
*/

public class SomeObject {
  private volatile Object lock = new Object();

  public void changeValue() {
    synchronized (lock) {
      // ...
    }
  }

  public void setLock(Object lockValue) {
    lock = lockValue;
  }
}

/*
Noncompliant Code Example (Public Final Lock Object)
This noncompliant code example uses a public final lock object.

This noncompliant code example also violates rule OBJ01-J. Limit accessibility of fields.
*/

public class SomeObject {
  public final Object lock = new Object();

  public void changeValue() {
    synchronized (lock) {
      // ...
    }
  }
}

/*
Noncompliant Code Example (Static)
This noncompliant code example exposes the class object of SomeObject to untrusted code.
The untrusted code attempts to acquire a lock on the class object''s monitor and, upon succeeding, introduces an indefinite delay that prevents the synchronized changeValue() method from acquiring the same lock.

A compliant solution must also comply with rule LCK05-J. Synchronize access to static fields that can be modified by untrusted code.
In the untrusted code, the attacker intentionally violates rule LCK09-J. Do not perform operations that can block while holding a lock.
*/

public class SomeObject {
  //changeValue locks on the class object's monitor
  public static synchronized void changeValue() { 
    // ...
  }
}

// Untrusted code
synchronized (SomeObject.class) {
  while (true) {
    Thread.sleep(Integer.MAX_VALUE); // Indefinitely delay someObject
  }
}


