/*
Noncompliant Code Example
This noncompliant code example shows a fragment of a custom class loader that extends the class URLClassLoader. It overrides the getPermissions() method but does not call its superclass's more restrictive getPermissions() method. Consequently, a class defined using this custom class loader has permissions that are completely independent of those specified in the systemwide policy file. In effect, the class's permissions override them.

*/

protected PermissionCollection getPermissions(CodeSource cs) {
  PermissionCollection pc = new Permissions();
  // Allow exit from the VM anytime
  pc.add(new RuntimePermission("exitVM"));
  return pc;
}