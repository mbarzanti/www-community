/*
Noncompliant Code Example
This noncompliant code example creates a servlet that prompts the user for an email address, then repeats the address back to the user. The previous address is stored in the lastAddr variable, which is an instance field.

Because the HttpServlet class is a singleton, there is only one lastAddr field shared by every client who accesses the servlet. Consequently, the contents of the lastAddr field can be the previous setting of the field by a different client. Also, because this code example lacks thread-safety, it is possible for the lastAddr field to take on a stale value should two clients request the parameter simultaneously, which violates VNA01-J. Ensure visibility of shared references to immutable objects.
*/

public class SampleServlet extends HttpServlet {

  private String lastAddr = "nobody@nowhere.com";

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    out.println("<html>");

    String emailAddr = request.getParameter("emailAddr");

    if (emailAddr != null) {
      out.println("Email Address:");
      out.println(sanitize(emailAddr));
      out.println("<br>Previous Address:");
      out.println(sanitize(lastAddr));
    };

    out.println("<p>");
    out.print("<form action=\"");
    out.print("SampleServlet\" ");
    out.println("method=POST>");
    out.println("Parameter:");
    out.println("<input type=text size=20 name=emailAddr>");
    out.println("<br>");
    out.println("<input type=submit>");
    out.println("</form>");

    lastAddr = emailAddr;
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    doGet(request, response);
  }

  // Filter the specified message string for characters
  // that are sensitive in HTML.
  public static String sanitize(String message) {
    // ...
  }
}

/*
Noncompliant Code Example
In this noncompliant code example, the lastAddr field is static. It more accurately reflects the fact that there is never more than a single instance of the field. However, this code has the same behavior as the previous noncompliant code example and also violates VNA01-J. Ensure visibility of shared references to immutable objects.
*/


public class SampleServlet extends HttpServlet {

  private static String lastAddr = "nobody@nowhere.com";

  // ... Other methods unchanged
}
/*
Noncompliant Code Example
In this noncompliant code example, the lastAddr field is static and is protected from concurrent access by a separate lock object, as is recommended by LCK00-J. Use private final lock objects to synchronize classes that may interact with untrusted code. This approach guarantees thread-safety in the servlet. However, the servlet can still return the email address provided by a different session.

*/

public class SampleServlet extends HttpServlet {
 
  private static String lastAddr = "nobody@nowhere.com";
  private static final Object lastAddrLock = new Object();

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    out.println("<html>");
 
    String emailAddr = request.getParameter("emailAddr");
 
    if (emailAddr != null) {
      out.println("Email Address::");
      out.println(sanitize(emailAddr));
      synchronized (lock) {
        out.println("<br>Previous Email Address::");
        out.println(sanitize(lastAddr));
      }
    };
 
    out.println("<p>");
    out.print("<form action=\"");
    out.print("SampleServlet\" ");
    out.println("method=POST>");
    out.println("Parameter:");
    out.println("<input type=text size=20 name=emailAddr>");
    out.println("<br>");
    out.println("<input type=submit>");
    out.println("</form>");
 
    synchronized (lock) {
      lastAddr = emailAddr;
    }
  }
 
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    doGet(request, response);
  }

  // Filter the specified message string for characters
  // that are sensitive in HTML.
  public static String sanitize(String message) {
    // ...
  }
}