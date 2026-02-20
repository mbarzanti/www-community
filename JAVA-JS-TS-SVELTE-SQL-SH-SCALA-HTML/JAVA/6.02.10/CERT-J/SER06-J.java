/*
Noncompliant Code Example
This noncompliant code example fails to defensively copy the mutable Date object date. An attacker might be able to create an instance of MutableSer whose date object contains a nefarious subclass of Date and whose methods can perform actions specified by an attacker. Any code that depends on the immutability of the subobject is vulnerable.
*/


class MutableSer implements Serializable {
  private static final Date epoch = new Date(0);
  private Date date = null; // Mutable component
  
  public MutableSer(Date d){
    date = new Date(d.getTime()); // Constructor performs defensive copying
  }

  private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
    ois.defaultReadObject();
    // Perform validation if necessary
  }
}