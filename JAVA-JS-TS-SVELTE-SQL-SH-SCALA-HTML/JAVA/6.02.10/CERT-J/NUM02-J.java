/*
Noncompliant Code Example (Division)
The result of the / operator is the quotient from the division of the first arithmetic operand by the second arithmetic operand. Division operations are susceptible to divide-by-zero errors. Overflow can also occur during two's-complement signed integer division when the dividend is equal to the minimum (negative) value for the signed integer type and the divisor is equal to −1 (see NUM00-J. Detect or prevent integer overflow for more information). This noncompliant code example can result in a divide-by-zero error during the division of the signed operands num1 and num2:
*/
long num1, num2, result;

/* Initialize num1 and num2 */

result = num1 / num2;

/*
Noncompliant Code Example (Remainder)
The % operator provides the remainder when two operands of integer type are divided. This noncompliant code example can result in a divide-by-zero error during the remainder operation on the signed operands num3 and num4:
*/
long num3, num4, result2;

/* Initialize num3 and num4 */

result2 = num3 % num4;