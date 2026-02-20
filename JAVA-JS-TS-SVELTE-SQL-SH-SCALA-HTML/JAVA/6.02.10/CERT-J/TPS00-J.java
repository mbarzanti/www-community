/*
Noncompliant Code Example (Thread-Per-Message)
This noncompliant code example demonstrates the Thread-Per-Message design pattern. The RequestHandler class provides a public static factory method so that callers can obtain a RequestHandler instance. The handleRequest() method is subsequently invoked to handle each request in its own thread.

The thread-per-message strategy fails to provide graceful degradation of service. As threads are created, processing continues normally until some scarce resource is exhausted. For example, a system may allow only a limited number of open file descriptors even though additional threads can be created to serve requests. When the scarce resource is memory, the system may fail abruptly, resulting in a DoS.
*/

class Helper {
  public void handle(Socket socket) {
    // ...
  }
}

final class RequestHandler {
  private final Helper helper = new Helper();
  private final ServerSocket server;

  private RequestHandler(int port) throws IOException {
    server = new ServerSocket(port);
  }

  public static RequestHandler newInstance() throws IOException {
    return new RequestHandler(0); // Selects next available port
  }

  public void handleRequest() {
    new Thread(new Runnable() {
        public void run() {
          try {
            helper.handle(server.accept());
          } catch (IOException e) {
            // Forward to handler
          }
        }
    }).start();
  }

}
