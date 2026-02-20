/*
Noncompliant Code Example (Logical Negation)
This noncompliant code example declares a shared boolean flag variable and provides a toggle() method that negates the current value of flag:
Execution of this code may result in a data race because the value of flag is read, negated, and written back.
*/

final class Flag {
  private boolean flag = true;

  public void toggle() {  // Unsafe
    flag = !flag;
  }

  public boolean getFlag() { // Unsafe
    return flag;
  }
}
/*
Noncompliant Code Example (Bitwise Negation)
The toggle() method may also use the compound assignment operator ^= to negate the current value of flag:

This code is also not thread-safe. A data race exists because ^= is a non-atomic compound operation.
*/

final class Flag {
  private boolean flag = true;

  public void toggle() {  // Unsafe
    flag ^= true;  // Same as flag = !flag;
  }

  public boolean getFlag() { // Unsafe
    return flag;
  }
}

/*
Noncompliant Code Example (Volatile)
Declaring flag volatile also fails to solve the problem:

This code remains unsuitable for multithreaded use because declaring a variable volatile fails to guarantee the atomicity of compound operations on the variable.
*/
final class Flag {
  private volatile boolean flag = true;

  public void toggle() {  // Unsafe
    flag ^= true;
  }

  public boolean getFlag() { // Safe
    return flag;
  }
}

/*
Noncompliant Code Example (Addition of Primitives)
In this noncompliant code example, multiple threads can invoke the setValues() method to set the a and b fields. Because this class fails to test for integer overflow, users of the Adder class must ensure that the arguments to the setValues() method can be added without overflow (see NUM00-J. Detect or prevent integer overflow for more information).

The getSum() method contains a race condition. For example, when a and b currently have the values 0 and Integer.MAX_VALUE, respectively, and one thread calls getSum() while another calls setValues(Integer.MAX_VALUE, 0), the getSum() method might return either 0 or Integer.MAX_VALUE, or it might overflow. Overflow will occur when the first thread reads a and b after the second thread has set the value of a to Integer.MAX_VALUE but before it has set the value of b to 0.

Note that declaring the variables as volatile fails to resolve the issue because these compound operations involve reads and writes of multiple variables.
*/


final class Adder {
  private int a;
  private int b;

  public int getSum() {
    return a + b;
  }

  public void setValues(int a, int b) {
    this.a = a;
    this.b = b;
  }
}

/*
Noncompliant Code Example (Addition of Atomic Integers)
In this noncompliant code example, a and b are replaced with atomic integers:

The simple replacement of the two int fields with atomic integers fails to eliminate the race condition because the compound operation a.get() + b.get() is still non-atomic.
*/


final class Adder {
  private final AtomicInteger a = new AtomicInteger();
  private final AtomicInteger b = new AtomicInteger();

  public int getSum() {
    return a.get() + b.get();
  }

  public void setValues(int a, int b) {
    this.a.set(a);
    this.b.set(b);
  }
}

