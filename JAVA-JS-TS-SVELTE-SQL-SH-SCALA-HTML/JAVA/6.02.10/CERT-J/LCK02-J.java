/*
Noncompliant Code Example (getClass() Lock Object)
In this noncompliant code example, the parse() method of the Base class parses a date and synchronizes on the class object returned by getClass(). The Derived class also inherits the parse() method. However, this inherited method synchronizes on Derived's class object because the inherited parse method's invocation of getClass() is really an invocation of this.getClass(), and the this argument is a reference to the instance of the Derived class.

The Derived class also adds a doSomethingAndParse() method that locks on the class object of the Base class because the developer misconstrued that the parse() method in Base always obtains a lock on the Base class object, and doSomethingAndParse() must follow the same locking policy. Consequently, the Derived class has two different locking strategies and fails to be thread-safe.

*/

class Base {
  static DateFormat format =
      DateFormat.getDateInstance(DateFormat.MEDIUM);

  public Date parse(String str) throws ParseException {
    synchronized (getClass()) {
      return format.parse(str);
    }
  }
}

class Derived extends Base {
  public Date doSomethingAndParse(String str) throws ParseException {
    synchronized (Base.class) {
      // ...
      return format.parse(str);
    }
  }
}

/*
Noncompliant Code Example (getClass() Lock Object, Inner Class)
This noncompliant code example synchronizes on the class object returned by getClass() in the parse() method of class Base. The Base class also has a nested Helper class whose doSomethingAndParse() method incorrectly synchronizes on the value returned by getClass().

The call to getClass() in the Helper class returns a Helper class object instead of the Base class object. Consequently, a thread that calls Base.parse() locks on a different object than a thread that calls Base.doSomething(). It is easy to overlook concurrency errors in inner classes because they exist within the body of the containing outer class. A reviewer might incorrectly assume that the two classes have the same locking strategy.
*/

class Base {
  static DateFormat format =
      DateFormat.getDateInstance(DateFormat.MEDIUM);

  public Date parse(String str) throws ParseException {
    synchronized (getClass()) { // Intend to synchronizes on Base.class
      return format.parse(str);
    }
  }

  public Date doSomething(String str) throws ParseException {
    return new Helper().doSomethingAndParse(str);
  }

  private class Helper {
    public Date doSomethingAndParse(String str) throws ParseException {
      synchronized (getClass()) { // Synchronizes on Helper.class
        // ...
        return format.parse(str);
      }
    }
  }
}
