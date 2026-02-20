/*
Noncompliant Code Example
In this noncompliant code example, the programmer hides the static method rather than overriding it. Consequently, the code invokes the displayAccountStatus() method of the superclass, causing it to print  Account details for admin despite being instructed to choose user rather than admin.
*/


class GrantAccess {
  public static void displayAccountStatus() {
    System.out.println("Account details for admin: XX");
  }
}

class GrantUserAccess extends GrantAccess {
  public static void displayAccountStatus() {
    System.out.println("Account details for user: XX");
  }
}

public class StatMethod {
  public static void choose(String username) {
    GrantAccess admin = new GrantAccess();
    GrantAccess user = new GrantUserAccess();
    if (username.equals("admin")) {
      admin.displayAccountStatus();
    } else {
      user.displayAccountStatus();
    }
  }

  public static void main(String[] args) {
    choose("user");
  }
}