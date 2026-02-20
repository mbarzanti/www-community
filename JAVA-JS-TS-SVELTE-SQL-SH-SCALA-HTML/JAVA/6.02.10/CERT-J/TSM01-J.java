/*
Noncompliant Code Example (Publish before Initialization)
This noncompliant code example publishes the this reference before initialization has concluded by storing it in a public static volatile class field. Consequently, other threads can obtain a partially initialized Publisher instance.

If an object's initialization (and consequently, its construction) depends on a security check within the constructor, the security check can be bypassed when an untrusted caller obtains the partially initialized instance (see OBJ11-J. Be wary of letting constructors throw exceptions for more information).
*/


final class Publisher {
  public static volatile Publisher published;
  int num;

  Publisher(int number) {
    published = this;
    // Initialization
    this.num = number;
    // ...
  }
}
/*

Noncompliant Code Example (Nonvolatile Public Static Field)
This noncompliant code example publishes the this reference in the last statement of the constructor. It remains vulnerable because the published field has public accessibility and the programmer has failed to declare it as volatile.

Because the field is nonvolatile and nonfinal, the statements within the constructor can be reordered by the compiler in such a way that the this reference is published before the initialization statements have executed.
*/

final class Publisher {
  public static Publisher published;
  int num;

  Publisher(int number) {
    // Initialization
    this.num = number;
    // ...
    published = this;
  }
}

