/*
Noncompliant Code Example (AtomicReference)
This noncompliant code example wraps references to BigInteger objects within thread-safe AtomicReference objects:

AtomicReference is an object reference that can be updated atomically. However, operations that combine more than one atomic reference are non-atomic. In this noncompliant code example, one thread may call update() while a second thread may call add(). This might cause the add() method to add the new value of first to the old value of second, yielding an erroneous result.
*/


final class Adder {
  private final AtomicReference<BigInteger> first;
  private final AtomicReference<BigInteger> second;

  public Adder(BigInteger f, BigInteger s) {
    first  = new AtomicReference<BigInteger>(f);
    second = new AtomicReference<BigInteger>(s);
  }

  public void update(BigInteger f, BigInteger s) { // Unsafe
    first.set(f);
    second.set(s);
  }

  public BigInteger add() { // Unsafe
    return first.get().add(second.get());
  }
}

/*
Noncompliant Code Example (synchronizedList())
This noncompliant code example uses a java.util.ArrayList<E> collection, which is not thread-safe. However, the example uses Collections.synchronizedList as a synchronization wrapper for the ArrayList. It subsequently uses an array, rather than an iterator, to iterate over the ArrayList to avoid a ConcurrentModificationException.

Individually, the add() and toArray() collection methods are atomic. However, when called in succession (as shown in the addAndPrintIPAddresses() method), there is no guarantee that the combined operation is atomic. The addAndPrintIPAddresses() method contains a race condition that allows one thread to add to the list and a second thread to race in and modify the list before the first thread completes. Consequently, the addressCopy array may contain more IP addresses than expected.
*/

final class IPHolder {
  private final List<InetAddress> ips = 
      Collections.synchronizedList(new ArrayList<InetAddress>());

  public void addAndPrintIPAddresses(InetAddress address) {
    ips.add(address);
    InetAddress[] addressCopy = 
        (InetAddress[]) ips.toArray(new InetAddress[0]);
    // Iterate through array addressCopy ...
  }
}

/*
Noncompliant Code Example (synchronizedMap())
This noncompliant code example defines the KeyedCounter class that is not thread-safe. Although the HashMap is wrapped in a synchronizedMap(), the overall increment operation is not atomic [Lee 2009].
*/


final class KeyedCounter {
  private final Map<String, Integer> map =
      Collections.synchronizedMap(new HashMap<String, Integer>());

  public void increment(String key) {
    Integer old = map.get(key);
    int oldValue = (old == null) ? 0 : old.intValue();
    if (oldValue == Integer.MAX_VALUE) {
      throw new ArithmeticException("Out of range");
    }
    map.put( key, oldValue + 1);
  }

  public Integer getCount(String key) {
    return map.get(key);
  }
}


