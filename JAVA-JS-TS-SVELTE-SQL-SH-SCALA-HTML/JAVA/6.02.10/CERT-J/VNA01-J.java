/*
Noncompliant Code Example
This noncompliant code example consists of the immutable Helper class:
*/


// Immutable Helper
public final class Helper {
  private final int n;

  public Helper(int n) {
    this.n = n;
  }
  // ...
}
/*
and a mutable Foo class:
*/


final class Foo {
  private Helper helper;

  public Helper getHelper() {
    return helper;
  }

  public void setHelper(int num) {
    helper = new Helper(num);
  }
}
/*
The getHelper() method publishes the mutable helper field. Because the Helper class is immutable, it cannot be changed after it is initialized.


Furthermore, because Helper is immutable, it is always constructed properly before its reference is made visible, in compliance with TSM03-J. Do not publish partially initialized objects. Unfortunately, a separate thread could observe a stale reference in the helper field of the Foo class.
*/
