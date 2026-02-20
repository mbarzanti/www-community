/*
Noncompliant Code Example (Background Thread)
In this noncompliant code example, the static initializer starts a background thread as part of class initialization. The background thread attempts to initialize a database connection but should wait until all members of the ConnectionFactory class, including dbConnection, are initialized.

Statically initialized fields are guaranteed to be fully constructed before they are made visible to other threads (see TSM03-J. Do not publish partially initialized objects for more information). Consequently, the background thread must wait for the main (or foreground) thread to finish initialization before it can proceed. However, the ConnectionFactory class's main thread invokes the join() method, which waits for the background thread to finish. This interdependency causes a class initialization cycle that results in a deadlock situation [Bloch 2005b].

Similarly, it is inappropriate to start threads from constructors (see TSM01-J. Do not let the this reference escape during object construction for more information). Creating timers that perform recurring tasks and starting those timers from within code responsible for initialization also introduces liveness issues.
*/

public final class ConnectionFactory {
  private static Connection dbConnection;
  // Other fields ...

  static {
    Thread dbInitializerThread = new Thread(new Runnable() {
        @Override public void run() {
          // Initialize the database connection
          try {
            dbConnection = DriverManager.getConnection("connection string");
          } catch (SQLException e) {
            dbConnection = null;
          }
        }
    });

    // Other initialization, for example, start other threads

    dbInitializerThread.start();
    try {
      dbInitializerThread.join();
    } catch (InterruptedException ie) {
      throw new AssertionError(ie);
    }
  }

  public static Connection getConnection() {
    if (dbConnection == null) {
      throw new IllegalStateException("Error initializing connection");
    }
    return dbConnection;
  }

  public static void main(String[] args) {
    // ...
    Connection connection = getConnection();
  }
}


