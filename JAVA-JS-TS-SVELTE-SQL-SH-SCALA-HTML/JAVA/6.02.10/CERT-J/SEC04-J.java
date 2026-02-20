/*
Noncompliant Code Example
This noncompliant code example instantiates a Hashtable and defines a removeEntry() method to allow the removal of its entries. This method is considered sensitive, perhaps because the hash table contains sensitive information. However, the method is public and nonfinal, which leaves it exposed to malicious callers.

*/

class SensitiveHash {
  private Hashtable<Integer,String> ht = new Hashtable<Integer,String>();

  public void removeEntry(Object key) {
    ht.remove(key);
  }
}

/*
Noncompliant Code Example (check*())
This noncompliant code example uses the SecurityManager.checkRead() method to check whether the file schema.dtd can be read from the file system. The check*() methods lack support for fine-grained access control. For example, the check*() methods are inadequate to enforce a policy permitting read access to all files with the dtd extension and forbidding read access to all other files. Code that is not itself part of the JDK must not override the check*() methods because the default implementations of the Java libraries already use these methods to protect sensitive operations.
*/


SecurityManager sm = System.getSecurityManager();

if (sm != null) {  // Check whether file may be read
  sm.checkRead("/local/schema.dtd");
}