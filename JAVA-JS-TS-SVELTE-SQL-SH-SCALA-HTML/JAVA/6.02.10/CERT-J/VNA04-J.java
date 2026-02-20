/*
Noncompliant Code Example
In this noncompliant code example, if one thread repeatedly calls the assignValue() method and another thread repeatedly calls the printLong() method, the printLong() method could occasionally print a value of i that is neither zero nor the value of the j argument:

*/

class LongContainer {
  private long i = 0;

  void assignValue(long j) {
    i = j;
  }

  void printLong() {
    System.out.println("i = " + i);
  }
}
/*
A similar problem can occur when i is declared double.
*/

