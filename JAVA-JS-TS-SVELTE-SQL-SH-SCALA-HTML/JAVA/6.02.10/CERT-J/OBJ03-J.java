/*
Noncompliant Code Example
This noncompliant code example compiles but results in heap pollution. The compiler produces an unchecked warning because a raw argument (the obj parameter in the addToList() method) is passed to the List.add() method. 

Heap pollution is possible in this case because the parameterized type information is discarded before execution. The call to addToList(list, 42) succeeds in adding an integer to list, although it is of type List<String>. This Java runtime does not throw a ClassCastException until the value is read and has an invalid type (an int rather than a String). In other words, the code throws an exception some time after the execution of the operation that actually caused the error, complicating debugging.

Even when heap pollution occurs, the variable is still guaranteed to refer to a subclass or subinterface of the declared type but is not guaranteed to always refer to a subtype of its declared type. In this example, list does not refer to a subtype of its declared type (List<String>) but only to the subinterface of the declared type (List).
*/

class ListUtility {
  private static void addToList(List list, Object obj) {
    list.add(obj); // Unchecked warning
  }

  public static void main(String[] args) {
    List<String> list = new ArrayList<String> ();
    addToList(list, 42);
    System.out.println(list.get(0));  // Throws ClassCastException
  }
}
