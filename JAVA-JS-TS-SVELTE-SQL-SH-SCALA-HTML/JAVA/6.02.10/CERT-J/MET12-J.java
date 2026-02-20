/*
Noncompliant Code Example (Superclass's finalizer)
Superclasses that use finalizers impose additional constraints on their extending classes. Consider an example from JDK 1.5 and earlier. The following noncompliant code example allocates a 16 MB buffer used to back a Swing JFrame object. Although the JFrame APIs lack finalize() methods, JFrame extends AWT.Frame, which does have a finalize() method. When a MyFrame object becomes unreachable, the garbage collector cannot reclaim the storage for the byte buffer because code in the inherited finalize() method might refer to it. Consequently, the byte buffer must persist at least until the inherited finalize() method for class MyFrame completes its execution and cannot be reclaimed until the following garbage-collection cycle.
*/


class MyFrame extends JFrame {
  private byte[] buffer = new byte[16 * 1024 * 1024];
  // Persists for at least two GC cycles
}

/*
Noncompliant Code Example (System.runFinalizersOnExit())
This noncompliant code example uses the System.runFinalizersOnExit() method to simulate a garbage-collection run. Note that this method is deprecated because of thread-safety issues.

According to the Java API [API 2014] class System, runFinalizersOnExit() method documentation,

Enable or disable finalization on exit; doing so specifies that the finalizers of all objects that have finalizers that have not yet been automatically invoked are to be run before the Java runtime exits. By default, finalization on exit is disabled.

The class SubClass overrides the protected finalize() method and performs cleanup activities. Subsequently, it calls super.finalize() to make sure its superclass is also finalized. The unsuspecting BaseClass calls the doLogic() method, which happens to be overridden in the SubClass. This resurrects a reference to SubClass that not only prevents it from being garbage-collected but also prevents it from calling its finalizer to close new resources that may have been allocated by the called method. As detailed in MET05-J. Ensure that constructors do not call overridable methods, if the subclass's finalizer has terminated key resources, invoking its methods from the superclass might result in the observation of an object in an inconsistent state. In some cases, this can result in NullPointerException.

This code outputs:



Subclass finalize!
Superclass finalize!
This is sub-class! The date object is: null
*/

class BaseClass {
  protected void finalize() throws Throwable {
    System.out.println("Superclass finalize!");
    doLogic();
  }

  public void doLogic() throws Throwable {
    System.out.println("This is super-class!");
  }
}

class SubClass extends BaseClass {
  private Date d; // Mutable instance field

  protected SubClass() {
    d = new Date();
  }

  protected void finalize() throws Throwable {
    System.out.println("Subclass finalize!");
    try {
      //  Cleanup resources
      d = null;
    } finally {
      super.finalize();  // Call BaseClass's finalizer
    }
  }

  public void doLogic() throws Throwable {
    // Any resource allocations made here will persist

    // Inconsistent object state
    System.out.println(
        "This is sub-class! The date object is: " + d);
    // 'd' is already null
  }
}

public class BadUse {
  public static void main(String[] args) {
    try {
      BaseClass bc = new SubClass();
      // Artificially simulate finalization (do not do this)
      System.runFinalizersOnExit(true);
    } catch (Throwable t) {
      // Handle error
    }
  }
}

