/*
Noncompliant Code Example
This noncompliant code example constructs a Helper object in the initialize() method of the Foo class. The Helper object's fields are initialized by its constructor.

If a thread were to access helper using the getHelper() method before the initialize() method executed, the thread would observe an uninitialized helper field. Later, if one thread calls initialize() and another calls getHelper(), the second thread could observe one of the following:

The helper reference as null
A fully initialized Helper object with the n field set to 42
A partially initialized Helper object with an uninitialized n, which contains the default value 0
In particular, the JMM permits compilers to allocate memory for the new Helper object and to assign a reference to that memory to the helper field before initializing the new Helper object. In other words, the compiler can reorder the write to the helper instance field and the write that initializes the Helper object (that is, this.n = n) so that the former occurs first. This can expose a race window during which other threads can observe a partially initialized Helper object instance.

There is a separate issue: if more than one thread were to call initialize(), multiple Helper objects would be created. This is merely a performance issue—correctness would be preserved. The n field of each object would be properly initialized and the unused Helper object (or objects) would eventually be garbage-collected.
*/

class Foo {
  private Helper helper;

  public Helper getHelper() {
    return helper;
  }

  public void initialize() {
    helper = new Helper(42);
  }
}

public class Helper {
  private int n;

  public Helper(int n) {
    this.n = n;
  }
  // ...
}
