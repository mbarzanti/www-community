/*
Noncompliant Code Example
In this noncompliant code example, a server logs the IP address of the remote client in the event of a security exception. This data can be misused, for example, to build a profile of a user's browsing habits. Such logging may violate legal restrictions in many countries.

When the log cannot contain IP addresses, it should not contain any information about a SecurityException, because it might leak an IP address. When an exception contains sensitive information, the custom MyExceptionReporter class should extract or cleanse it before returning control to the next statement in the catch block (see ERR00-J. Do not suppress or ignore checked exceptions).
*/


public void logRemoteIPAddress(String name) {
  Logger logger = Logger.getLogger("com.organization.Log");
  InetAddress machine = null;
  try {
    machine = InetAddress.getByName(name);
  } catch (UnknownHostException e) {
    Exception e = MyExceptionReporter.handle(e);
  } catch (SecurityException e) {
    Exception e = MyExceptionReporter.handle(e);
    logger.severe(name + "," + machine.getHostAddress() + "," +
                  e.toString());
  }
}
/*
Noncompliant Code Example
Log messages with sensitive information should not be printed to the console display for security reasons (a possible example of sensitive information is passenger age). The java.util.logging.Logger class supports different logging levels that can be used for classifying such information: FINEST, FINER, FINE, CONFIG, INFO, WARNING, and SEVERE. By default, the INFO, WARNING, and SEVERE levels print the message to the console, which is accessible by end users and system administrators.

If we assume that the passenger age can appear in log files on the current system but not on the console display, this code example is noncompliant.
*/


logger.info("Age: " + passengerAge);