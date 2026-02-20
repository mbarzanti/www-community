/*
Noncompliant Code Example
This noncompliant code example exposes the private (x,y) coordinates through the getPoint() method of the inner class. Consequently, the AnotherClass class that belongs to the same package can also access the coordinates.
*/


class Coordinates {
  private int x;
  private int y;

  public class Point {
    public void getPoint() {
      System.out.println("(" + x + "," + y + ")");
    }
  }
}

class AnotherClass {
  public static void main(String[] args) {
    Coordinates c = new Coordinates();
    Coordinates.Point p = c.new Point();
    p.getPoint();
  }
}