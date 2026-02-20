/*
Noncompliant Code Example
In this code example, a class Account stores banking-related information without any inherent security. Security is delegated to the subclass BankAccount. The client application is required to use BankAccount because it contains the security mechanism.

*/

class Account { 
  // Maintains all banking-related data such as account balance
  private double balance = 100;

  boolean withdraw(double amount) {
    if ((balance - amount) >= 0) {
      balance -= amount;
      System.out.println("Withdrawal successful. The balance is : "
                         + balance);
      return true;
    }
    return false;
  }
}

public class BankAccount extends Account { 
  // Subclass handles authentication
  @Override boolean withdraw(double amount) {
    if (!securityCheck()) {
      throw new IllegalAccessException();
    }
    return super.withdraw(amount);
  }

  private final boolean securityCheck() {
    // Check that account management may proceed
  }
}

public class Client {
  public static void main(String[] args) {
    Account account = new BankAccount();
    // Enforce security manager check
    boolean result = account.withdraw(200.0);   
    System.out.println("Withdrawal successful? " + result);
  }
}
/*
At a later date, the maintainer of the Account class added a new method called overdraft(). However, the BankAccount class maintainer was unaware of the change. Consequently, the client application became vulnerable to malicious invocations. For example, the overdraft() method could be invoked directly on a BankAccount object, avoiding the security checks that should have been present. The following noncompliant code example shows this vulnerability:
*/

class Account { 
  // Maintains all banking-related data such as account balance
  private double balance = 100;

  boolean overdraft() {
    balance += 300;     // Add 300 in case there is an overdraft
    System.out.println("Added back-up amount. The balance is :" 
                       + balance);
    return true;
  }

  // Other Account methods
}

public class BankAccount extends Account { 
  // Subclass handles authentication
  // NOTE: unchanged from previous version
  // NOTE: lacks override of overdraft method
}

public class Client {
  public static void main(String[] args) {
    Account account = new BankAccount();
    // Enforce security manager check
    boolean result = account.withdraw(200.0);   
    if (!result) {
      result = account.overdraft();
    }
    System.out.println("Withdrawal successful? " + result);
  }
}
/*
Although this code works as expected, it adds a dangerous attack vector. Because the overdraft() method has no security check, a malicious client can invoke it without authentication:
*/
public class MaliciousClient {
  public static void main(String[] args) {
    Account account = new BankAccount();
    // No security check performed
    boolean result = account.overdraft();
    System.out.println("Withdrawal successful? " + result);
  }
}