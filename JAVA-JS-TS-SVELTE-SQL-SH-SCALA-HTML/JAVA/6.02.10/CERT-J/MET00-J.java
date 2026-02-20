/*
Noncompliant Code Example
In this noncompliant code example, setState() and useState() fail to validate their arguments. A malicious caller could pass an invalid state to the library, consequently corrupting the library and exposing a vulnerability.
Such vulnerabilities are particularly severe when the internal state contains or refers to sensitive or system-critical data.
*/

private Object myState = null;

// Sets some internal state in the library
void setState(Object state) {
  myState = state;
}

// Performs some action using the state passed earlier
void useState() {
  // Perform some action here
}

