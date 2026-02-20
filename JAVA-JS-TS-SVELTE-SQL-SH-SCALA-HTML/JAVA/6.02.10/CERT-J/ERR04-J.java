/*
Noncompliant Code Example
In this noncompliant code example, the finally block completes abruptly because of a return statement in the block:

The IllegalStateException is suppressed by the abrupt completion of the finally block caused by the return statement.
*/

class TryFinally {
  private static boolean doLogic() {
    try {
      throw new IllegalStateException();
    } finally {
      System.out.println("logic done");
      return true;
    }
  }
}


