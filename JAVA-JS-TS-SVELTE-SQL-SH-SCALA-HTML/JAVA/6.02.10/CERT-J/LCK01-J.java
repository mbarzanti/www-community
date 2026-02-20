/*
Noncompliant Code Example (Interned String Object)
This noncompliant code example locks on an interned String object.

According to the Java API class java.lang.String documentation [API 2006]:

When the intern() method is invoked, if the pool already contains a string equal to this String object as determined by the equals(Object) method, then the string from the pool is returned. Otherwise, this String object is added to the pool and a reference to this String object is returned.

Consequently, an interned String object behaves like a global variable in the JVM. As demonstrated in this noncompliant code example, even when every instance of an object maintains its own lock field, the fields all refer to a common String constant. Locking on String constants has the same reuse problem as locking on Boolean constants.

Additionally, hostile code from any other package can exploit this vulnerability, if the class is accessible. See rule LCK00-J. Use private final lock objects to synchronize classes that may interact with untrusted code for more information.
*/

private final String lock = new String("LOCK").intern();

public void doSomething() {
  synchronized (lock) {
    // ...
  }
}

/*
Noncompliant Code Example (String Literal)
This noncompliant code example locks on a final String literal.

String literals are constant and are automatically interned. Consequently, this example suffers from the same pitfalls as the preceding noncompliant code example.
*/


// This bug was found in jetty-6.1.3 BoundedThreadPool
private final String lock = "LOCK";

public void doSomething() {
  synchronized (lock) {
    // ...
  }
}
