/*
Noncompliant Code Example
This noncompliant code example allows any caller to reset the value of the object at any time because the readExternal() method is necessarily declared to be public and lacks protection against hostile callers:
*/


public void readExternal(ObjectInput in) 
                         throws IOException, ClassNotFoundException {
   // Read instance fields
   this.name = (String) in.readObject();
   this.UID = in.readInt();
   // ...
}