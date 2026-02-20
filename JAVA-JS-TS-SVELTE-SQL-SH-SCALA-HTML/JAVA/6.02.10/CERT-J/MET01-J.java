/*
Noncompliant Code Example
The method getAbsAdd() computes and returns the sum of the absolute value of parameters x and y. It lacks argument validation, in violation of MET00-J. Validate method arguments. Consequently, it can produce incorrect results because of integer overflow or when either or both of its arguments are Integer.MIN_VALUE.
The conditions checked by the assertions are reasonable. However, the validation code is not executed when assertions are disabled.
*/


public static int getAbsAdd(int x, int y) {
  return Math.abs(x) + Math.abs(y);
}
getAbsAdd(Integer.MIN_VALUE, 1);
Noncompliant Code Example
This noncompliant code example uses assertions to validate arguments of a public method:



public static int getAbsAdd(int x, int y) {
  assert x != Integer.MIN_VALUE;
  assert y != Integer.MIN_VALUE;
  int absX = Math.abs(x);
  int absY = Math.abs(y);
  assert (absX <= Integer.MAX_VALUE - absY);
  return absX + absY;
}


