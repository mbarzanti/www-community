/*
Noncompliant Code Example (Tomcat)
This noncompliant code example shows a vulnerability present in several versions of the Tomcat HTTP web server (fixed in version 6.0.20) that allows untrusted web applications to override the default XML parser used by the system to process web.xml, context.xml and tag library descriptor (TLD) files of other web applications deployed on the Tomcat instance. Consequently, untrusted web applications that install a parser could view and/or alter these files under certain circumstances.

The noncompliant code example shows the code associated with initialization of a new Digester instance in the org.apache.catalina.startup.ContextConfig class. "A Digester processes an XML input stream by matching a series of element nesting patterns to execute Rules that have been added prior to the start of parsing" [Tomcat 2009]. The code to initialize the Digester follows:

The useContextClassLoader flag is used by Digester to decide which ClassLoader to use when loading new classes. When true, it uses the WebappClassLoader, which is untrusted because it loads whatever classes are requested by various web applications.
*/


protected static Digester webDigester = null;

if (webDigester == null) {
  webDigester = createWebDigester();
}
The createWebDigester() method is responsible for creating the Digester. This method calls createWebXMLDigester(), which invokes the method DigesterFactory.newDigester(). This method creates the new digester instance and sets a boolean flag useContextClassLoader to true.



// This method exists in the class DigesterFactory and is called by 
// ContextConfig.createWebXmlDigester().
// which is in turn called by ContextConfig.createWebDigester()
// webDigester finally contains the value of digester defined
// in this method.
public static Digester newDigester(boolean xmlValidation,
                                   boolean xmlNamespaceAware,
                                   RuleSet rule) {
  Digester digester = new Digester();
  // ...
  digester.setUseContextClassLoader(true);
  // ...
  return digester;
}




public ClassLoader getClassLoader() {
  // ...
  if (this.useContextClassLoader) {
    // Uses the context class loader which was previously set
    // to the WebappClassLoader
    ClassLoader classLoader =
        Thread.currentThread().getContextClassLoader();
  }
  return classloader;
}
The Digester.getParser() method is subsequently called by Tomcat to process web.xml and other files:



// Digester.getParser() calls this method. It is defined in class Digester
public SAXParserFactory getFactory() {
  if (factory == null) {
    factory = SAXParserFactory.newInstance(); // Uses WebappClassLoader
    // ...
  }
  return (factory);
}
/
The underlying problem is that the newInstance() method is being invoked on behalf of a web application's class loader, the WebappClassLoader, and it loads classes before Tomcat has loaded all the classes it needs. If a web application has loaded its own Trojan javax.xml.parsers.SAXParserFactory, when Tomcat tries to access a SAXParserFactory, it accesses the Trojan SaxParserFactory installed by the web application rather than the standard Java SAXParserFactory that Tomcat depends on.

Note that the Class.newInstance() method requires the class to contain a no-argument constructor. If this requirement is not satisfied, a runtime exception results, which indirectly prevents a security breach.
*/