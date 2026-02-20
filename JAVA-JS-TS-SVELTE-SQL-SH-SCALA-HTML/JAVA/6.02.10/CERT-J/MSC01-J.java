/*
Noncompliant Code Example
This noncompliant code example implements an idle task that continuously executes a loop without executing any instructions within the loop. An optimizing compiler or JIT could remove the while loop in this example.
*/


public int nop() {
  while (true) {}
}