/*
Noncompliant Code Example
In this noncompliant code example, the fields contained within the outer class are serialized when the inner class is serialized:

*/

public class OuterSer implements Serializable {
  private int rank;
  class InnerSer implements Serializable {
    protected String name;
    // ...
  }
}