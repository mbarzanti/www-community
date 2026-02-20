/*
Noncompliant Code Example (Singleton)
In this noncompliant code example [Bloch 2005], a class with singleton semantics uses the default serialized form, which fails to enforce any implementation-defined invariants. Consequently, malicious code can create a second instance even though the class should have only a single instance. For purposes of this example, we assume that the class contains only nonsensitive data.

*/

public class NumberData extends Number {
  // ... Implement abstract Number methods, like Number.doubleValue()...

  private static final NumberData INSTANCE = new NumberData ();
  public static NumberData getInstance() {
    return INSTANCE;
  }

  private NumberData() {
    // Perform security checks and parameter validation
  }

  protected int printData() {
    int data = 1000;
    // Print data
    return data;
  }
}

class Malicious {
  public static void main(String[] args) {
    NumberData sc = (NumberData) deepCopy(NumberData.getInstance());
    // Prints false; indicates new instance
    System.out.println(sc == NumberData.getInstance());  
    System.out.println("Balance = " + sc.printData());
  }

  // This method should not be used in production code
  public static Object deepCopy(Object obj) {
    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      new ObjectOutputStream(bos).writeObject(obj);
      ByteArrayInputStream bin =
          new ByteArrayInputStream(bos.toByteArray());
      return new ObjectInputStream(bin).readObject();
    } catch (Exception e) { 
      throw new IllegalArgumentException(e);
    }
  }
}

/*
Noncompliant Code Example
This noncompliant code example uses a custom-defined readObject() method but fails to perform input validation after deserialization. The design of the system requires the maximum ticket number of any lottery ticket to be 20,000 and the minimum ticket number be greater than 0. However, an attacker can manipulate the serialized array to generate a different number on deserialization. Such a number could be greater than 20,000 or could be 0 or negative.
*/


public class Lottery implements Serializable {	
  private int ticket = 1;
  private SecureRandom draw = new SecureRandom();

  public Lottery(int ticket) {
    this.ticket = (int) (Math.abs(ticket % 20000) + 1);
  }

  public int getTicket() {
    return this.ticket;	
  }

  public int roll() {
    this.ticket = (int) ((Math.abs(draw.nextInt()) % 20000) + 1);
    return this.ticket;
  }

  public static void main(String[] args) {
    Lottery l = new Lottery(2);
    for (int i = 0; i < 10; i++) {
      l.roll();
      System.out.println(l.getTicket());
    }
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
  }
}

/*
Noncompliant Code Example (AtomicReferenceArray<>)
CVE-2012-0507 describes an exploit that managed to bypass Java's applet security sandbox and run malicious code on a remote user's machine. The exploit deserialized a malicious object that subverted Java's type system. The malicious object was an array of two objects. The second object, of type AtomicReferenceArray<>, contained an array, but this array was also the first object. This data structure could not be created without serialization because the array referenced by AtomicReferenceArray<> should be private. However, whereas the first object was an array of objects of type Help (which inherited from ClassLoader), the AtomicReferenceArray<>'s internal array type was Object, enabling the malicious code to use AtomicReferenceArray.set(ClassLoader) to create an object that was subsequently interpreted as being of type Help object with no cast necessary. A cast would have caught this type mismatch. This exploit allowed attackers to create their own ClassLoader object, which is forbidden by the applet security manager.

This exploit worked because, in Java versions prior to 1.7.0_02, the object of type AtomicReferenceArray<> performed no validation on its internal array.
*/


public class AtomicReferenceArray<E> implements java.io.Serializable {
  private static final long serialVersionUID = -6209656149925076980L;

  // Rest of class...
  // No readObject() method, relies on default readObject
}