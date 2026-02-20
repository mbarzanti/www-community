/*
Noncompliant Code Example
This noncompliant code example uses the insecure java.util.Random class. This class produces an identical sequence of numbers for each given seed value; consequently, the sequence of numbers is predictable.
*/


import java.util.Random;
// ...

Random number = new Random(123L);
//...
for (int i = 0; i < 20; i++) {
  // Generate another random integer in the range [0, 20]
  int n = number.nextInt(21);
  System.out.println(n);
}