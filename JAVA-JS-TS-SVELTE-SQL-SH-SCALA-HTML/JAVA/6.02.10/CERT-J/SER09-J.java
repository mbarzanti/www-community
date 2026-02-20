/*
Noncompliant Code Example
This noncompliant code example invokes an overridable method from the readObject() method:
*/


private void readObject(final ObjectInputStream stream)
                        throws IOException, ClassNotFoundException {
  overridableMethod(); 
  stream.defaultReadObject();
}

public void overridableMethod() {
  // ...
}