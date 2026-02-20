/*
Noncompliant Code Example
This noncompliant code example defines a mutable class Employee that consists of the fields name and salary, whose values can be changed using the setEmployeeName() and setSalary() method. The equals() method is overridden to provide a comparison facility by employee name.
Use of the Employee object as a key to the map is insecure because the properties of the object could change after an ordering has been established. For example, a client could modify the name field when the last name of an employee changes. As a result, clients would observe nondeterministic behavior.
*/

// Mutable class Employee
class Employee {
  private String name;
  private double salary;

  Employee(String empName, double empSalary) {
    this.name = empName;
    this.salary = empSalary;
  }

  public void setEmployeeName(String empName) {
    this.name = empName;
  }

  public void setSalary(double empSalary) {
    this.salary = empSalary;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Employee)) {
      return false;
    }

    Employee emp = (Employee)o;
    return emp.name.equals(name);
  }

  public int hashCode() {/* ... */}

}

// Client code
Map<Employee, Calendar> map =
  new ConcurrentHashMap<Employee, Calendar>();
// ...

/*
Noncompliant Code Example
Many programmers are surprised by an instance of hash code mutability that arises because of serialization. The contract for the hashCode() method lacks any requirement that hash codes remain consistent across different executions of an application. Similarly, when an object is serialized and subsequently deserialized, its hash code after deserialization may be inconsistent with its original hash code.

This noncompliant code example uses the MyKey class as the key index for the Hashtable. The MyKey class overrides Object.equals() but uses the default Object.hashCode(). According to the Java API [API 2014] class Hashtable documentation:

To successfully store and retrieve objects from a hash table, the objects used as keys must implement the hashCode method and the equals method.

This noncompliant code example follows that advice but nevertheless can fail after serialization and deserialization. Consequently, it may be impossible to retrieve the value of the object after deserialization by using the original key.

*/

class MyKey implements Serializable {
  // Does not override hashCode()
}

class HashSer {
  public static void main(String[] args)
                     throws IOException, ClassNotFoundException {
    Hashtable<MyKey,String> ht = new Hashtable<MyKey, String>();
    MyKey key = new MyKey();
    ht.put(key, "Value");
    System.out.println("Entry: " + ht.get(key));
    // Retrieve using the key, works

    // Serialize the Hashtable object
    FileOutputStream fos = new FileOutputStream("hashdata.ser");
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    oos.writeObject(ht);
    oos.close();

    // Deserialize the Hashtable object
    FileInputStream fis = new FileInputStream("hashdata.ser");
    ObjectInputStream ois = new ObjectInputStream(fis);
    Hashtable<MyKey, String> ht_in =
        (Hashtable<MyKey, String>)(ois.readObject());
    ois.close();

    if (ht_in.contains("Value"))
      // Check whether the object actually exists in the hash table
      System.out.println("Value was found in deserialized object.");

    if (ht_in.get(key) == null) // Gets printed
      System.out.println(
          "Object was not found when retrieved using the key.");
  }
}