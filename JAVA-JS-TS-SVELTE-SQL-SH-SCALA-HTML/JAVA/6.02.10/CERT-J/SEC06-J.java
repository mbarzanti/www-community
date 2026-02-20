/*
Noncompliant Code Example
This noncompliant code example demonstrates the JarRunner application, which can be used to dynamically execute a particular class residing within a JAR file (abridged version of the class in The Java Tutorials [Java Tutorials]). It creates a JarClassLoader that loads an application update, plug-in, or patch over an untrusted network such as the Internet. The URL to fetch the code is specified as the first argument (for example, http://www.securecoding.cert.org/software-updates.jar); any other arguments specify the arguments that are to be passed to the class that is loaded. JarRunner uses reflection to invoke the main() method of the loaded class. Unfortunately, by default, JarClassLoader verifies the signature using the public key contained within the JAR file.
*/


public class JarRunner {
  public static void main(String[] args)
       throws IOException, ClassNotFoundException,
              NoSuchMethodException, InvocationTargetException {
  
    URL url = new URL(args[0]);
    
    // Create the class loader for the application jar file
    JarClassLoader cl = new JarClassLoader(url);
    
    // Get the application's main class name
    String name = cl.getMainClassName();
    
    // Get arguments for the application
    String[] newArgs = new String[args.length - 1];
    System.arraycopy(args, 1, newArgs, 0, newArgs.length);
    
    // Invoke application's main class
    cl.invokeClass(name, newArgs);
  }
}

final class JarClassLoader extends URLClassLoader {
  private URL url;
  public JarClassLoader(URL url) {
    super(new URL[] { url });
    this.url = url;
  }

  public String getMainClassName() throws IOException {
    URL u = new URL("jar", "", url + "!/");
    JarURLConnection uc = (JarURLConnection) u.openConnection();
    Attributes attr = uc.getMainAttributes();
    return attr != null ? 
        attr.getValue(Attributes.Name.MAIN_CLASS) : null;
  }

  public void invokeClass(String name, String[] args)
      throws ClassNotFoundException, NoSuchMethodException,
             InvocationTargetException {
    Class c = loadClass(name);
    Method m = c.getMethod("main", new Class[] { args.getClass() });
    m.setAccessible(true);
    int mods = m.getModifiers();
    if (m.getReturnType() != void.class || !Modifier.isStatic(mods) ||
        !Modifier.isPublic(mods)) {
      throw new NoSuchMethodException("main");
    }
    try {
      m.invoke(null, new Object[] { args });
    } catch (IllegalAccessException e) {
      System.out.println("Access denied");
    }
  }
}
