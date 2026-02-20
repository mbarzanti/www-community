/*
Noncompliant Code Example (Finalizer Attack)
This noncompliant code example, based on an example by Kabutz [Kabutz 2001], defines the constructor of the BankOperations class so that it performs social security number (SSN) verification using the method performSSNVerification(). The implementation of the performSSNVerification() method assumes that an attacker does not know the correct SSN and trivially returns false.



public class BankOperations {
  public BankOperations() {
    if (!performSSNVerification()) {
      throw new SecurityException("Access Denied!");
    }
  }

  private boolean performSSNVerification() {
    return false; // Returns true if data entered is valid, else false
                  // Assume that the attacker always enters an invalid SSN
  }

  public void greet() {
    System.out.println("Welcome user! You may now use all the features.");
  }
}

public class Storage {
  private static BankOperations bop;

  public static void store(BankOperations bo) {
  // Store only if it is initialized
    if (bop == null) {
      if (bo == null) {
        System.out.println("Invalid object!");
        System.exit(1);
      }
      bop = bo;
    }
  }
}

public class UserApp {
  public static void main(String[] args) {
    BankOperations bo;
    try {
      bo = new BankOperations();
    } catch (SecurityException ex) { bo = null; }

    Storage.store(bo);
    System.out.println("Proceed with normal logic");
  }
}
The constructor throws a SecurityException when SSN verification fails. The UserApp class appropriately catches this exception and displays an "Access Denied" message. However, these precautions fail to prevent a malicious program from invoking methods of the partially initialized class BankOperations, as shown by the following exploit code.

The goal of the attack is to capture a reference to the partially initialized object of the BankOperations class. If a malicious subclass catches the SecurityException thrown by the BankOperations constructor, it is unable to further exploit the vulnerable code because the new object instance has gone out of scope. Instead, an attacker can exploit this code by extending the BankOperations class and overriding the finalize() method. This attack intentionally violates MET12-J. Do not use finalizers.

When the constructor throws an exception, the garbage collector waits to grab the object reference. However, the object cannot be garbage-collected until after the finalizer completes its execution. The attacker's finalizer obtains and stores a reference by using the this keyword. Consequently, the attacker can maliciously invoke any instance method on the base class by using the stolen instance reference. This attack can even bypass a check by a security manager.

Compliance with ERR00-J. Do not suppress or ignore checked exceptions and ERR03-J. Restore prior object state on method failure can help to ensure that fields are appropriately initialized in catch blocks. A developer who explicitly initializes the variable to null is more likely to document this behavior so that other programmers or clients include the appropriate null reference checks where required. Moreover, this approach guarantees initialization safety in a multithreaded scenario.
*/


public class Interceptor extends BankOperations {
  private static Interceptor stealInstance = null;

  public static Interceptor get() {
    try {
      new Interceptor();
    } catch (Exception ex) {/* Ignore exception */}
    try {
      synchronized (Interceptor.class) {
        while (stealInstance == null) {
          System.gc();
          Interceptor.class.wait(10);
        }
      }
    } catch (InterruptedException ex) { return null; }
    return stealInstance;
  }

  public void finalize() {
    synchronized (Interceptor.class) {
      stealInstance = this;
      Interceptor.class.notify();
    }
    System.out.println("Stole the instance in finalize of " + this);
  }
}

public class AttackerApp { // Invoke class and gain access
                           // to the restrictive features
  public static void main(String[] args) {
    Interceptor i = Interceptor.get(); // Stolen instance

    // Can store the stolen object even though this should have printed
    // "Invalid Object!"
    Storage.store(i);

    // Now invoke any instance method of BankOperations class
    i.greet();

    UserApp.main(args); // Invoke the original UserApp
  }
}
