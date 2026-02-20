/*
Noncompliant Code Example
The data members of class Point are private. Assuming the coordinates are sensitive, their presence in the data stream would expose them to malicious tampering.

In the absence of sensitive data, classes can be serialized by simply implementing the java.io.Serializable interface. By doing so, the class indicates that no security issues may result from the object's serialization. Note that any derived subclasses also inherit this interface and are consequently serializable. This approach is inappropriate for any class that contains sensitive data.
*/

public class Point implements Serializable {
  private double x;
  private double y;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public Point() {
    // No-argument constructor
  }
}

public class Coordinates extends Point {
  public static void main(String[] args) {
    FileOutputStream fout = null;
    try {
      Point p = new Point(5, 2);
      fout = new FileOutputStream("point.ser");
      ObjectOutputStream oout = new ObjectOutputStream(fout);
      oout.writeObject(p);
    } catch (Throwable t) { 
      // Forward to handler 
    } finally {
      if (fout != null) {
        try {
          fout.close();
        } catch (IOException x) {
          // Handle error
        }
      }
    }
  }
}
/*
Noncompliant Code Example
Serialization can be used maliciously, for example, to return multiple instances of a singleton class object. In this noncompliant code example (based on [Bloch 2005]), a subclass SensitiveClass inadvertently becomes serializable because it extends the java.lang.Number class, which implements Serializable:
See MSC07-J. Prevent multiple instantiations of singleton objects for more information about singleton classes.
*/


public class SensitiveClass extends Number {
  // ... Implement abstract methods, such as Number.doubleValue()â€¦

  private static final SensitiveClass INSTANCE = new SensitiveClass();
  public static SensitiveClass getInstance() {
    return INSTANCE;
  }

  private SensitiveClass() {
    // Perform security checks and parameter validation
  }

  private int balance = 1000;
  protected int getBalance() {
    return balance;
  }
}

class Malicious {
  public static void main(String[] args) {
    SensitiveClass sc =
       (SensitiveClass) deepCopy(SensitiveClass.getInstance());
    // Prints false; indicates new instance
    System.out.println(sc == SensitiveClass.getInstance());  
    System.out.println("Balance = " + sc.getBalance());
  }

  // This method should not be used in production code
  static public Object deepCopy(Object obj) {
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
