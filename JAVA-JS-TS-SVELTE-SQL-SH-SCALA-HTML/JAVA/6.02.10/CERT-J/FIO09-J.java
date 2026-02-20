/*
Noncompliant Code Example
This noncompliant code example accepts a value from the user without validating it. Any value that is not in the range of 0 to 255 is truncated. For instance, write(303) prints / on ASCII-based systems because the lower-order 8 bits of 303 are used while the 24 high-order bits are ignored (303 % 256 = 47, which is the ASCII code for /). That is, the result is the remainder of the input divided by 256.
*/


class ConsoleWrite {
  public static void main(String[] args) { 
    // Any input value > 255 will result in unexpected output
    System.out.write(Integer.valueOf(args[0]));
    System.out.flush();
  }
}