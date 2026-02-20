/*
Noncompliant Code Example (Class.newInstance())
This noncompliant code example throws undeclared checked exceptions. The undeclaredThrow() method takes a Throwable argument and invokes a function that will throw the argument without declaring it. Although undeclaredThrow() catches any exceptions the function declares that it might throw, it nevertheless throws the argument it is given without regard to whether the argument is one of the declared exceptions. This noncompliant code example also violates ERR07-J. Do not throw RuntimeException, Exception, or Throwable. However, because of exception ERR08-J-EX0, it does not violate ERR08-J. Do not catch NullPointerException or any of its ancestors.
Any checked exception thrown by the default constructor of java.lang.Class.newInstance() is propagated to the caller even though Class.newInstance() declares that it throws only InstantiationException and IllegalAccessException. This noncompliant code example demonstrates one way to use Class.newInstance() to throw arbitrary checked and unchecked exceptions:
*/


public class NewInstance {
  private static Throwable throwable;

  private NewInstance() throws Throwable {
    throw throwable;
  }

  public static synchronized void undeclaredThrow(Throwable throwable) {
    // These exceptions should not be passed
    if (throwable instanceof IllegalAccessException ||
        throwable instanceof InstantiationException) {
      // Unchecked, no declaration required
      throw new IllegalArgumentException(); 
    }

    NewInstance.throwable = throwable;
    try {
      // Next line throws the Throwable argument passed in above,
      // even though the throws clause of class.newInstance fails
      // to declare that this may happen
      NewInstance.class.newInstance();
    } catch (InstantiationException e) { /* Unreachable */
    } catch (IllegalAccessException e) { /* Unreachable */
    } finally { // Avoid memory leak
      NewInstance.throwable = null;
    }
  }
}

public class UndeclaredException {
  public static void main(String[] args) {   
    // No declared checked exceptions
    NewInstance.undeclaredThrow(
        new Exception("Any checked exception"));
  }
}
/*
Noncompliant Code Example (Class.newInstance() Workarounds)
When the programmer wishes to catch and handle the possible undeclared checked exceptions, the compiler refuses to believe that any can be thrown in the particular context. This noncompliant code example attempts to catch undeclared checked exceptions thrown by Class.newInstance(). It catches Exception and dynamically checks whether the caught exception is an instance of the possible checked exception (carefully rethrowing all other exceptions).

*/

public static void main(String[] args) {
  try {
    NewInstance.undeclaredThrow(
        new IOException("Any checked exception"));
  } catch (Throwable e) {
    if (e instanceof IOException) {
      System.out.println("IOException occurred");
    } else if (e instanceof RuntimeException) {
      throw (RuntimeException) e;
    } else {
      // Forward to handler
    }
  }
}

/*
Noncompliant Code Example (sun.misc.Unsafe)
This noncompliant code example is insecure both because it can throw undeclared checked exceptions and because it uses the sun.misc.Unsafe class. All sun.* classes are unsupported and undocumented because their use can cause portability and backward compatibility issues.

Classes loaded by the bootstrap class loader have the permissions needed to call the static factory method Unsafe.getUnsafe(). Arranging to have an arbitrary class loaded by the bootstrap class loader without modifying the sun.boot.class.path system property can be difficult. However, an alternative way to gain access is to change the accessibility of the field that holds an instance of Unsafe through the use of reflection. This approach works only when permitted by the current security manager (which would violate ENV03-J. Do not grant dangerous combinations of permissions). Given access to Unsafe, a program can throw an undeclared checked exception by calling the Unsafe.throwException() method.
*/


import java.io.IOException;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

public class UnsafeCode {
  public static void main(String[] args)
      throws SecurityException, NoSuchFieldException,
             IllegalArgumentException, IllegalAccessException {
    Field f = Unsafe.class.getDeclaredField("theUnsafe");
    f.setAccessible(true);
    Unsafe u = (Unsafe) f.get(null);
    u.throwException(new IOException("No need to declare this checked exception"));
  }
}

/*
Noncompliant Code Example (Generic Exception)
An unchecked cast of a generic type with parameterized exception declaration can also result in unexpected checked exceptions. All such casts are diagnosed by the compiler unless the warnings are suppressed.
*/


interface Thr<EXC extends Exception> {
  void fn() throws EXC;
}

public class UndeclaredGen {
  static void undeclaredThrow() throws RuntimeException {
    @SuppressWarnings("unchecked")  // Suppresses warnings
    Thr<RuntimeException> thr = (Thr<RuntimeException>)(Thr)
      new Thr<IOException>() {
        public void fn() throws IOException {
          throw new IOException();
	}
      };
    thr.fn();
  }

  public static void main(String[] args) {
    undeclaredThrow();
  }
}
/*
Noncompliant Code Example (Thread.stop(Throwable))
According to the Java API [API 2014], class Thread:

[Thread.stop()] This method was originally designed to force a thread to stop and throw a given Throwable as an exception. It was inherently unsafe (see Thread.stop() for details), and furthermore could be used to generate exceptions that the target thread was not prepared to handle.

For example, the following method is behaviorally identical to Java's throw operation but circumvents the compiler's attempts to guarantee that the calling method has declared all of the checked exceptions that it may throw.

Note that the Thread.stop() methods are deprecated, so this code also violates MET02-J. Do not use deprecated or obsolete classes or methods.

Noncompliant Code Example (Bytecode Manipulation)
It is also possible to disassemble a class, remove any declared checked exceptions, and reassemble the class so that checked exceptions are thrown at runtime when the class is used [Roubtsov 2003]. Compiling against a class that declares the checked exception and supplying at runtime a class that lacks the declaration can also result in undeclared checked exceptions. Undeclared checked exceptions can also be produced through crafted use of the sun.corba.Bridge class. All of these practices are violations of this rule.
*/


static void sneakyThrow(Throwable t) {
  Thread.currentThread().stop(t);
}
