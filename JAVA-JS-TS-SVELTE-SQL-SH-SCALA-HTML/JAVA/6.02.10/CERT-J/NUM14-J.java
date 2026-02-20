/*
Noncompliant Code Example (Arithmetic vs. Logical)
In this noncompliant code example, method countOneBits loops forever on negative inputs because the >> operator performs an arithmetic shift rather than a logical shift:
*/
static int countOneBits(long value) {
  int bits = 0;
  while (value != 0) {
    bits += value & 1L;
    value >>= 1; // Signed right shift, by one
  }
  return bits;
}

/*
Noncompliant Code Example (Promotion)
In this noncompliant code example, the programmer intends to shift a byte value two bits to the right (with zero fill). However, the JLS specifies that the left operand must be promoted to either type int or type long (int, in this case); this promotion performs sign extension. Because of the promotion, the result of the shift for negative input values will be a large positive number, and the programmer could find this result surprising.
*/
byte b = /* Initialize */;
int result = b >>> 2;

/*
Noncompliant Code Example (Truncation)
This noncompliant code example fails to perform explicit range-checking to avoid truncation of the shift distance:
*/
public int doOperation(int exp) {
  // Compute 2^exp
  int temp = 1 << exp;
  // Do other processing
  return temp;
}