/*
Noncompliant Code Example
This noncompliant code example associates credit card numbers with strings using a HashMap and subsequently attempts to retrieve the string value associated with a credit card number. The expected retrieved value is 4111111111111111; the actual retrieved value is null.
The cause of this erroneous behavior is that the CreditCard class overrides the equals() method but fails to override the hashCode() method. Consequently, the default hashCode() method returns a different value for each object, even though the objects are logically equivalent; these differing values lead to examination of different buckets in the hash table, which prevents the get() method from finding the intended value.
Note that by specifying the credit card number in main(), these code examples violate MSC03-J. Never hard code sensitive information for the sake of brevity.
*/

public final class CreditCard {
  private final int number;

  public CreditCard(int number) {
    this.number = number;
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } 
    if (!(o instanceof CreditCard)) {
      return false;
    }
    CreditCard cc = (CreditCard)o;
    return cc.number == number; 
  }

  public static void main(String[] args) {
    Map<CreditCard, String> m = new HashMap<CreditCard, String>();
    m.put(new CreditCard(100), "4111111111111111");
    System.out.println(m.get(new CreditCard(100)));  
  }
}