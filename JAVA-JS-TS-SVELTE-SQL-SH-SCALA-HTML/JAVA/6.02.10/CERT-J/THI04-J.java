/*
Noncompliant Code Example (Blocking I/O, Volatile Flag)
This noncompliant code example uses a volatile done flag to indicate it whether is safe to shut down the thread, as suggested in THI05-J. Do not use Thread.stop() to terminate threads. However, when the thread is blocked on network I/O as a consequence of invoking the readLine() method, it cannot respond to the newly set flag until the network I/O is complete. Consequently, thread termination may be indefinitely delayed.
*/


// Thread-safe class 
public final class SocketReader implements Runnable { 
  private final Socket socket;
  private final BufferedReader in;
  private volatile boolean done = false;
  private final Object lock = new Object();

  public SocketReader(String host, int port) throws IOException {
    this.socket = new Socket(host, port);
    this.in = new BufferedReader(
        new InputStreamReader(this.socket.getInputStream())
    );
  }

  // Only one thread can use the socket at a particular time
  @Override public void run() {
    try {
      synchronized (lock) {
        readData();
      }
    } catch (IOException ie) {
      // Forward to handler
    }
  }

  public void readData() throws IOException {
    String string;
    while (!done && (string = in.readLine()) != null) {
      // Blocks until end of stream (null)
    }
  }

  public void shutdown() {
    done = true;
  }

  public static void main(String[] args) 
                          throws IOException, InterruptedException {
    SocketReader reader = new SocketReader("somehost", 25);
    Thread thread = new Thread(reader);
    thread.start();
    Thread.sleep(1000);
    reader.shutdown(); // Shut down the thread
  }
}
/*
Noncompliant Code Example (Blocking I/O, Interruptible)
This noncompliant code example is similar to the preceding example but uses thread interruption to shut down the thread. Network I/O on a java.net.Socket is unresponsive to thread interruption.
*/


// Thread-safe class 
public final class SocketReader implements Runnable { 
  // Other methods...

  public void readData() throws IOException {
    String string;
    while (!Thread.interrupted() && (string = in.readLine()) != null) {
      // Blocks until end of stream (null)
    }
  }

  public static void main(String[] args) 
                          throws IOException, InterruptedException {
    SocketReader reader = new SocketReader("somehost", 25);
    Thread thread = new Thread(reader);
    thread.start();
    Thread.sleep(1000);
    thread.interrupt(); // Interrupt the thread
  }
}

/*
Noncompliant Code Example (Database Connection)
This noncompliant code example shows a thread-safe DBConnector class that creates one JDBC connection per thread. Each connection belongs to one thread and is not shared by other threads. This is a common use case because JDBC connections are intended to be single-threaded.

Database connections, like sockets, lack inherent interruptibility. Consequently, this design fails to support the client's attempts to cancel a task by closing the resource when the corresponding thread is blocked on a long-running query, such as a join.
*/

public final class DBConnector implements Runnable {
  private final String query;

  DBConnector(String query) {
    this.query = query;
  }

  @Override public void run() {
    Connection connection;
    try {
      // Username and password are hard coded for brevity
      connection = DriverManager.getConnection(
          "jdbc:driver:name", 
          "username", 
          "password"
      );
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      // ...
    } catch (SQLException e) {
      // Forward to handler
    }
    // ...
  }

  public static void main(String[] args) throws InterruptedException {
    DBConnector connector = new DBConnector("suitable query");
    Thread thread = new Thread(connector);
    thread.start();
    Thread.sleep(5000);
    thread.interrupt();
  }
}
