/*
Noncompliant Code Example (Integer Narrowing)
In this noncompliant code example, a value of type int is converted to a value of type byte without range checking:
*/


class CastAway {
  public static void main(String[] args) {
    int i = 128;
    workWith(i);
  }

  public static void workWith(int i) {
    byte b = (byte) i;  // b has value -128
    // Work with b
  }
}
/*
Noncompliant Code Example (Floating-Point to Integer Conversion)
The narrowing primitive conversions in this noncompliant code example suffer from loss in the magnitude of the numeric value as well as a loss of precision:
The minimum and maximum float values are converted to 0 and maximum int values (0x7fffffff respectively). The resulting short values are 0 and the lower 16 bits of this value (0xffff). The resulting final values (0 and −1) might be unexpected.
*/

float i = Float.MIN_VALUE;
float j = Float.MAX_VALUE;
short b = (short) i;
short c = (short) j;

/*
Noncompliant Code Example (double to float Conversion)
The narrowing primitive conversions in this noncompliant code example suffer from a loss in the magnitude of the numeric value as well as a loss of precision. Because Double.MAX_VALUE is larger than Float.MAX_VALUE, c receives the value infinity, and because Double.MIN_VALUE is smaller than Float.MIN_VALUE, b receives the value 0.
*/
double i = Double.MIN_VALUE;
double j = Double.MAX_VALUE;
float b = (float) i;
float c = (float) j;