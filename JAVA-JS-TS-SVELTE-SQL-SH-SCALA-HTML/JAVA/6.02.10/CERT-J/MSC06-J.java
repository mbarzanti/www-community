/*
Noncompliant Code Example (Single-Threaded)
This noncompliant code example (based on Sun Developer Network SDN 2011 bug report 6687277) uses the ArrayList's remove() method to remove an element from an ArrayList while iterating over the ArrayList. The resulting behavior is unspecified.

*/

class BadIterate {
  public static void main(String[] args) {
    List<String> list = new ArrayList<String>();
    list.add("one");
    list.add("two");
        
    Iterator iter = list.iterator();
    while (iter.hasNext()) {
      String s = (String)iter.next();
      if (s.equals("one")) {
        list.remove(s);
      }
    }
  }    
}
/*
Noncompliant Code Example (Multithreaded)
Although acceptable in a single-threaded environment, this noncompliant code example is insecure in a multithreaded environment because it is possible for another thread to modify the widgetList while the current thread iterates over the widgetList. Additionally, the doSomething() method could modify the collection during iteration.
*/


List<Widget> widgetList = new ArrayList<Widget>();

public void widgetOperation() {
  // May throw ConcurrentModificationException
  for (Widget w : widgetList) {
    doSomething(w);
  }
}