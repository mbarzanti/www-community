/*
Noncompliant Code Example (Left Shift)
Left- and right-shift operators are often employed to multiply or divide a number by a power of two. This approach compromises code readability and portability for the sake of often-illusory speed gains. The Java Virtual Machine (JVM) usually makes such optimizations automatically, and, unlike a programmer, the JVM can optimize for the implementation details of the current platform. This noncompliant code example includes both bitwise and arithmetic manipulations of the integer x that conceptually contains a numeric value. The result is a prematurely optimized statement that assigns the value 5x + 1 to x, which is what the programmer intended to express.
*/
int compute(int x) {
  x += (x << 2) + 1;
  return x;
}
// ...

int x = compute(50);

/*
Noncompliant Code Example (Left Shift)
This noncompliant code example segregates arithmetic and bitwise operators by variables. The x variable participates only in bitwise operations, and y participates only in arithmetic operations.
This example is noncompliant because the actual data has both bitwise and arithmetic operations performed on it, even though the operations are performed on different variables.
*/

int compute2(int x) {
  int y = x << 2;
  x += y + 1;
  return x;
}
// ...

int x = compute2(50);

/*
Noncompliant Code Example (Logical Right Shift)
In this noncompliant code example, the programmer wishes to divide x by 4. In a misguided attempt to optimize performance, the programmer uses a right-shift operation rather than a division operation.
The >>>= operator is a logical right shift; it fills the leftmost bits with zeroes, regardless of the number's original sign. After execution of this code sequence, x contains a large positive number (specifically, 0x3FFFFFF3). Using logical right shift for division produces an incorrect result when the dividend (x in this example) contains a negative value.
*/

int compute3(int x) {
  x >>>= 2;
  return x;
}
// ...

int x = compute3(-50);

/*
Noncompliant Code Example (Arithmetic Right Shift)
In this noncompliant code example, the programmer attempts to correct the previous example by using an arithmetic right shift (the >>= operator):
After this code sequence is run, x contains the value -13 rather than the expected -12. Arithmetic right shift truncates the resulting value toward negative infinity, whereas integer division truncates toward zero.
*/

int compute4(int x) {
  x >>= 2;
  return x;
}
// ...

int x = compute4(-50);

/*
Noncompliant Code Example
In this noncompliant code example, a programmer attempts to fetch four values from a byte array and pack them into the integer variable result. The integer value in this example represents a bit collection, not a numeric value.
In the bitwise operation, the value of the byte array element b[i] is promoted to an int by sign extension. When a byte array element contains a negative value (for example, 0xff), the sign extension propagates 1-bits into the upper 24 bits of the int. This behavior might be unexpected if the programmer is assuming that byte is an unsigned type. In this example, adding the promoted byte values to result fails to result in a packed integer representation of the bytes [FindBugs 2008].

See NUM01-J-EX1 for details about doing similar calculations for the purpose of serializing numbers into bytes.
*/
// b[] is a byte array, initialized to 0xff
byte[] b = new byte[] {-1, -1, -1, -1};
int result = 0;
for (int i = 0; i < 4; i++) {
  result = ((result << 8) + b[i]);
}

/*
Noncompliant Code Example
This noncompliant code example masks off the upper 24 bits of the promoted byte array element before performing the addition. The number of bits required to mask the sizes of byte and int are specified by The Java Language Specification. Although this code calculates the correct result, it violates this rule by combining bitwise and arithmetic operations on the same data.
*/
byte[] b = new byte[] {-1, -1, -1, -1};
int result = 0;
for (int i = 0; i < 4; i++) {
  result = ((result << 8) + (b[i] & 0xff));
}