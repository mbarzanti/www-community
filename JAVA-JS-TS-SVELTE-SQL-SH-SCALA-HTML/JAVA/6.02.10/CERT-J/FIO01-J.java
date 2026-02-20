/*
Noncompliant Code Example
The constructors for FileOutputStream and FileWriter do not allow the programmer to explicitly specify file access permissions. In this noncompliant code example, the access permissions of any file created are implementation-defined and may not prevent unauthorized access:
*/


Writer out = new FileWriter("file");