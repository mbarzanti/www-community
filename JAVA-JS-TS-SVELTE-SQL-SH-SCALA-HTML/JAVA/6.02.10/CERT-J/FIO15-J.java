/*
Noncompliant Code Example
This noncompliant code example illustrates a servlet that indicates if an internal error occurs by using the HttpServletResponse.sendError() method to indicate an internal server error.

If an IOException occurs after flushing the stream, the stream will be committed when the catch clause executes. Consequently, the sendError() operation will throw an IllegalStateException.
*/


public void doGet(HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException {

  ServletOutputStream out = response.getOutputStream();
  try {
    out.println("<html>");

    // ... Write some response text

    out.flush();  // Commits the stream

    // ... More work

  } catch (IOException x) {
    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  }
}

/*
Noncompliant Code Example
This noncompliant code example illustrates a servlet that indicates if an internal error occurs by printing an error message to the output stream and flushing it:

If an IOException occurs after flushing the stream, the stream will be reflushed in the catch clause.
*/

public void doGet(HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException {

  ServletOutputStream out = response.getOutputStream();
  try {
    out.println("<html>");

    // ... Write some response text

    out.flush();  // Commits the stream

    // ... More work

  } catch (IOException x) {
    out.println(x.getMessage());
    out.flush();
  }
}
