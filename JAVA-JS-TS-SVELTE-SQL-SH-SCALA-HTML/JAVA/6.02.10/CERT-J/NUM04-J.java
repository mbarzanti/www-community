/*
Noncompliant Code Example
This noncompliant code example performs some basic currency calculations:
Because the value 0.10 lacks an exact representation in Java floating-point type (or any floating-point format that uses a binary mantissa), on most platforms, this program prints the following:
A dollar less 7 dimes is $0.29999999999999993
*/

double dollar = 1.00;
double dime = 0.10;
int number = 7;
System.out.println(
  "A dollar less " + number + " dimes is $" + (dollar - number * dime) 
);
