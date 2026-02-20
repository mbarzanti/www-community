/*
Noncompliant Code Example
In this noncompliant code example, the private fields i and j can be modified using reflection via a Field object. Furthermore, any class can modify these fields using reflection via the zeroField() method. However, only class FieldExample can modify these fields without the use of reflection.

Allowing hostile code to pass arbitrary field names to the zeroField() method can

Leak information about field names by throwing an exception for invalid or inaccessible field names (see ERR01-J. Do not allow exceptions to expose sensitive information for additional information). This example complies with ERR01-J by catching the relevant exceptions at the end of the method.
Access potentially sensitive data that is visible to zeroField() but is hidden from the attacking method. This privilege escalation attack can be difficult to find during code review because the specific field or fields being accessed are controlled by strings in the attacker's code rather than by locally visible source code.
*/

class FieldExample {
  private int i = 3;
  private int j = 4;

  public String toString() {
    return "FieldExample: i=" + i + ", j=" + j;
  }

  public void zeroI() {
    this.i = 0;
  }

  public void zeroField(String fieldName) {
    try {
      Field f = this.getClass().getDeclaredField(fieldName);
      // Subsequent access to field f passes language access checks
      // because zeroField() could have accessed the field via
      // ordinary field references
      f.setInt(this, 0);
      // Log appropriately or throw sanitized exception; see EXC06-J
    } catch (NoSuchFieldException ex) {
      // Report to handler
    } catch (IllegalAccessException ex) {
      // Report to handler
    }
  }

  public static void main(String[] args) {
    FieldExample fe = new FieldExample();
    System.out.println(fe.toString());
    for (String arg : args) {
      fe.zeroField(arg);
      System.out.println(fe.toString());
    }
  }
}
/*
Noncompliant Code Example
In this noncompliant code example, the programmer intends that code outside the Safe package should be prevented from creating a new instance of an arbitrary class. Consequently, the Trusted class uses a package-private constructor. However, because the API is public, an attacker can pass Trusted.class itself as an argument to the create() method and bypass the language access checks that prevent code outside the package from invoking the package-private constructor. The create() method returns an unauthorized instance of the Trusted class.

In the presence of a security manager s, the Class.newInstance() method throws a security exception when (a) s.checkMemberAccess(this, Member.PUBLIC) denies creation of new instances of this class or (b) the caller's class loader is not the same class loader or an ancestor of the class loader for the current class, and invocation of s.checkPackageAccess() denies access to the package of this class.

The checkMemberAccess method allows access to public members and classes that have the same class loader as the caller. However, the class loader comparison is often insufficient; for example, all applets share the same class loader by convention, consequently allowing a malicious applet to pass the security check in this case.
*/


package Safe;
public class Trusted {
  Trusted() { } // Package-private constructor
  public static <T> T create(Class<T> c)
      throws InstantiationException, IllegalAccessException {
    return c.newInstance();
  }
}

package Attacker;
import Safe.Trusted;

public class Attack {
  public static void main(String[] args)
      throws InstantiationException, IllegalAccessException {
    System.out.println(Trusted.create(Trusted.class)); // Succeeds
  }
}

/*
Related Vulnerabilities
CERT Vulnerability #636312 describes an exploit in Java that allows malicious code to disable any security manager currently in effect. Among other vulnerabilities, the attack code exploited the following method defined in sun.awt.SunToolkit, for Java 7:

This code operates inside a doPrivileged() block. It then uses the reflection method Class.getDeclaredField() to obtain a field given the field's class and name. This method would normally be blocked by a security manager. It then uses the reflection method Field.setAccessible() to make the field accessible, even if it were protected or private. But this method is public, so anyone can call it.
*/


public static Field getField(final Class klass, final String fieldName) {
  return AccessController.doPrivileged(new PrivilegedAction<Field>() {
       public Field run() {
           try {
               Field field = klass.getDeclaredField(fieldName);
               assert (field != null);
               field.setAccessible(true);
               return field;
           } catch (SecurityException e) {
               assert false;
           } catch (NoSuchFieldException e) {
               assert false;
           }
           return null;
       }//run
  });
}
