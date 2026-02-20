/*
Noncompliant Code Example
This noncompliant code example is taken from JDK v1.4.2 [FT 2008]. It declares a function table containing a public static field.
An attacker can replace the function table as follows:

FunctionTable.m_functions = new_table;

Replacing the function table gives the attacker access to XPathContext, which is used to set the reference node for evaluating XPath expressions. Manipulating XPathContext can cause XML fields to be modified in inconsistent ways, resulting in unexpected behavior. Also, because static variables are global across the Java Runtime Environment (JRE), they can be used as a covert communication channel between different application domains (for example, through code loaded by different class loaders).

This vulnerability was repaired in JDK v1.4.2_05.
*/

package org.apache.xpath.compiler;

public class FunctionTable {
  public static FuncLoader m_functions;
}
/*
Noncompliant Code Example (serialVersionUID)
This noncompliant code example uses a public static nonfinal serialVersionUID field in a class designed for serialization:

*/

class DataSerializer implements Serializable {
  public static long serialVersionUID = 1973473122623778747L;
  // ...
}