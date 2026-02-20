/*
Noncompliant Code Example
This noncompliant code example accepts user data without validating it:

This code produces unexpected results when an exceptional value is entered for val and subsequently used in calculations or as control values. The user could, for example, input the strings infinity or NaN on the command line, which would be parsed by Double.valueOf(String s) into the floating-point representations of either infinity or NaN. All subsequent calculations using these values would be invalid, possibly causing runtime exceptions or enabling denial-of-service (DoS) attacks.

In this noncompliant example, entering NaN for val would cause currentBalance to be set to NaN, corrupting its value. If this value were used in other expressions, every resulting value would also become NaN, possibly corrupting important data.
*/

double currentBalance; // User's cash balance

void doDeposit(String userInput) {
  double val = 0;
  try {
    val = Double.valueOf(userInput);
  } catch (NumberFormatException e) {
    // Handle input format error
  }

  if (val >= Double.MAX_VALUE - currentBalance) {
    // Handle range error
  }

  currentBalance += val;
}
