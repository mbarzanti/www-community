/*
Noncompliant Code Example
Suppose that SomeType is immutable.
Even though SomeType is immutable, this declaration allows the SOMETHINGS array to be modified by untrusted clients of the code. Any element of the array can be assigned a new value, namely a reference to a new SomeType object.

This noncompliant code example also violates OBJ01-J. Limit accessibility of fields.
*/
public static final SomeType [] SOMETHINGS = { ... };

/*
Noncompliant Code Example (getter method)
This noncompliant code example complies with OBJ01-J. Limit accessibility of fields by declaring the array private. But, in declaring the array private, this code example violates OBJ05-J. Do not return references to private mutable class members.

Suppose that SomeType is immutable.
Even though SomeType is immutable, the public getter method enables untrusted clients to modify the SOMETHINGS array. Any element of the array can be assigned a new value, namely a reference to a new SomeType object.
*/
private static final SomeType [] SOMETHINGS = { ... };
public static final getSomethings() {return SOMETHINGS;} 
