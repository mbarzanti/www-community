/*
Noncompliant Code Example
This noncompliant code example uses a floating-point variable as a loop counter. The decimal number 0.1 cannot be precisely represented as a float or even as a double.
Because 0.1f is rounded to the nearest value that can be represented in the value set of the float type, the actual quantity added to x on each iteration is somewhat larger than 0.1. Consequently, the loop executes only nine times and typically fails to produce the expected output.
*/
for (float x = 0.1f; x <= 1.0f; x += 0.1f) {
  System.out.println(x);
}
