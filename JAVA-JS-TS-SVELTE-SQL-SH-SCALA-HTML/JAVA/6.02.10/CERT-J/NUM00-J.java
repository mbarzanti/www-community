/* Either operation in this noncompliant code example could result in an overflow. When overflow occurs, the result will be incorrect.
*/
public static int multAccum(int oldAcc, int newVal, int scale) {
  // May result in overflow
  return oldAcc + (newVal * scale);
}