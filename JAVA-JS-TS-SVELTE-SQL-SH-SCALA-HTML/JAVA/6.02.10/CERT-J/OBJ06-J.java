/* 
Noncompliant Code Example
This noncompliant code example contains a TOCTOU vulnerability. Because cookie is a mutable input, an attacker can cause it to expire between the initial check (the hasExpired() call) and the actual use (the doLogic() call).
*/


public final class MutableDemo {
  // java.net.HttpCookie is mutable
  public void useMutableInput(HttpCookie cookie) {
    if (cookie == null) {
       throw new NullPointerException();
    }

    // Check whether cookie has expired
    if (cookie.hasExpired()) {
      // Cookie is no longer valid; handle condition by throwing an exception
    }

    // Cookie may have expired since time of check 
    doLogic(cookie);
  }
}

/*
Noncompliant Code Example
When the class of a mutable input is nonfinal or is an interface, an attacker can write a subclass that maliciously overrides the parent class's clone() method. The attacker's clone() method can subsequently subvert defensive copying. This noncompliant code example demonstrates this weakness:
*/

// java.util.Collection is an interface
public void copyInterfaceInput(Collection<String> collection) {
  doLogic(collection.clone());
}