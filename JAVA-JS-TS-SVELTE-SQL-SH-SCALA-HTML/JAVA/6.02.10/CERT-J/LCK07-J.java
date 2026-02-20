/*
Noncompliant Code Example (Different Lock Orders)
This noncompliant code example can deadlock because of excessive synchronization. The balanceAmount field represents the total balance available for a particular BankAccount object. Users are allowed to initiate an operation that atomically transfers a specified amount from one account to another.

Objects of this class are prone to deadlock. An attacker who has two bank accounts can construct two threads that initiate balance transfers from two different BankAccount object instances a and b. For example, consider the following code:



BankAccount a = new BankAccount(5000);
BankAccount b = new BankAccount(6000);
BankAccount.initiateTransfer(a, b, 1000); // starts thread 1
BankAccount.initiateTransfer(b, a, 1000); // starts thread 2
Each transfer is performed in its own thread. The first thread atomically transfers the amount from a to b by depositing it in account b and then withdrawing the same amount from a. The second thread performs the reverse operation; that is, it transfers the amount from b to a. When executing depositAmount(), the first thread acquires a lock on object a. The second thread could acquire a lock on object b before the first thread can. Subsequently, the first thread would request a lock on b, which is already held by the second thread. The second thread would request a lock on a, which is already held by the first thread. This constitutes a deadlock condition because neither thread can proceed.
*/

final class BankAccount {
  private double balanceAmount;  // Total amount in bank account

  BankAccount(double balance) {
    this.balanceAmount = balance;
  }

  // Deposits the amount from this object instance
  // to BankAccount instance argument ba
  private void depositAmount(BankAccount ba, double amount) {
    synchronized (this) {
      synchronized (ba) {
        if (amount > balanceAmount) {
          throw new IllegalArgumentException(
               "Transfer cannot be completed"
          );
        }
        ba.balanceAmount += amount;
        this.balanceAmount -= amount;
      }
    }
  }

  public static void initiateTransfer(final BankAccount first,
    final BankAccount second, final double amount) {

    Thread transfer = new Thread(new Runnable() {
        public void run() {
          first.depositAmount(second, amount);
        }
    });
    transfer.start();
  }
}


This noncompliant code example may or may not deadlock, depending on the scheduling details of the platform. Deadlock occurs when (1) two threads request the same two locks in different orders, and (2) each thread obtains a lock that prevents the other thread from completing its transfer. Deadlock is avoided when two threads request the same two locks but one thread completes its transfer before the other thread begins. Similarly, deadlock is avoided if the two threads request the same two locks in the same order (which would happen if they both transfer money from one account to a second account) or if two transfers involving distinct accounts occur concurrently.

/*
Noncompliant Code Example (Different Lock Orders, Recursive)
The following immutable WebRequest class encapsulates a web request received by a server:
Each request has a response time associated with it, along with a measurement of the network bandwidth required to fulfill the request.
*/

// Immutable WebRequest
public final class WebRequest {
  private final long bandwidth;
  private final long responseTime;

  public WebRequest(long bandwidth, long responseTime) {
    this.bandwidth = bandwidth;
    this.responseTime = responseTime;
  }

  public long getBandwidth() {
    return bandwidth;
  }

  public long getResponseTime() {
    return responseTime;
  }
}

/*
This noncompliant code example monitors web requests and provides routines for calculating the average bandwidth and response time required to serve incoming requests.

The monitoring application is built around the WebRequestAnalyzer class, which maintains a list of web requests using the requests vector and includes the addWebRequest() setter method. Any thread can request the average bandwidth or average response time of all web requests by invoking the getAverageBandwidth() and getAverageResponseTime() methods.

These methods use fine-grained locking by holding locks on individual elements (web requests) of the vector. These locks permit new requests to be added while the computations are still underway. Consequently, the statistics reported by the methods are accurate when they return the results.

Unfortunately, this noncompliant code example is prone to deadlock because the recursive calls within the synchronized regions of these methods acquire the intrinsic locks in opposite numerical orders. That is, calculateAverageBandwidth() requests locks from index 0 up to requests.size() − 1, whereas calculateAverageResponseTime() requests them from index requests.size() − 1 down to 0. Because of recursion, previously acquired locks are never released by either method. Deadlock occurs when two threads call these methods out of order, because one thread calls calculateAverageBandwidth(), while the other calls calculateAverageResponseTime() before either method has finished executing.

For example, when there are 20 requests in the vector, and one thread calls getAverageBandwidth(), the thread acquires the intrinsic lock of WebRequest 0, the first element in the vector. Meanwhile, if a second thread calls getAverageResponseTime(), it acquires the intrinsic lock of WebRequest 19, the last element in the vector. Consequently, deadlock results because neither thread can acquire all of the locks required to proceed with its calculations.

Note that the addWebRequest() method also has a race condition with calculateAverageResponseTime(). While iterating over the vector, new elements can be added to the vector, invalidating the results of the previous computation. This race condition can be prevented by locking on the last element of the vector (when it contains at least one element) before inserting the element.
*/

public final class WebRequestAnalyzer {
  private final Vector<WebRequest> requests = new Vector<WebRequest>();

  public boolean addWebRequest(WebRequest request) {
    return requests.add(new WebRequest(request.getBandwidth(),
                        request.getResponseTime()));
  }

  public double getAverageBandwidth() {
    if (requests.size() == 0) {
      throw new IllegalStateException("The vector is empty!");
    }
    return calculateAverageBandwidth(0, 0);
  }

  public double getAverageResponseTime() {
    if (requests.size() == 0) {
      throw new IllegalStateException("The vector is empty!");
    }
    return calculateAverageResponseTime(requests.size() - 1, 0);
  }

  private double calculateAverageBandwidth(int i, long bandwidth) {
    if (i == requests.size()) {
      return bandwidth / requests.size();
    }
    synchronized (requests.elementAt(i)) {
      bandwidth += requests.get(i).getBandwidth();
      // Acquires locks in increasing order
      return calculateAverageBandwidth(++i, bandwidth);
    }
  }

  private double calculateAverageResponseTime(int i, long responseTime) {
    if (i <= -1) {
      return responseTime / requests.size();
    }
    synchronized (requests.elementAt(i)) {
      responseTime += requests.get(i).getResponseTime();
      // Acquires locks in decreasing order
      return calculateAverageResponseTime(--i, responseTime);
    }
  }
}
