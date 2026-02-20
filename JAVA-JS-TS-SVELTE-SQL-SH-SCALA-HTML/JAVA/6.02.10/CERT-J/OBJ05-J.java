/*
Noncompliant Code Example
This noncompliant code example shows a getDate() accessor method that returns the sole instance of the private Date object:
An untrusted caller can manipulate a private Date object because returning the reference exposes the internal mutable component beyond the trust boundaries of MutableClass.
*/


class MutableClass {
  private Date d;

  public MutableClass() {
    d = new Date();
  }

  public Date getDate() {
    return d;
  }
}

/*
Noncompliant Code Example (Mutable Member Array)
In this noncompliant code example, the getDate() accessor method returns an array of Date objects. The method fails to make a defensive copy of the array before returning it. Because the array contains references to Date objects that are mutable, a shallow copy of the array is insufficient because an attacker can modify the Date objects in the array.
*/


class MutableClass {
  private Date[] date;

  public MutableClass() {
    date = new Date[20];
    for (int i = 0; i < date.length; i++) {
      date[i] = new Date();
    }
  }

  public Date[] getDate() {
    return date; // Or return date.clone()
  }
}

/*
Noncompliant Code Example (Mutable Member Containing Immutable Objects)
In this noncompliant code example, class ReturnRef contains a private Hashtable instance field. The hash table stores immutable but sensitive data (for example, social security numbers [SSNs]). The getValues() method gives the caller access to the hash table by returning a reference to it. An untrusted caller can use this method to gain access to the hash table; as a result, hash table entries can be maliciously added, removed, or replaced. Furthermore, multiple threads can perform these modifications, providing ample opportunities for race conditions.
In returning a reference to the ht hash table, this example also hinders efficient garbage collection.
*/

class ReturnRef {
  // Internal state, may contain sensitive data
  private Hashtable<Integer,String> ht = new Hashtable<Integer,String>();

  private ReturnRef() {
    ht.put(1, "123-45-6666");
  }

  public Hashtable<Integer,String> getValues(){
    return ht;
  }

  public static void main(String[] args) {
    ReturnRef rr = new ReturnRef();
    Hashtable<Integer, String> ht1 = rr.getValues(); // Prints sensitive data 123-45-6666
    ht1.remove(1);                                   // Untrusted caller can remove entries
    Hashtable<Integer, String> ht2 = rr.getValues(); // Now prints null; original entry is removed
  }
}
