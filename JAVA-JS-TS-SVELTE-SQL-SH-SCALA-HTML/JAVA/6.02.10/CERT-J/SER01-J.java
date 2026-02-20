/*
Noncompliant Code Example (readObject(), writeObject())
This noncompliant code example shows a class Ser with a private constructor, indicating that code external to the class should be unable to create instances of it. The class implements java.io.Serializable and defines public readObject() and writeObject() methods. Consequently, untrusted code can obtain the reconstituted objects by using readObject() and can write to the stream by using writeObject().

Note that there are two things wrong with the signatures of writeObject() and readObject() in this Noncompliant Code Example: (1) the method is declared public instead of private, and (2) the method is declared static instead of non-static.  Since the method signatures do not exactly match the required signatures, the JVM will not detect the two methods, resulting in failure to use the custom serialized form.
*/

public class Ser implements Serializable {
  private final long serialVersionUID = 123456789;
  private Ser() {
    // Initialize
  }
  public static void writeObject(final ObjectOutputStream stream)
    throws IOException {
    stream.defaultWriteObject();
  }	
  public static void readObject(final ObjectInputStream stream)
      throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
  }
}
/*
Noncompliant Code Example (readResolve(), writeReplace())
This noncompliant code example declares the readResolve() and writeReplace() methods as private:

*/


class Extendable implements Serializable {
  private Object readResolve() {
    // ...
  }

  private Object writeReplace() {
    // ...
  }
}
/*
Noncompliant Code Example (readResolve(), writeReplace())
This noncompliant code example declares the readResolve() and writeReplace() methods as static:

*/

class Extendable implements Serializable {
  protected static Object readResolve() {
    // ...
  }

  protected static Object writeReplace() {
    // ...
  }
}


