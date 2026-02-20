/*
Noncompliant Code Example
This noncompliant code example shows the use of regular sockets for a server application that fails to protect sensitive information in transit. The insecure code for the corresponding client application follows the server's code.

Note that the sockets are properly closed in accordance with ERR05-J. Do not let checked exceptions escape from a finally block.
*/

  
// Exception handling has been omitted for the sake of brevity
class EchoServer { 
  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(9999); 
      Socket socket = serverSocket.accept();
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true); 
      BufferedReader in = new BufferedReader(
          new InputStreamReader(socket.getInputStream())); 
      String inputLine; 
      while ((inputLine = in.readLine()) != null) { 
        System.out.println(inputLine); 
        out.println(inputLine); 
      }
    } finally {
      if (serverSocket != null) {
        try {
          serverSocket.close();
        } catch (IOException x) {
          // Handle error
        }
      }
    }
  }
}

class EchoClient {
  public static void main(String[] args) 
                          throws UnknownHostException, IOException {
    Socket socket = null;
    try {
      socket = new Socket("localhost", 9999);
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(
          new InputStreamReader(socket.getInputStream()));
      BufferedReader stdIn = new BufferedReader(
          new InputStreamReader(System.in));
      String userInput;
      while ((userInput = stdIn.readLine()) != null) {
        out.println(userInput);
        System.out.println(in.readLine());
      } 
    } finally {
      if (socket != null) {
        try {
          socket.close();
        } catch (IOException x) {
          // Handle error
        }
      }
    }
  }
}
