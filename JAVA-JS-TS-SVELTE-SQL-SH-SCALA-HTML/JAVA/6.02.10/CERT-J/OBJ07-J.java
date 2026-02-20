/*
Noncompliant Code Example
This noncompliant code example defines class SensitiveClass, which contains a character array used to hold a file name, along with a Boolean shared variable, initialized to false. This data is not meant to be copied; consequently, SensitiveClass lacks a copy constructor.
The malicious class creates an instance ms1 and produces a second instance ms2 by cloning the first. It then obtains a new filename by invoking the get() method on the first instance. At this point, the shared flag is set to true. Because the second instance ms2 does not have its shared flag set to true, it is possible to alter the first instance ms1 using the replace() method. This approach obviates any security efforts and severely violates the class's invariants.
*/


class SensitiveClass {
  private char[] filename;
  private Boolean shared = false;

  SensitiveClass(String filename) {
    this.filename = filename.toCharArray();
  }

  final void replace() {
    if (!shared) {
      for(int i = 0; i < filename.length; i++) {
    	filename[i]= 'x' ;}
    }
  }

  final String get() {
    if (!shared) {
      shared = true;
      return String.valueOf(filename);
    } else {
      throw new IllegalStateException("Failed to get instance");
    }
  }

  final void printFilename() {
    System.out.println(String.valueOf(filename));
  }
}
When a client requests a String instance by invoking the get() method, the shared flag is set. To maintain the array's consistency with the returned String object, operations that can modify the array are subsequently prohibited. As a result, the replace() method designed to replace all elements of the array with an x cannot execute normally when the flag is set. Java's cloning feature provides a way to circumvent this constraint even though SensitiveClass does not implement the Cloneable interface.

This class can be exploited by a malicious class, shown in the following noncompliant code example, that subclasses the nonfinal SensitiveClass and provides a public clone() method:



class MaliciousSubclass extends SensitiveClass implements Cloneable {
  protected MaliciousSubclass(String filename) {
    super(filename);
  }

  @Override public MaliciousSubclass clone() {  // Well-behaved clone() method
    MaliciousSubclass s = null;
    try {
      s = (MaliciousSubclass)super.clone();
    } catch(Exception e) {
      System.out.println("not cloneable");
    }
    return s;
  }

  public static void main(String[] args) {
    MaliciousSubclass ms1 = new MaliciousSubclass("file.txt");
    MaliciousSubclass ms2 = ms1.clone(); // Creates a copy
    String s = ms1.get();  // Returns filename
    System.out.println(s); // Filename is "file.txt"
    ms2.replace();         // Replaces all characters with 'x'
    // Both ms1.get() and ms2.get() will subsequently return filename = 'xxxxxxxx'
    ms1.printFilename();   // Filename becomes 'xxxxxxxx'
    ms2.printFilename();   // Filename becomes 'xxxxxxxx'
  }
}
