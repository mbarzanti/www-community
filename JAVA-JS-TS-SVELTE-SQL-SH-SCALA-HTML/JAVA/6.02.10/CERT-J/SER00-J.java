/*
Noncompliant Code Example
This noncompliant code example implements a GameWeapon class with a serializable field called numOfWeapons and uses the default serialized form. Any changes to the internal representation of the class can break the existing serialized form.

Because this class does not provide a serialVersionUID, the Java Virtual Machine (JVM) assigns it one using implementation-defined methods. If the class definition changes, the serialVersionUID is also likely to change. Consequently, the JVM will refuse to associate the serialized form of an object with the class definition when the version IDs are different.
*/

class GameWeapon implements Serializable {
  int numOfWeapons = 10;
	    
  public String toString() {
    return String.valueOf(numOfWeapons);
  }
}
