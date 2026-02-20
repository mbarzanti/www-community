/*
Noncompliant Code Example
The double-checked locking pattern uses block synchronization rather than method synchronization and installs an additional null reference check before attempting synchronization. This noncompliant code example uses an incorrect form of the double-checked locking idiom:

According to Pugh [Pugh 2004],

Writes that initialize the Helper object and the write to the helper field can be done or perceived out of order. As a result, a thread which invokes getHelper() could see a non-null reference to a helper object, but see the default values for fields of the helper object, rather than the values set in the constructor.

Even if the compiler does not reorder those writes, on a multiprocessor, the processor or the memory system may reorder those writes, as perceived by a thread running on another processor.

This code also violates TSM03-J. Do not publish partially initialized objects.
*/

// Double-checked locking idiom
final class Foo {
  private Helper helper = null;
  public Helper getHelper() {
    if (helper == null) {
      synchronized (this) {
        if (helper == null) {
          helper = new Helper();
        }
      }
    }
    return helper;
  }

  // Other methods and members...
}

/*
Noncompliant Code Example (Immutable)
In this noncompliant code example, the Helper class is made immutable by declaring its fields final. The JMM guarantees that immutable objects are fully constructed before they become visible to any other thread. The block synchronization in the getHelper() method guarantees that all threads that can see a non-null value of the helper field will also see the fully initialized Helper object.

However, this code is not guaranteed to succeed on all Java Virtual Machine platforms because there is no happens-before relationship between the first read and third read of helper. Consequently, it is possible for the third read of helper to obtain a stale null value (perhaps because its value was cached or reordered by the compiler), causing the getHelper() method to return a null pointer.
*/

public final class Helper {
  private final int n;
 
  public Helper(int n) {
    this.n = n;
  }
 
  // Other fields and methods, all fields are final
}
 
final class Foo {
  private Helper helper = null;
 
  public Helper getHelper() {
    if (helper == null) {            // First read of helper
      synchronized (this) {
        if (helper == null) {        // Second read of helper
          helper = new Helper(42);
        }
      }
    }
    return helper;                   // Third read of helper
  }
}
