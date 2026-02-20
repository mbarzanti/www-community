/*
Noncompliant Code Example
In this noncompliant code example, MutableClass uses a mutable field date of type Date. Class Date is also a mutable class. The example is noncompliant because the MutableClass objects lack copy functionality.

When a trusted caller passes an instance of MutableClass to untrusted code, and the untrusted code modifies that instance (perhaps by incrementing the month or changing the timezone), the object's state can be made inconsistent with respect to its previous state. Similar problems can arise in the presence of multiple threads, even in the absence of untrusted code.
*/

public final class MutableClass {
  private Date date;

  public MutableClass(Date d) {
    this.date = d;
  }

  public void setDate(Date d) {
    this.date = d;
  }

  public Date getDate() {
    return date;
  }
}
