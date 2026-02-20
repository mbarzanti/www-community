/*
Noncompliant Code Example (Security Policy File)
This noncompliant example grants AllPermission to the klib library:
The permission itself is specified in the security policy file used by the security manager. Program code can obtain a permission object by subclassing the java.security.Permission class or any of its subclasses (BasicPermission, for example). The code can use the resulting object to grant AllPermission to a ProtectionDomain.
*/


// Grant the klib library AllPermission  
grant codebase "file:${klib.home}/j2se/home/klib.jar" { 
  permission java.security.AllPermission; 
}; 
/*
Noncompliant Code Example (PermissionCollection)
This noncompliant code example shows an overridden getPermissions() method, defined in a custom class loader. It grants java.lang.ReflectPermission with target suppressAccessChecks to any class that it loads.
*/


protected PermissionCollection getPermissions(CodeSource cs) {
  PermissionCollection pc = super.getPermissions(cs);
  pc.add(new ReflectPermission("suppressAccessChecks"));   // Permission to create a class loader
  // Other permissions
  return pc;
}