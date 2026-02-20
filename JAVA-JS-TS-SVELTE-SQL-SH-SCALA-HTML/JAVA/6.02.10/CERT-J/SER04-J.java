/*
Noncompliant Code Example
In this noncompliant code example, security manager checks are used within the constructor but are omitted from the writeObject() and readObject() methods that are used in the serialization-deserialization process. This omission allows untrusted code to maliciously create instances of the class.

(Although there are security manager checks, the data in this example is not sensitive. Serializing unencrypted sensitive data violates SER03-J. Do not serialize unencrypted sensitive data.)

AccessDeniedException and InvalidInputException are both security exceptions that can be thrown by any method without requiring a throws declaration.
*/


public final class Hometown implements Serializable {
  // Private internal state
  private String town;
  private static final String UNKNOWN = "UNKNOWN";

  void performSecurityManagerCheck() throws AccessDeniedException {
    // ...
  }

  void validateInput(String newCC) throws InvalidInputException {
    // ...
  }

  public Hometown() {
    performSecurityManagerCheck();

    // Initialize town to default value
    town = UNKNOWN;
  }

  // Allows callers to retrieve internal state
  String getValue() {
    performSecurityManagerCheck();
    return town;
  }

  // Allows callers to modify (private) internal state
  public void changeTown(String newTown) {
    if (town.equals(newTown)) {
      // No change
      return;
    } else {  
      performSecurityManagerCheck();
      validateInput(newTown);
      town = newTown;
    }
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeObject(town);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    in.defaultReadObject();
    // If the deserialized name does not match the default value normally
    // created at construction time, duplicate the checks
    if (!UNKNOWN.equals(town)) {
      validateInput(town);
    }
  }
}
