/*
Noncompliant Code Example (Collection View)
This noncompliant code example creates a HashMap object and two view objects: a synchronized view of an empty HashMap encapsulated by the mapView field and a set view of the map's keys encapsulated by the setView field. This example synchronizes on setView [Java Tutorials].

In this example, HashMap provides the backing collection for the synchronized map represented by mapView, which provides the backing collection for setView
*/

private final Map<Integer, String> mapView =
    Collections.synchronizedMap(new HashMap<Integer, String>());
private final Set<Integer> setView = mapView.keySet();

public Map<Integer, String> getMap() {
  return mapView;
}

public void doSomething() {
  synchronized (setView) {  // Incorrectly synchronizes on setView
    for (Integer k : setView) {
      // ...
    }
  }
}

