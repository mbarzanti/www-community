/*
Noncompliant Code Example
In this noncompliant code example, two identical large integer literals are passed as arguments to the subFloatFromInt() method. The second argument is coerced to float, cast back to int, and subtracted from a value of type int. The result is returned as a value of type int.

This method could have unexpected results because of the loss of precision. In FP-strict mode, values of type float have 23 mantissa bits, a sign bit, and an 8-bit exponent (see NUM53-J. Use the strictfp modifier for floating-point calculation consistency across platforms for more information about FP-strict mode). The exponent allows type float to represent a larger range than that of type int. However, the 23-bit mantissa means that float supports exact representation only of integers whose representation fits within 23 bits; float supports only approximate representation of integers outside that range.
Note that conversions from long to either float or double can lead to similar loss of precision.
*/
strictfp class WideSample {
  public static int subFloatFromInt(int op1, float op2) {
    return op1 - (int)op2;
  }

  public static void main(String[] args) {
    int result = subFloatFromInt(1234567890, 1234567890);
    // This prints -46, not 0, as may be expected
    System.out.println(result);  
  }
}
