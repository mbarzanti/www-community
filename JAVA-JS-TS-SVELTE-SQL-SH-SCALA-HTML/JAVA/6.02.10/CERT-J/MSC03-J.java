/*
Noncompliant Code Example
This noncompliant code example includes a hard-coded server IP address in a constant String:
A malicious user can use the javap -c IPaddress command to disassemble the class and discover the hard-coded server IP address
*/


class IPaddress {
  String ipAddress = new String("172.16.254.1");
  public static void main(String[] args) {
    //...
  }
}
/*
Noncompliant Code Example (Hard-Coded Database Password)
The user name and password fields in the SQL connection request are hard coded in this noncompliant code example:
Note that the one- and two-argument java.sql.DriverManager.getConnection() methods can also be used incorrectly.
*/

public final Connection getConnection() throws SQLException {
  return DriverManager.getConnection(
      "jdbc:mysql://localhost/dbName", 
      "username", "password");
}
