/*
Noncompliant Code Example
This noncompliant code example shows a Dimensions class that contains three internal attributes: the length, width, and height of a rectangular box. The getVolumePackage() method is designed to return the total volume required to hold the box after accounting for packaging material, which adds 2 units to the dimensions of each side. Nonpositive values of the dimensions of the box (exclusive of packaging material) are rejected during input validation. No dimension can be larger than 10. Also, the weight of the object is passed in as an argument and cannot be more than 20 units.

If the weight is more than 20 units, it causes an IllegalArgumentException, which is intercepted by the custom error reporter. Although the logic restores the object's original state in the absence of this exception, the rollback code fails to execute in the event of an exception. Consequently, subsequent invocations of getVolumePackage() produce incorrect results.

The catch clause is permitted by exception ERR08-J-EX0 in ERR08-J. Do not catch NullPointerException or any of its ancestors because it serves as a general filter passing exceptions to the MyExceptionReporter class, which is dedicated to safely reporting exceptions as recommended by ERR00-J. Do not suppress or ignore checked exceptions. Although this code only throws IllegalArgumentException, the catch clause is general enough to handle any exception in case the try block should be modified to throw other exceptions.
*/

class Dimensions {
  private int length;
  private int width;
  private int height;
  static public final int PADDING = 2;
  static public final int MAX_DIMENSION = 10;

  public Dimensions(int length, int width, int height) {
    this.length = length;
    this.width = width;
    this.height = height;
  }

  protected int getVolumePackage(int weight) {
    length += PADDING;
    width  += PADDING;
    height += PADDING;
    try {
      if (length <= PADDING || width <= PADDING || height <= PADDING ||
        length > MAX_DIMENSION + PADDING || width > MAX_DIMENSION + PADDING ||
        height > MAX_DIMENSION + PADDING || weight <= 0 || weight > 20) {
        throw new IllegalArgumentException();
      }

      int volume = length * width * height;
      length -= PADDING; width -= PADDING; height -= PADDING; // Revert
      return volume;
    } catch (Throwable t) {
      MyExceptionReporter mer = new MyExceptionReporter();
      mer.report(t); // Sanitize
      return -1; // Non-positive error code
    }
  }

  public static void main(String[] args) {
    Dimensions d = new Dimensions(8, 8, 8);
    System.out.println(d.getVolumePackage(21)); // Prints -1 (error)
    System.out.println(d.getVolumePackage(19));
    // Prints 1728 (12x12x12) instead of 1000 (10x10x10)
  }
}

